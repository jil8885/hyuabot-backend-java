package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.ShuttleStop
import org.springframework.data.jpa.repository.JpaRepository

interface StopRepository: JpaRepository<ShuttleStop, String>