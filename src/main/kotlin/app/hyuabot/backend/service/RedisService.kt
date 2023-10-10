package app.hyuabot.backend.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional(readOnly = true)
class RedisService(private val redisTemplate: RedisTemplate<String, String>) {
    @Transactional
    fun setValues(key: String, value: String) {
        redisTemplate.opsForValue().set(key, value)
    }

    @Transactional
    fun setValuesWithTimeout(key: String, value: String, timeout: Long) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS)
    }

    fun getValues(key: String): String? {
        return redisTemplate.opsForValue().get(key)
    }

    @Transactional
    fun deleteValues(key: String) {
        redisTemplate.delete(key)
    }
}