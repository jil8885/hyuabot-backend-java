package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.Route
import org.springframework.data.jpa.repository.JpaRepository

interface RouteRepository: JpaRepository<Route, String>