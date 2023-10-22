package app.hyuabot.backend.repository.bus

import app.hyuabot.backend.domain.bus.BusTimetable
import app.hyuabot.backend.dto.database.BusTimetablePK
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalTime

interface BusTimetableRepository : JpaRepository<BusTimetable, BusTimetablePK>