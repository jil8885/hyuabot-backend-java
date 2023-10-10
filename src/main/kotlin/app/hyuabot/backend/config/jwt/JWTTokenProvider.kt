package app.hyuabot.backend.config.jwt

import app.hyuabot.backend.api.user.response.TokenResponse
import app.hyuabot.backend.config.redis.RedisService
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.security.SignatureException
import java.util.*
import javax.crypto.SecretKey

@Component
@Transactional(readOnly = true)
class JWTTokenProvider(
    private val userDetailsService: UserDetailsService,
    private val redisService: RedisService,
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access-token-validity-in-seconds}") private val accessTokenValidityInSeconds: Long,
    @Value("\${jwt.refresh-token-validity-in-seconds}") private val refreshTokenValidityInSeconds: Long,
): InitializingBean {
    private val accessTokenValidityInMilliSeconds: Long = accessTokenValidityInSeconds * 1000
    private val refreshTokenValidityInMilliSeconds: Long = refreshTokenValidityInSeconds * 1000

    companion object {
        lateinit var signingKey: SecretKey
        const val AUTHORITIES_KEY = "auth"
        const val USERNAME_KEY = "username"
        const val URL = "https://admin.hyuabot.app"
    }

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        val secretKeyBytes = Decoders.BASE64.decode(secretKey)
        signingKey = Keys.hmacShaKeyFor(secretKeyBytes)
    }

    @Transactional
    fun generateToken(username: String, authorities: String): TokenResponse {
        val now = System.currentTimeMillis()
        val accessToken = Jwts.builder()
            .header()
            .add("typ", "JWT")
            .add("alg", "HS512")
            .and()
            .expiration(Date(now + accessTokenValidityInMilliSeconds))
            .claim(URL, true)
            .claim(USERNAME_KEY, username)
            .claim(AUTHORITIES_KEY, authorities)
            .subject("access-token")
            .signWith(signingKey)
            .compact()
        val refreshToken = Jwts.builder()
            .header()
            .add("typ", "JWT")
            .add("alg", "HS512")
            .and()
            .expiration(Date(now + refreshTokenValidityInMilliSeconds))
            .subject("refresh-token")
            .signWith(signingKey)
            .compact()
        return TokenResponse(accessToken, refreshToken)
    }

    fun getClaims(token: String): Claims {
        return try {
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)
        val username = claims[USERNAME_KEY].toString()
        val user = userDetailsService.loadUserByUsername(username)
        return UsernamePasswordAuthenticationToken(user, "", user.authorities)
    }

    fun getTokenExpiration(token: String): Long {
        return getClaims(token).expiration.time
    }

    fun validateRefreshToken(token: String): Boolean {
        try {
            if (redisService.getValues(token).equals("delete")) {
                return false
            }
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
            return true
        } catch (e: SignatureException) {
            return false
        } catch (e: ExpiredJwtException) {
            return false
        } catch (e: MalformedJwtException) {
            return false
        } catch (e: IllegalArgumentException) {
            return false
        } catch (e: UnsupportedJwtException) {
            return false
        } catch (e: Exception) {
            return false
        }
    }

    fun validateAccessToken(token: String): Boolean {
        try {
            if (redisService.getValues(token) != null && redisService.getValues(token).equals("logout")) {
                return false
            }
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
            return true
        } catch (e: ExpiredJwtException) {
            return false
        } catch (e: Exception) {
            return false
        }
    }

    fun validateAccessTokenOnlyExpired(token: String): Boolean {
        return try {
            getClaims(token)
                .expiration
                .before(Date())
        } catch (e: ExpiredJwtException) {
            true
        } catch (e: Exception) {
            false
        }
    }
}