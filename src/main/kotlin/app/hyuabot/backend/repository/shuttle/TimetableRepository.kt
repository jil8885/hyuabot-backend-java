package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.ShuttleTimetable
import org.springframework.data.jpa.repository.JpaRepository

interface TimetableRepository: JpaRepository<ShuttleTimetable, Int> {
    fun findAllByRouteNameAndPeriodType(routeName: String, periodType: String): List<ShuttleTimetable>
}
