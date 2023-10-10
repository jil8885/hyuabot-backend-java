package app.hyuabot.backend.security

import io.jsonwebtoken.IncorrectClaimException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.filter.OncePerRequestFilter

class JWTAuthenticationFilter(private val jwtTokenProvider: JWTTokenProvider): OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val accessToken = resolveAccessToken(request)
        try {
            if (accessToken != null && jwtTokenProvider.validateAccessToken(accessToken)) {
                val authentication = jwtTokenProvider.getAuthentication(accessToken)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: IncorrectClaimException) {
            SecurityContextHolder.clearContext()
            response.sendError(403, "INVALID_ACCESS_TOKEN")
        } catch (e: UsernameNotFoundException) {
            SecurityContextHolder.clearContext()
            response.sendError(403, "USER_NOT_FOUND")
        }
        filterChain.doFilter(request, response)
    }

    private fun resolveAccessToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else {
            null
        }
    }
}