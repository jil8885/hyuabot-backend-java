package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.ShuttleRouteStop
import app.hyuabot.backend.dto.database.ShuttleRouteStopPK
import org.springframework.data.jpa.repository.JpaRepository

interface ShuttleRouteStopRepository: JpaRepository<ShuttleRouteStop, ShuttleRouteStopPK> {
    fun findAllByRouteName(routeName: String): List<ShuttleRouteStop>
}