package app.hyuabot.backend.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class RedisServiceTest {
    @Autowired
    private lateinit var redisService: RedisService

    @Test
    fun testSetValues() {
        redisService.setValues("test", "test")
        assertTrue(redisService.getValues("test") == "test")
    }

    @Test
    fun testSetValuesWithTimeout() {
        redisService.setValuesWithTimeout("test", "test", 1000)
        Thread.sleep(1000)
        assertTrue(redisService.getValues("test") == null)
    }

    @Test
    fun testGetValues() {
        redisService.setValues("test", "test")
        assertTrue(redisService.getValues("test") == "test")
    }

    @Test
    fun testDeleteValues() {
        redisService.setValues("test", "test")
        assertTrue(redisService.getValues("test") == "test")
        redisService.deleteValues("test")
        assertTrue(redisService.getValues("test") == null)
    }
}