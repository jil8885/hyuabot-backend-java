package app.hyuabot.backend.service

import app.hyuabot.backend.repository.UserRepository
import app.hyuabot.backend.dto.request.SignUpRequest
import app.hyuabot.backend.domain.admin.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(private val userRepository: UserRepository) {
    @Transactional
    fun signUp(newUser: SignUpRequest) {
        val user = User.registerUser(newUser)
        if (checkDuplicate(user.userID)) {
            throw Exception("DUPLICATED_USER_ID")
        }
        userRepository.save(user)
    }

    fun checkDuplicate(userID: String): Boolean {
        return userRepository.findByUserID(userID) != null
    }
}