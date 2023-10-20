package app.hyuabot.backend.dto.request.shuttle

data class PatchTimetableRequest (
    val periodType: String? = null,
    val isWeekday: Boolean? = null,
    val routeName: String? = null,
    val departureTime: String? = null,
)