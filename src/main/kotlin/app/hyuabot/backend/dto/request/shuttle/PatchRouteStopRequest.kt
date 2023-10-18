package app.hyuabot.backend.dto.request.shuttle

data class PatchRouteStopRequest (
    val seq: Int?,
    var cumulativeTime: String?,
)