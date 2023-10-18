package app.hyuabot.backend.dto.request.shuttle

data class PatchRouteRequest (
    val routeName: String?,
    val routeType: String?,
    val routeDescriptionKorean: String?,
    val routeDescriptionEnglish: String?,
    val startStop: String?,
    val endStop: String?,
)