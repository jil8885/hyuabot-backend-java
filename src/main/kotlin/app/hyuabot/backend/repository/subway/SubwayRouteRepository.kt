package app.hyuabot.backend.repository.subway

import app.hyuabot.backend.domain.subway.SubwayRoute
import org.springframework.data.jpa.repository.JpaRepository

interface SubwayRouteRepository : JpaRepository<SubwayRoute, Int> {
    fun findAllByName(name: String): List<SubwayRoute>
}