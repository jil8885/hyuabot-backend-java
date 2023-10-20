package app.hyuabot.backend.service

import app.hyuabot.backend.dto.request.SignUpRequest
import app.hyuabot.backend.repository.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

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
            )
        )
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
    fun testSignUp() {
        // Create test user
        userService.signUp(
            SignUpRequest(
                "test2",
                BCryptPasswordEncoder().encode("test2"),
                "test2",
                "test@email.com",
                "010-0000-0000",
            )
        )
        assertTrue(userRepository.findByUserID("test2") != null)
        userRepository.deleteById("test2")
        // Create duplicated user
        assertTrue(
            assertThrows<Exception> {
                userService.signUp(
                    SignUpRequest(
                        "test",
                        BCryptPasswordEncoder().encode("test"),
                        "test",
                        "test@email.com",
                        "010-0000-0000",
                    )
                )
            }.message == "DUPLICATED_USER_ID"
        )
    }

    @Test
    fun testCheckDuplicate() {
        assertTrue(userService.checkDuplicate("test"))
        assertTrue(!userService.checkDuplicate("test2"))
    }
}