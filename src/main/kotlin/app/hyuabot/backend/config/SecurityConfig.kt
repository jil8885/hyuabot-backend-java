package app.hyuabot.backend.config

import app.hyuabot.backend.security.JWTAccessDeniedHandler
import app.hyuabot.backend.security.JWTAuthenticationEntryPoint
import app.hyuabot.backend.security.JWTAuthenticationFilter
import app.hyuabot.backend.security.JWTTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val tokenProvider: JWTTokenProvider,
    private val accessDeniedHandler: JWTAccessDeniedHandler,
    private val authenticationEntryPoint: JWTAuthenticationEntryPoint,
) {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .addFilterBefore(JWTAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            .sessionManagement{ it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .exceptionHandling{
                it.authenticationEntryPoint(authenticationEntryPoint)
                it.accessDeniedHandler(accessDeniedHandler)
            }
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**").permitAll()
                it.requestMatchers("/api/**").authenticated()
                it.anyRequest().permitAll()
            }
            .headers {
                it.frameOptions { frameOptions -> frameOptions.sameOrigin() }
            }
        return http.build()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(configuration: AuthenticationConfiguration): AuthenticationManager = configuration.authenticationManager
}