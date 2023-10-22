package app.hyuabot.backend.dto.database

import java.io.Serializable
import java.time.LocalTime

data class SubwayTimetablePK (
    val stationID: String,
    val heading: String,
    val weekdays: String,
    val departureTime: LocalTime,
) : Serializable
