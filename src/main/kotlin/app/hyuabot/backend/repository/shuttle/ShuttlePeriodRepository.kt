package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.ShuttlePeriod
import app.hyuabot.backend.dto.database.ShuttlePeriodPK
import org.springframework.data.jpa.repository.JpaRepository

interface ShuttlePeriodRepository: JpaRepository<ShuttlePeriod, ShuttlePeriodPK>