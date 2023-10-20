package app.hyuabot.backend.dto.database

import java.io.Serializable
import java.time.LocalDateTime

data class ShuttlePeriodPK(
    var periodType: String = "",
    var periodStart: LocalDateTime = LocalDateTime.now(),
    var periodEnd: LocalDateTime = LocalDateTime.now(),
): Serializable
