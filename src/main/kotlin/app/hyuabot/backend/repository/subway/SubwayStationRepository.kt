package app.hyuabot.backend.repository.subway

import app.hyuabot.backend.domain.subway.SubwayStation
import org.springframework.data.jpa.repository.JpaRepository

interface SubwayStationRepository : JpaRepository<SubwayStation, String>