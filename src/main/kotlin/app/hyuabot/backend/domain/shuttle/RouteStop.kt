package app.hyuabot.backend.domain.shuttle

import app.hyuabot.backend.dto.database.ShuttleRouteStopPK
import app.hyuabot.backend.service.DurationConverter
import jakarta.persistence.*
import java.time.Duration

@Entity
@Table(name = "shuttle_route_stop")
@IdClass(ShuttleRouteStopPK::class)
data class RouteStop (
    @Column(name = "route_name")
    @Id
    val routeName: String,
    @Column(name = "stop_name")
    @Id
    val stopName: String,
    @Column(name = "stop_order")
    var seq: Int,
    @Column(name = "cumulative_time")
    @Transient
    @Convert(converter = DurationConverter::class)
    var cumulativeTime: Duration,
)