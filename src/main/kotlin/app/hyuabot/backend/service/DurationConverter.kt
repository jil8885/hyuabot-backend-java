package app.hyuabot.backend.service

import jakarta.persistence.AttributeConverter
import java.time.Duration

class DurationConverter: AttributeConverter<Duration, String> {
    override fun convertToDatabaseColumn(attribute: Duration?): String {
        return attribute.toString()
    }

    override fun convertToEntityAttribute(dbData: String?): Duration {
        return Duration.parse(dbData)
    }
}