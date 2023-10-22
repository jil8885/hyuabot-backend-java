package app.hyuabot.backend.domain.shuttle

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "shuttle_period_type")
data class ShuttlePeriodType (
    @Id
    @Column(name = "period_type")
    val periodType: String,
)