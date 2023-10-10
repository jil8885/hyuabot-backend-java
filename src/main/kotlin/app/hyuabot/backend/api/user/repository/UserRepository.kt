package app.hyuabot.backend.api.user.repository

import app.hyuabot.backend.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, String> {
    fun findByUserID(userID: String): User?
}
