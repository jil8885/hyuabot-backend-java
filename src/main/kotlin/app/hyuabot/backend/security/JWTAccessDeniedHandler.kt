package app.hyuabot.backend.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class JWTAccessDeniedHandler: AccessDeniedHandler {
    override fun handle(request: HttpServletRequest?, response: HttpServletResponse?, accessDeniedException: AccessDeniedException?) {
        response!!.characterEncoding = "UTF-8"
        response.sendError(403, "ACCESS_DENIED")
    }
}