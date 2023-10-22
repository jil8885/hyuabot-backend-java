package app.hyuabot.backend.domain.bus

import app.hyuabot.backend.dto.database.BusRouteStopPK
import jakarta.persistence.*
import org.apache.commons.lang3.builder.ToStringExclude

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
    val seq: Int,
    @Column(name = "start_stop_id")
    val startStopID: Int,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "routeStop")
    @ToStringExclude
    @OrderBy("departureTime ASC")
    val timetable: List<BusTimetable>,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "routeStop")
    @ToStringExclude
    @OrderBy("seq ASC")
    val realtime: List<BusRealtime>,

    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id", referencedColumnName = "route_id", insertable = false, updatable = false)
    val route: BusRoute,

    @ManyToOne(optional = false)
    @JoinColumn(name = "stop_id", referencedColumnName = "stop_id", insertable = false, updatable = false)
    val stop: BusStop,

    @ManyToOne(optional = false)
    @JoinColumn(name = "start_stop_id", referencedColumnName = "stop_id", insertable = false, updatable = false)
    val startStop: BusStop,
)