package app.hyuabot.backend.dto.request.shuttle

data class PatchRouteStopRequest (
    val seq: Int? = null,
    var cumulativeTime: String? = null,
)