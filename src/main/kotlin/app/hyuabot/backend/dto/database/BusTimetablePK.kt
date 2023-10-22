package app.hyuabot.backend.dto.database

import java.io.Serializable
import java.time.LocalTime

data class BusTimetablePK (
    val routeID: Int = 0,
    val startStopID: Int = 0,
    val departureTime: LocalTime = LocalTime.now(),
    val weekdays: String = "",
) : Serializable
