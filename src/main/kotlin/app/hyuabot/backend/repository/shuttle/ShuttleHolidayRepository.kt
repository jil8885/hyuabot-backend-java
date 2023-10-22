package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.ShuttleHoliday
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ShuttleHolidayRepository: JpaRepository<ShuttleHoliday, LocalDate>