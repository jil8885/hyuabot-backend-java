package app.hyuabot.backend.dto.request.shuttle

data class PatchRouteRequest (
    val routeType: String? = null,
    val routeDescriptionKorean: String? = null,
    val routeDescriptionEnglish: String? = null,
    val startStop: String? = null,
    val endStop: String? = null,
)