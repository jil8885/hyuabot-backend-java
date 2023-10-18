package app.hyuabot.backend.dto.database

import java.io.Serializable

class ShuttleRouteStopPK(
    val routeName: String,
    val stopName: String,
) : Serializable
