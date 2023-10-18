package app.hyuabot.backend.dto.database

import java.io.Serializable
import java.time.LocalDateTime

class ShuttlePeriodPK(
    val periodType: String,
    val periodStart: LocalDateTime,
    val periodEnd: LocalDateTime,
): Serializable
