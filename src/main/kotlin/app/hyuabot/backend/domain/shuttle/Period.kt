package app.hyuabot.backend.domain.shuttle

import app.hyuabot.backend.dto.database.ShuttlePeriodPK
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "shuttle_period")
@IdClass(ShuttlePeriodPK::class)
data class Period (
    @Column(name = "period_type")
    @Id
    var periodType: String,
    @Column(name = "period_start")
    @Id
    var periodStart: LocalDateTime,
    @Column(name = "period_end")
    @Id
    var periodEnd: LocalDateTime,
)