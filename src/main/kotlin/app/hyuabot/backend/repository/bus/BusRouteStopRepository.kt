package app.hyuabot.backend.repository.bus

import app.hyuabot.backend.domain.bus.BusRouteStop
import app.hyuabot.backend.dto.database.BusRouteStopPK
import jakarta.persistence.*
import org.apache.commons.lang3.builder.ToStringExclude
import org.springframework.data.jpa.repository.JpaRepository

interface BusRouteStopRepository : JpaRepository<BusRouteStop, BusRouteStopPK>