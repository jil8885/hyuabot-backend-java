package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.Stop
import org.springframework.data.jpa.repository.JpaRepository

interface StopRepository: JpaRepository<Stop, String>