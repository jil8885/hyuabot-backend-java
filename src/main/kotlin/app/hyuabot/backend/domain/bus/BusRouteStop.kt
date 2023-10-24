package app.hyuabot.backend.domain.bus

import app.hyuabot.backend.dto.database.BusRouteStopPK
import jakarta.persistence.*

@Entity
@Table(name = "bus_route_stop")
@IdClass(BusRouteStopPK::class)
data class BusRouteStop (
    @Id
    @Column(name = "route_id")
    val routeID: Int,
    @Id
    @Column(name = "stop_id")
    val stopID: Int,
    @Column(name = "stop_sequence")
    var seq: Int,
    @Column(name = "start_stop_id")
    var startStopID: Int,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "routeStop")
    var timetable: List<BusTimetable> = listOf(),

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "routeStop")
    var realtime: List<BusRealtime> = listOf(),
)