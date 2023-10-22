package app.hyuabot.backend.repository.admin

import app.hyuabot.backend.domain.admin.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, String> {
    fun findByUserID(userID: String): User?
}
