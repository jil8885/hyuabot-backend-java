package app.hyuabot.backend.repository.bus

import app.hyuabot.backend.domain.bus.BusStop
import org.springframework.data.jpa.repository.JpaRepository

interface BusStopRepository : JpaRepository<BusStop, Int>