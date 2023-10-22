package app.hyuabot.backend.domain.shuttle

import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "shuttle_timetable")
data class ShuttleTimetable (
    @Column(name = "seq")
    @Id
    val seq: Int,
    @Column(name = "period_type")
    var periodType: String,
    @Column(name = "weekday")
    var isWeekday: Boolean,
    @Column(name = "route_name")
    var routeName: String,
    @Column(name = "departure_time")
    var departureTime: LocalTime,
)