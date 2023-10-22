package app.hyuabot.backend.dto.database

import java.io.Serializable

data class BusRouteStopPK (
    val routeID: Int = 0,
    val stopID: Int = 0,
) : Serializable
