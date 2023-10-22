package app.hyuabot.backend.domain.commute

import app.hyuabot.backend.dto.database.CommuteShuttleTimetablePK
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
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
    val seq: Int,
    @Column(name = "departure_time")
    val departureTime: LocalTime,

    @OneToOne(optional = false)
    @JoinColumn(name = "route_name", referencedColumnName = "route_name", insertable = false, updatable = false)
    val commuteShuttleRoute: CommuteShuttleRoute,

    @OneToOne(optional = false)
    @JoinColumn(name = "stop_name", referencedColumnName = "stop_name", insertable = false, updatable = false)
    val commuteShuttleStop: CommuteShuttleStop,
)