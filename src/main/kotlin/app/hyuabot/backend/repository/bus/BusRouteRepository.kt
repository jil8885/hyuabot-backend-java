package app.hyuabot.backend.repository.bus

import app.hyuabot.backend.domain.bus.BusRoute
import org.springframework.data.jpa.repository.JpaRepository

interface BusRouteRepository : JpaRepository<BusRoute, Int>