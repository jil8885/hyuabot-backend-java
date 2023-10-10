package app.hyuabot.backend.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(info = Info(
    title = "휴아봇 백엔드 API 명세서",
    description = "스프링 백엔드 API 명세서.",
    version = "v1",
))
@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI {
        val securityScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name("Authorization")
        val securityRequirement = SecurityRequirement().addList("bearerAuth")
        return OpenAPI()
            .components(Components().addSecuritySchemes("bearerAuth", securityScheme))
            .security(listOf(securityRequirement))
    }
}