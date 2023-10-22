package app.hyuabot.backend.repository.bus

import app.hyuabot.backend.domain.bus.BusTimetable
import app.hyuabot.backend.dto.database.BusTimetablePK
import org.springframework.data.jpa.repository.JpaRepository

interface BusTimetableRepository : JpaRepository<BusTimetable, BusTimetablePK> {
    fun existsByRouteID(routeID: Int): Boolean
    fun findAllByRouteID(routeID: Int): List<BusTimetable>
    fun findAllByRouteIDAndStartStopID(routeID: Int, startStopID: Int): List<BusTimetable>
    fun findAllByRouteIDAndStartStopIDAndWeekdays(routeID: Int, startStopID: Int, weekdays: String): List<BusTimetable>
}