package app.hyuabot.backend.domain.shuttle

import app.hyuabot.backend.dto.database.ShuttleRouteStopPK
import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.time.Duration

@Entity
@Table(name = "shuttle_route_stop")
@IdClass(ShuttleRouteStopPK::class)
data class ShuttleRouteStop (
    @Column(name = "route_name")
    @Id
    val routeName: String,
    @Column(name = "stop_name")
    @Id
    val stopName: String,
    @Column(name = "stop_order")
    var seq: Int,
    @Column(name = "cumulative_time", columnDefinition = "interval")
    @Type(PostgreSQLIntervalType::class)
    var cumulativeTime: Duration,
)