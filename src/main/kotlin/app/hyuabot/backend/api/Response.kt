package app.hyuabot.backend.api

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDateTime

object Response {
    val objectMapper = ObjectMapper()

    data class ErrorResponse (
            val message: String,
            val path: String,
            val timestamp: String,
    ) {
        constructor(message: String, path: String) : this(message, path, LocalDateTime.now().toString())
    }

    data class SuccessResponse (
            val message: String,
            val timestamp: String,
    ) {
        constructor(message: String) : this(message, LocalDateTime.now().toString())
    }
}