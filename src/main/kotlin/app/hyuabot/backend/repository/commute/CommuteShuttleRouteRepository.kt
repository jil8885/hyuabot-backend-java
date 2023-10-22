package app.hyuabot.backend.repository.commute

import app.hyuabot.backend.domain.commute.CommuteShuttleRoute
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository

interface CommuteShuttleRouteRepository : JpaRepository<CommuteShuttleRoute, String>