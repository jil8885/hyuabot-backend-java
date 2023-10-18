package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.RouteStop
import app.hyuabot.backend.dto.database.ShuttleRouteStopPK
import org.springframework.data.jpa.repository.JpaRepository

interface RouteStopRepository: JpaRepository<RouteStop, ShuttleRouteStopPK> {
    fun findAllByRouteName(routeName: String): List<RouteStop>
}