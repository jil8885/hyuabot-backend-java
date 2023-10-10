package app.hyuabot.backend.api.user

import app.hyuabot.backend.api.Response
import app.hyuabot.backend.api.user.request.SignUpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
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
        const val COOKIE_EXPIRE_TIME = 60 * 60 * 24 * 90
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
}