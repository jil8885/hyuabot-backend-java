package app.hyuabot.backend.dto.response

data class ShuttleTimetableViewItem (
    val seq: Int,
    val periodType: String,
    val stopName: String,
    val isWeekdays: Boolean,
    val routeName: String,
    val routeType: String,
    val departureTime: String,
)