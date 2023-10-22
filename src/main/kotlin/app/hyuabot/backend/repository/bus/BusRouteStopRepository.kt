package app.hyuabot.backend.repository.bus

import app.hyuabot.backend.domain.bus.BusRouteStop
import app.hyuabot.backend.dto.database.BusRouteStopPK
import org.springframework.data.jpa.repository.JpaRepository

interface BusRouteStopRepository : JpaRepository<BusRouteStop, BusRouteStopPK> {
    fun existsByRouteID(routeID: Int): Boolean
    fun existsByStopID(stopID: Int): Boolean
    fun existsByRouteIDAndStopID(routeID: Int, stopID: Int): Boolean
    fun findAllByRouteID(routeID: Int): List<BusRouteStop>
    fun findAllByStopID(stopID: Int): List<BusRouteStop>
    fun findAllByRouteIDAndStopID(routeID: Int, stopID: Int): List<BusRouteStop>
}