package app.hyuabot.backend.repository.bus

import app.hyuabot.backend.domain.bus.BusRealtime
import app.hyuabot.backend.dto.database.BusRealtimePK
import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface BusRealtimeRepository : JpaRepository<BusRealtime, BusRealtimePK>