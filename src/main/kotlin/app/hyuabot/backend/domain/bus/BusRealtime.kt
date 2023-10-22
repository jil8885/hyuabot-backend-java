package app.hyuabot.backend.domain.bus

import app.hyuabot.backend.dto.database.BusRealtimePK
import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.time.Duration
import java.time.LocalDateTime

@Entity
@Table(name = "bus_realtime")
@IdClass(BusRealtimePK::class)
data class BusRealtime (
    @Id
    @Column(name = "route_id")
    val routeID: Int,
    @Id
    @Column(name = "stop_id")
    val stopID: Int,
    @Id
    @Column(name = "arrival_sequence")
    val seq: Int,
    @Column(name = "remaining_stop_count")
    val stop: Int,
    @Column(name = "remaining_seat_count")
    val seat: Int,
    @Column(name = "remaining_time", columnDefinition = "interval")
    @Type(PostgreSQLIntervalType::class)
    val time: Duration,
    @Column(name = "low_plate")
    val lowFloor: Boolean,
    @Column(name = "last_updated_time")
    val updatedAt: LocalDateTime,

    @ManyToOne
    @JoinColumns(
        JoinColumn(name = "route_id", referencedColumnName = "route_id"),
        JoinColumn(name = "stop_id", referencedColumnName = "stop_id"),
    )
    val routeStop: BusRouteStop? = null,
)