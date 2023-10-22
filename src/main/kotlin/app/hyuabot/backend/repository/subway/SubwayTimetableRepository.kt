package app.hyuabot.backend.repository.subway

import app.hyuabot.backend.domain.subway.SubwayTimetable
import app.hyuabot.backend.dto.database.SubwayTimetablePK
import org.springframework.data.jpa.repository.JpaRepository

interface SubwayTimetableRepository :
    JpaRepository<SubwayTimetable, SubwayTimetablePK> {
    fun existsByStationID(stationID: String): Boolean
    fun findAllByStationID(stationID: String): List<SubwayTimetable>
    fun findAllByStationIDAndHeading(stationID: String, heading: String): List<SubwayTimetable>
    fun findAllByStationIDAndHeadingAndWeekdays(stationID: String, heading: String, weekdays: String): List<SubwayTimetable>
}