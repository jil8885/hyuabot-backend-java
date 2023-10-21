package app.hyuabot.backend.dto.request.shuttle

data class PostTimetableRequest (
    val seq: Int,
    val periodType: String,
    val isWeekday: Boolean,
    val routeName: String,
    val departureTime: String,
)