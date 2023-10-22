package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.ShuttlePeriodType
import org.springframework.data.jpa.repository.JpaRepository

interface PeriodTypeRepository: JpaRepository<ShuttlePeriodType, String>