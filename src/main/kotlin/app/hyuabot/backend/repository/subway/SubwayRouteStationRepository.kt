package app.hyuabot.backend.repository.subway

import app.hyuabot.backend.domain.subway.SubwayRouteStation
import org.springframework.data.jpa.repository.JpaRepository

interface SubwayRouteStationRepository : JpaRepository<SubwayRouteStation, String>