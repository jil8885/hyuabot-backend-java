package app.hyuabot.backend.domain.subway

import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType
import jakarta.persistence.*
import org.apache.commons.lang3.builder.ToStringExclude
import org.hibernate.annotations.Type
import java.time.Duration

@Entity
@Table(name = "subway_route_station")
data class SubwayRouteStation (
    @Id
    @Column(name = "station_id")
    val id: String,
    @Column(name = "station_name")
    val name: String,
    @Column(name = "route_id")
    val routeID: Int,
    @Column(name = "station_sequence")
    val seq: Int,
    @Column(name = "cumulative_time", columnDefinition = "interval")
    @Type(PostgreSQLIntervalType::class)
    val cumulativeTime: Duration,

    @ManyToOne
    @JoinColumn(name = "station_name", referencedColumnName = "station_name", insertable = false, updatable = false)
    val station: SubwayStation,

    @ManyToOne
    @JoinColumn(name = "route_id", referencedColumnName = "route_id", insertable = false, updatable = false)
    val route: SubwayRoute,

    @OneToMany(mappedBy = "station", fetch = FetchType.LAZY)
    @ToStringExclude
    val timetable: List<SubwayTimetable>,

    @OneToMany(mappedBy = "station", fetch = FetchType.LAZY)
    @ToStringExclude
    val realtime: List<SubwayRealtime>,
)