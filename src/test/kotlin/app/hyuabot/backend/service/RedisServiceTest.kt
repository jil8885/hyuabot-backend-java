package app.hyuabot.backend.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class RedisServiceTest {
    @Autowired
    private lateinit var redisService: RedisService

    @Test
    @DisplayName("SET_VALUES")
    fun testSetValues() {
        redisService.setValues("test", "test")
        assertTrue(redisService.getValues("test") == "test")
    }

    @Test
    @DisplayName("SET_VALUES_WITH_TIMEOUT")
    fun testSetValuesWithTimeout() {
        redisService.setValuesWithTimeout("test", "test", 1000)
        Thread.sleep(1000)
        assertTrue(redisService.getValues("test") == null)
    }

    @Test
    @DisplayName("GET_VALUES")
    fun testGetValues() {
        redisService.setValues("test", "test")
        assertTrue(redisService.getValues("test") == "test")
    }

    @Test
    @DisplayName("DELETE_VALUES")
    fun testDeleteValues() {
        redisService.setValues("test", "test")
        assertTrue(redisService.getValues("test") == "test")
        redisService.deleteValues("test")
        assertTrue(redisService.getValues("test") == null)
    }
}