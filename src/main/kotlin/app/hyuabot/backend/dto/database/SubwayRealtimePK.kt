package app.hyuabot.backend.dto.database

import java.io.Serializable

data class SubwayRealtimePK (
    val stationID: String = "",
    val heading: String = "",
    val seq: Int = 0,
) : Serializable
