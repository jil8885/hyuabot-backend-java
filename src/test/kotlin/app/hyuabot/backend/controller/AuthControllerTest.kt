package app.hyuabot.backend.controller

import app.hyuabot.backend.dto.request.LoginRequest
import app.hyuabot.backend.dto.request.SignUpRequest
import app.hyuabot.backend.dto.response.Response
import app.hyuabot.backend.repository.UserRepository
import app.hyuabot.backend.service.UserService
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired lateinit var userService: UserService
    @Autowired lateinit var userRepository: UserRepository
    @Autowired lateinit var mockmvc: MockMvc

    @BeforeEach
    fun init() {
        // Create test user
        userService.signUp(
            SignUpRequest(
            "test",
            BCryptPasswordEncoder().encode("test"),
            "test",
            "test@email.com",
            "010-0000-0000",
        ))
        userRepository.findByUserID("test")?.apply {
            active = true
            userRepository.save(this)
        }
    }

    @AfterEach
    fun clean() {
        // Delete test user
        userRepository.deleteById("test")
    }

    @Test
    @DisplayName("SIGN_UP_SUCCESS")
    fun testSignUp() {
        val previousCount = userRepository.count()
        val request = SignUpRequest(
            "test2",
            "test2",
            "test2",
            "test2@email.com",
            "010-0000-0000",
        )
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(request))
        )
            .andDo(print())
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("SUCCESS_TO_SIGN_UP"))
        assertEquals(previousCount + 1, userRepository.count())
        userRepository.deleteById("test2")
    }

    @Test
    @DisplayName("SIGN_UP_FAIL_DUPLICATED_USER_ID")
    fun testSignUpFail() {
        val previousCount = userRepository.count()
        val request = SignUpRequest(
            "test",
            "test",
            "test",
            "test@email.com",
            "010-0000-0000",
        )
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(request))
        )
            .andDo(print())
            .andExpect(status().isConflict)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("DUPLICATED_USER_ID"))
            .andExpect(jsonPath("$.path").value("/api/auth/signup"))
            .andExpect(jsonPath("$.timestamp").exists())
        assertEquals(previousCount, userRepository.count())
    }

    @Test
    @DisplayName("LOGIN_SUCCESS")
    fun testLogin() {
        val request = LoginRequest("test", "test")
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(request))
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(header().exists("Authorization"))
            .andExpect(header().exists("Set-Cookie"))
            .andExpect(cookie().exists("refresh-token"))
    }

    @Test
    @DisplayName("LOGIN_FAIL_INVALID_USERNAME_OR_PASSWORD")
    fun testLoginFail() {
        val request = LoginRequest("test", "test2")
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(request))
        )
            .andDo(print())
            .andExpect(status().isUnauthorized)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("INVALID_USERNAME_OR_PASSWORD"))
            .andExpect(jsonPath("$.path").value("/api/auth/login"))
            .andExpect(jsonPath("$.timestamp").exists())
    }

    @Test
    @DisplayName("VALIDATE_ACCESS_TOKEN_SUCCESS")
    fun testValidToken() {
        val request = LoginRequest("test", "test")
        val response = mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(request))
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(header().exists("Authorization"))
            .andExpect(header().exists("Set-Cookie"))
            .andExpect(cookie().exists("refresh-token"))
            .andReturn()
        val accessToken = response.response.getHeader("Authorization")
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/validate")
                .header("Authorization", accessToken)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("VALID_ACCESS_TOKEN"))
    }

    @Test
    @DisplayName("VALIDATE_ACCESS_TOKEN_FAIL_INVALID_ACCESS_TOKEN")
    fun testInvalidToken() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/validate")
                .header("Authorization", "")
                .cookie(Cookie("refresh-token", ""))
        )
            .andDo(print())
            .andExpect(status().isUnauthorized)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message").value("INVALID_ACCESS_TOKEN"))
            .andExpect(jsonPath("$.path").value("/api/auth/validate"))
            .andExpect(jsonPath("$.timestamp").exists())
    }

    @Test
    @DisplayName("REISSUE_ACCESS_TOKEN_SUCCESS")
    fun testReissueToken() {
                val request = LoginRequest("test", "test")
        val response = mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(request))
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(header().exists("Authorization"))
            .andExpect(header().exists("Set-Cookie"))
            .andExpect(cookie().exists("refresh-token"))
            .andReturn()
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/reissue")
                .header("Authorization", response.response.getHeader("Authorization"))
                .cookie(response.response.getCookie("refresh-token")!!)
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(header().exists("Authorization"))
            .andExpect(header().exists("Set-Cookie"))
            .andExpect(cookie().exists("refresh-token"))
    }

    @Test
    @DisplayName("REISSUE_ACCESS_TOKEN_FAIL_INVALID_TOKEN")
    fun testReissueTokenFail() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/reissue")
                .header("Authorization", "")
                .cookie(Cookie("refresh-token", ""))
        )
            .andDo(print())
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("LOGOUT_SUCCESS")
    fun testLogout() {
        val request = LoginRequest("test", "test")
        val response = mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(request))
        )
            .andDo(print())
            .andExpect(status().isOk)
            .andExpect(header().exists("Authorization"))
            .andExpect(header().exists("Set-Cookie"))
            .andExpect(cookie().exists("refresh-token"))
            .andReturn()
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/logout")
                .header("Authorization", response.response.getHeader("Authorization"))
                .cookie(response.response.getCookie("refresh-token")!!)
        )
            .andDo(print())
            .andExpect(status().isOk)
    }

    @Test
    @DisplayName("LOGOUT_FAIL_INVALID_TOKEN")
    fun testLogoutFail() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/logout")
                .header("Authorization", "")
        )
            .andDo(print())
            .andExpect(status().isUnauthorized)
    }
}