package app.hyuabot.backend.domain.shuttle

import app.hyuabot.backend.dto.database.ShuttleTimetableViewPK
import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import java.time.LocalTime

@Entity
@Table(name = "shuttle_timetable_view")
@Immutable
@IdClass(ShuttleTimetableViewPK::class)
data class TimetableView (
    @Column(name = "seq")
    @Id
    val seq: Int,
    @Column(name = "period_type")
    val periodType: String,
    @Column(name = "weekday")
    val isWeekday: Boolean,
    @Column(name = "route_name")
    val routeName: String,
    @Column(name = "route_tag")
    val routeType: String,
    @Column(name = "stop_name")
    @Id
    val stopName: String,
    @Column(name = "departure_time")
    val departureTime: LocalTime,
) {
    constructor() : this(0, "", false, "", "", "", LocalTime.of(0, 0))
}