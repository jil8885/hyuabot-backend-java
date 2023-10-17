package app.hyuabot.backend.dto.request.shuttle

data class PatchTimetableRequest (
    val periodType: String?,
    val isWeekday: Boolean?,
    val routeName: String?,
    val departureTime: String?,
)