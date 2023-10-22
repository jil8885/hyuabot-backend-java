package app.hyuabot.backend.dto.database

import java.io.Serializable

data class CommuteShuttleTimetablePK (
    val stopName: String = "",
    val routeName: String = "",
) : Serializable
