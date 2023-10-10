package app.hyuabot.backend.dto.response

data class TokenResponse (
    val accessToken: String,
    val refreshToken: String,
)