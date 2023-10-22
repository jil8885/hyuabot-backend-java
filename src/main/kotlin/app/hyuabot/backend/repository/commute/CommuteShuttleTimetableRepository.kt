package app.hyuabot.backend.repository.commute

import app.hyuabot.backend.domain.commute.CommuteShuttleTimetable
import app.hyuabot.backend.dto.database.CommuteShuttleTimetablePK
import org.springframework.data.jpa.repository.JpaRepository

interface CommuteShuttleTimetableRepository :
    JpaRepository<CommuteShuttleTimetable, CommuteShuttleTimetablePK> {
    fun findByRouteName(routeName: String): List<CommuteShuttleTimetable>
    fun findByStopName(stopName: String): List<CommuteShuttleTimetable>
}