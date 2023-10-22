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
    var seq: Int,
    @Column(name = "start_stop_id")
    var startStopID: Int,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "routeStop")
    @ToStringExclude
    @OrderBy("departureTime ASC")
    val timetable: List<BusTimetable> = emptyList(),

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "routeStop")
    @ToStringExclude
    @OrderBy("seq ASC")
    val realtime: List<BusRealtime> = emptyList(),

    @ManyToOne(optional = false)
    @JoinColumn(name = "route_id", referencedColumnName = "route_id", insertable = false, updatable = false)
    val route: BusRoute? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "stop_id", referencedColumnName = "stop_id", insertable = false, updatable = false)
    val stop: BusStop? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "start_stop_id", referencedColumnName = "stop_id", insertable = false, updatable = false)
    val startStop: BusStop? = null,
)