package app.hyuabot.backend.domain.shuttle

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "shuttle_holiday")
data class Holiday (
    @Id
    @Column(name = "holiday_date")
    val holidayDate: LocalDate,
    @Column(name = "holiday_type")
    val holidayType: String,
    @Column(name = "calendar_type")
    val calendarType: String,
) {
    constructor() : this(LocalDate.now(), "", "")
}