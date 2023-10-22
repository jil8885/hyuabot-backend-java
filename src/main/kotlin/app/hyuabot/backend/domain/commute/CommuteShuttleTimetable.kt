package app.hyuabot.backend.domain.commute

import app.hyuabot.backend.dto.database.CommuteShuttleTimetablePK
import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "commute_shuttle_timetable")
@IdClass(CommuteShuttleTimetablePK::class)
data class CommuteShuttleTimetable (
    @Id
    @Column(name = "route_name")
    val routeName: String,
    @Id
    @Column(name = "stop_name")
    val stopName: String,
    @Column(name = "stop_order")
    var seq: Int,
    @Column(name = "departure_time")
    var departureTime: LocalTime,

    @OneToOne(optional = false)
    @JoinColumn(name = "route_name", referencedColumnName = "route_name", insertable = false, updatable = false)
    val commuteShuttleRoute: CommuteShuttleRoute? = null,

    @OneToOne(optional = false)
    @JoinColumn(name = "stop_name", referencedColumnName = "stop_name", insertable = false, updatable = false)
    val commuteShuttleStop: CommuteShuttleStop? = null,
)