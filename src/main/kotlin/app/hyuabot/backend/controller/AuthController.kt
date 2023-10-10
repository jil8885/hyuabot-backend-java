package app.hyuabot.backend.controller

import app.hyuabot.backend.dto.response.Response
import app.hyuabot.backend.service.AuthService
import app.hyuabot.backend.service.UserService
import app.hyuabot.backend.dto.request.LoginRequest
import app.hyuabot.backend.dto.request.SignUpRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val userService: UserService,
    private val encoder: BCryptPasswordEncoder,
) {
    companion object {
        const val COOKIE_EXPIRE_TIME: Long = 60 * 60 * 24 * 90
    }

    @PostMapping(
        "/signup",
        consumes = ["application/json"],
        produces = ["application/json"],
    )
    fun signup(@RequestBody request: SignUpRequest): ResponseEntity<String> {
        val hashedPassword = encoder.encode(request.password)
        try {
            userService.signUp(request.copy(password = hashedPassword))
        } catch (e: Exception) {
            if (e.message == "DUPLICATED_USER_ID") {
                val response = Response.ErrorResponse(
                    message = "DUPLICATED_USER_ID",
                    path = "/api/auth/signup",
                )
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Response.objectMapper.writeValueAsString(response))
            }
            val response = Response.ErrorResponse(
                message = e.message ?: "ERROR_TO_SIGN_UP",
                path = "/api/auth/signup",
            )
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.objectMapper.writeValueAsString(response))
        }
        val response = Response.SuccessResponse(
            message = "SUCCESS_TO_SIGN_UP",
        )
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(Response.objectMapper.writeValueAsString(response))
    }

    @PostMapping(
        "/login",
        consumes = ["application/json"],
        produces = ["application/json"],
    )
    fun login(@RequestBody request: LoginRequest): ResponseEntity<String> {
        val response = authService.login(request.username, request.password)
        val cookie = ResponseCookie.from("refresh-token", response.refreshToken)
            .httpOnly(true)
            .maxAge(COOKIE_EXPIRE_TIME)
            .path("/")
            .build()
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${response.accessToken}")
            .build()
    }

    @PostMapping("/validate", produces = ["application/json"])
    fun validate(@RequestHeader("Authorization") accessToken: String): ResponseEntity<String> {
        if (!authService.validate(accessToken)) {
            val response = Response.SuccessResponse(
                message = "VALID_ACCESS_TOKEN",
            )
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(Response.objectMapper.writeValueAsString(response))
        }
        val response = Response.ErrorResponse(
            message = "INVALID_ACCESS_TOKEN",
            path = "/api/auth/validate",
        )
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(Response.objectMapper.writeValueAsString(response))
    }

    @PostMapping("/reissue")
    fun reissue(@CookieValue("refresh-token") refreshToken: String, @RequestHeader("Authorization") accessToken: String): ResponseEntity<Void> {
        val newToken = authService.reissue(accessToken, refreshToken)
        if (newToken != null) {
            val cookie = ResponseCookie.from("refresh-token", newToken.refreshToken)
                .httpOnly(true)
                .secure(true)
                .maxAge(COOKIE_EXPIRE_TIME)
                .path("/")
                .build()
            return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${newToken.accessToken}")
                .build()
        } else {
            val cookie = ResponseCookie.from("refresh-token", "")
                .maxAge(0)
                .path("/")
                .build()
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build()
        }
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader("Authorization") accessToken: String): ResponseEntity<Void> {
        authService.logout(accessToken)
        val cookie = ResponseCookie.from("refresh-token", "")
            .maxAge(0)
            .path("/")
            .build()
        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build()
    }
}