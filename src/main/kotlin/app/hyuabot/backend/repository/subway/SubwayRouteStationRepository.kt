package app.hyuabot.backend.repository.subway

import app.hyuabot.backend.domain.subway.SubwayRouteStation
import org.springframework.data.jpa.repository.JpaRepository

interface SubwayRouteStationRepository : JpaRepository<SubwayRouteStation, String> {
    fun existsByRouteID(routeID: Int): Boolean
    fun existsByName(stationName: String): Boolean
    fun findAllByRouteID(routeID: Int): List<SubwayRouteStation>
    fun findAllByName(stationName: String): List<SubwayRouteStation>
    fun findAllByRouteIDAndName(routeID: Int, stationName: String): List<SubwayRouteStation>
}