package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.Holiday
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface HolidayRepository: JpaRepository<Holiday, LocalDate>