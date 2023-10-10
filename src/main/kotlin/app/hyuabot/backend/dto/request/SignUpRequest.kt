package app.hyuabot.backend.dto.request

data class SignUpRequest (
    val username: String,
    val password: String,
    val name: String,
    val email: String,
    val phone: String,
)