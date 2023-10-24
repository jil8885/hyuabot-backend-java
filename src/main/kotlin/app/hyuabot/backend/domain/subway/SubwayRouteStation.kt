package app.hyuabot.backend.domain.subway

import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type
import java.time.Duration

@Entity
@Table(name = "subway_route_station")
data class SubwayRouteStation (
    @Id
    @Column(name = "station_id")
    val id: String,
    @Column(name = "station_name")
    var name: String,
    @Column(name = "route_id")
    var routeID: Int,
    @Column(name = "station_sequence")
    var seq: Int,
    @Column(name = "cumulative_time", columnDefinition = "interval")
    @Type(PostgreSQLIntervalType::class)
    var cumulativeTime: Duration,
)