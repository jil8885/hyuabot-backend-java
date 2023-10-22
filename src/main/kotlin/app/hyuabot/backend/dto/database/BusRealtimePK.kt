package app.hyuabot.backend.dto.database

import java.io.Serializable

data class BusRealtimePK (
    var stopID: Int = 0,
    var routeID: Int = 0,
    var seq: Int = 0,
): Serializable
