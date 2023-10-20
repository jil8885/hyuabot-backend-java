package app.hyuabot.backend.service

import app.hyuabot.backend.dto.request.SignUpRequest
import app.hyuabot.backend.repository.UserRepository
import app.hyuabot.backend.security.JWTTokenProvider
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import java.lang.NullPointerException

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {
    @Autowired
    private lateinit var authService: AuthService

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var redisService: RedisService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var jwtTokenProvider: JWTTokenProvider

    @BeforeEach
    fun init() {
        // Create test user
        userService.signUp(SignUpRequest(
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
    fun testLogin() {
        // Successful case
        val token = authService.login("test", "test")
        assertTrue(token.accessToken != "")
        assertTrue(token.refreshToken != "")

        // Fail case
        assertThrows<BadCredentialsException> {
            authService.login("test", "test2")
        }
    }

    @Test
    fun testValidateToken() {
        // Successful case
        val token = authService.login("test", "test").accessToken
        assertFalse(authService.validate("Bearer $token"))
    }

    @Test
    fun testReissueToken() {
        // Successful case
        val token = authService.login("test", "test")
        val newToken = authService.reissue(
            "Bearer ${token.accessToken}",
            token.refreshToken
        )
        assertTrue(newToken != null)
        assertTrue(newToken!!.accessToken != "")
        assertTrue(newToken.refreshToken != "")

        // Fail case
        assertThrows<NullPointerException> {
            authService.reissue("test", "test")
        }
    }

    @Test
    fun testGenerateToken() {
        // Successful case
        val token = authService.generateToken(
            AuthService.SERVER,
            "test",
            "ROLE_USER"
        )
        assertTrue(token.accessToken != "")
        assertTrue(token.refreshToken != "")
        assertTrue(redisService.getValues("RT:${AuthService.SERVER}:test") != null)
    }

    @Test
    fun testSaveRefreshToken() {
        // Successful case
        val token = authService.login("test", "test")
        authService.saveRefreshToken(
            AuthService.SERVER,
            "test",
            token.refreshToken,
        )
        assertTrue(redisService.getValues("RT:${AuthService.SERVER}:test") != null)
    }

    @Test
    fun testGetAuthorities() {
        // Successful case
        val token = authService.login("test", "test")
        val authorities = authService.getAuthorities(
            jwtTokenProvider.getAuthentication(token.accessToken)
        )
        assertTrue(authorities != "")
    }

    @Test
    fun testGetPrincipal() {
        // Successful case
        val token = authService.login("test", "test")
        val principal = authService.getPrincipal(token.accessToken)
        assertTrue(principal != "")
    }

    @Test
    fun testResolveToken() {
        // Successful case
        val token = authService.login("test", "test")
        val resolvedToken = authService.resolveToken("Bearer ${token.accessToken}")
        assertTrue(resolvedToken == token.accessToken)
    }

    @Test
    fun testLogout() {
        // Successful case
        val token = authService.login("test", "test")
        authService.logout("Bearer ${token.accessToken}")
        assertTrue(redisService.getValues(token.accessToken) == null)
    }
}
