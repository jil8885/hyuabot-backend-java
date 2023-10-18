package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.Period
import app.hyuabot.backend.dto.database.ShuttlePeriodPK
import org.springframework.data.jpa.repository.JpaRepository

interface PeriodRepository: JpaRepository<Period, ShuttlePeriodPK>