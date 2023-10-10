package app.hyuabot.backend.api.user.request

data class LoginRequest (
    val userID: String,
    val password: String,
)
