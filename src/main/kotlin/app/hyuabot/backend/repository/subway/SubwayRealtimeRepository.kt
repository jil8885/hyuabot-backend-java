package app.hyuabot.backend.repository.subway

import app.hyuabot.backend.domain.subway.SubwayRealtime
import app.hyuabot.backend.dto.database.SubwayRealtimePK
import org.springframework.data.jpa.repository.JpaRepository

interface SubwayRealtimeRepository :
    JpaRepository<SubwayRealtime, SubwayRealtimePK> {
    fun existsByStationID(stationID: String): Boolean
    fun findAllByStationID(stationID: String): List<SubwayRealtime>
    fun findAllByStationIDAndHeading(stationID: String, heading: String): List<SubwayRealtime>
}