package app.hyuabot.backend.repository.bus

import app.hyuabot.backend.domain.bus.BusRealtime
import app.hyuabot.backend.dto.database.BusRealtimePK
import org.springframework.data.jpa.repository.JpaRepository

interface BusRealtimeRepository : JpaRepository<BusRealtime, BusRealtimePK> {
    fun findAllByRouteID(routeID: Int): List<BusRealtime>
    fun findAllByStopID(stopID: Int): List<BusRealtime>
    fun findAllByRouteIDAndStopID(routeID: Int, stopID: Int): List<BusRealtime>

}