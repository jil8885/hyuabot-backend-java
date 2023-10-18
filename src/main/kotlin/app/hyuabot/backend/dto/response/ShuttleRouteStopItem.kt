package app.hyuabot.backend.dto.response

data class ShuttleRouteStopItem (
    val route: String,
    val stop: String,
    val order: Int,
    val cumulativeTime: String,
)