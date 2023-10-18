package app.hyuabot.backend.dto.request.shuttle

data class PatchPeriodRequest (
    val period: String?,
    val start: String?,
    val end: String?,
)