package app.hyuabot.backend.repository.commute

import app.hyuabot.backend.domain.commute.CommuteShuttleStop
import org.springframework.data.jpa.repository.JpaRepository

interface CommuteShuttleStopRepository :
    JpaRepository<CommuteShuttleStop, String>