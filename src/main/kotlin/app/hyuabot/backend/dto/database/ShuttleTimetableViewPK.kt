package app.hyuabot.backend.dto.database

import java.io.Serializable

data class ShuttleTimetableViewPK(
    val stopName: String = "",
    val seq: Int = 0,
) : Serializable
