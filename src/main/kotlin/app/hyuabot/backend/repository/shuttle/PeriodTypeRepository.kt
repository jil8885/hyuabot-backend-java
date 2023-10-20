package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.PeriodType
import org.springframework.data.jpa.repository.JpaRepository

interface PeriodTypeRepository: JpaRepository<PeriodType, String>