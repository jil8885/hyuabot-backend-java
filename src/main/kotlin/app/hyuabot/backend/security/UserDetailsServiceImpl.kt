package app.hyuabot.backend.security

import app.hyuabot.backend.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class UserDetailsServiceImpl(private val userRepository: UserRepository): UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = userRepository.findByUserID(username!!)
        if (user != null && user.active) {
            return UserDetailsImpl(user)
        } else {
            throw UsernameNotFoundException("USER_NOT_FOUND")
        }
    }
}