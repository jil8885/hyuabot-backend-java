package app.hyuabot.backend.service

import app.hyuabot.backend.dto.response.TokenResponse
import app.hyuabot.backend.security.JWTTokenProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional(readOnly = true)
class AuthService (
    private val jwtTokenProvider: JWTTokenProvider,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val redisService: RedisService,
) {
    companion object {
        const val SERVER = "server"
    }

    @Transactional
    fun login(username: String, password: String): TokenResponse {
        val authenticationToken = authenticationManagerBuilder.getObject().authenticate(
            UsernamePasswordAuthenticationToken(username, password)
        )
        SecurityContextHolder.getContext().authentication = authenticationToken
        return generateToken(SERVER, authenticationToken.name, getAuthorities(authenticationToken))
    }

    fun validate(accessToken: String): Boolean {
        val token = resolveToken(accessToken)
        return jwtTokenProvider.validateAccessTokenOnlyExpired(token!!)
    }

    @Transactional
    fun reissue(requestAccessTokenInHeader: String, refreshToken: String): TokenResponse? {
        val accessToken = resolveToken(requestAccessTokenInHeader)
        val authentication = jwtTokenProvider.getAuthentication(accessToken!!)
        val principal = getPrincipal(accessToken)

        val refreshInRedis = redisService.getValues("RT:$SERVER:$principal") ?: return null

        if (!jwtTokenProvider.validateRefreshToken(refreshToken) || refreshToken != refreshInRedis) {
            redisService.deleteValues("RT:$SERVER:$principal")
            return null
        }

        SecurityContextHolder.getContext().authentication = authentication
        val authorities = getAuthorities(authentication)
        val response = jwtTokenProvider.generateToken(principal, authorities)
        saveRefreshToken(SERVER, principal, response.refreshToken)
        return response
    }

    @Transactional
    fun generateToken(provider: String, username: String, authorities: String): TokenResponse {
        if (redisService.getValues("RT:$provider:$username") != null) {
            redisService.deleteValues("RT:$provider:$username")
        }
        val response = jwtTokenProvider.generateToken(username, authorities)
        saveRefreshToken(provider, username, response.refreshToken)
        return response
    }

    @Transactional
    fun saveRefreshToken(provider: String, principal: String, refreshToken: String) {
        redisService.setValuesWithTimeout(
            "RT:$provider:$principal",
            refreshToken,
            jwtTokenProvider.getTokenExpiration(refreshToken)
        )
    }

    fun getAuthorities(authentication: Authentication): String {
        return authentication.authorities
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(java.util.stream.Collectors.joining(",")) ?: ""
    }

    fun getPrincipal(accessToken: String): String {
        return jwtTokenProvider.getAuthentication(accessToken).name
    }

    fun resolveToken(accessToken: String?): String? {
        return if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken.substring(7)
        } else {
            null
        }
    }

    @Transactional
    fun logout(accessToken: String) {
        val token = resolveToken(accessToken)
        val principal = getPrincipal(token!!)

        val refreshToken = redisService.getValues("RT:$SERVER:$principal")
        if (refreshToken != null) {
            redisService.deleteValues("RT:$SERVER:$principal")
        }
        val expiration = jwtTokenProvider.getTokenExpiration(token) - System.currentTimeMillis()
        redisService.setValuesWithTimeout(accessToken, "logout", expiration)
    }
}