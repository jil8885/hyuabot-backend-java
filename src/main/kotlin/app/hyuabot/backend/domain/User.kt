package app.hyuabot.backend.domain

import app.hyuabot.backend.api.user.request.SignUpRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "admin_user")
data class User (
    @Id
    @Column(name = "user_id", unique = true)
    val userID: String,
    @Column(name = "password")
    val password: String,
    @Column(name = "name")
    val name: String,
    @Column(name = "email", unique = true)
    val email: String,
    @Column(name = "phone")
    val phone: String,
    @Column(name = "active")
    val active: Boolean,
) {
    constructor() : this("", "", "", "", "", false)
    companion object {
        fun registerUser(newUser: SignUpRequest): User {
            return User(
                userID = newUser.username,
                password = newUser.password,
                name = newUser.name,
                email = newUser.email,
                phone = newUser.phone,
                active = false,
            )
        }
    }
}
