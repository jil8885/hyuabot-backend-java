package app.hyuabot.backend.dto.response

data class ShuttleTimetableItem (
    val seq: Int,
    val periodType: String,
    val isWeekdays: Boolean,
    val routeName: String,
    val departureTime: String,
)