package app.hyuabot.backend.domain.bus

import app.hyuabot.backend.dto.database.BusTimetablePK
import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "bus_timetable")
@IdClass(BusTimetablePK::class)
data class BusTimetable (
    @Id
    @Column(name = "route_id")
    val routeID: Int,
    @Id
    @Column(name = "start_stop_id")
    val startStopID: Int,
    @Id
    @Column(name = "weekday")
    val weekdays: String,
    @Id
    @Column(name = "departure_time")
    val departureTime: LocalTime,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns(
        JoinColumn(name = "route_id", referencedColumnName = "route_id", insertable = false, updatable = false),
        JoinColumn(name = "start_stop_id", referencedColumnName = "stop_id", insertable = false, updatable = false),
    )
    var routeStop: BusRouteStop? = null,
)