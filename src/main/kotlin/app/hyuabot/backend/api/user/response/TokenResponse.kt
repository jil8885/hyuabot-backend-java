package app.hyuabot.backend.api.user.response

data class TokenResponse (
    val accessToken: String,
    val refreshToken: String,
)