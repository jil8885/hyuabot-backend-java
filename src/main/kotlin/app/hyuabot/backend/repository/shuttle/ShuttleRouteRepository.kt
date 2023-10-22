package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.ShuttleRoute
import org.springframework.data.jpa.repository.JpaRepository

interface ShuttleRouteRepository: JpaRepository<ShuttleRoute, String>