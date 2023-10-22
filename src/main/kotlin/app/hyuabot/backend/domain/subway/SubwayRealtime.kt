package app.hyuabot.backend.domain.subway

import app.hyuabot.backend.dto.database.SubwayRealtimePK
import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.time.Duration
import java.time.LocalDateTime

@Entity
@Table(name = "subway_realtime")
@IdClass(SubwayRealtimePK::class)
data class SubwayRealtime (
    @Id
    @Column(name = "station_id")
    val stationID: String,
    @Id
    @Column(name = "arrival_sequence")
    val seq: Int,
    @Id
    @Column(name = "up_down_type")
    val heading: String,
    @Column(name = "current_station_name")
    val current: String,
    @Column(name = "remaining_stop_count")
    val stop: Int,
    @Column(name = "remaining_time", columnDefinition = "interval")
    @Type(PostgreSQLIntervalType::class)
    val time: Duration,
    @Column(name = "terminal_station_id")
    val terminalStationID: String,
    @Column(name = "train_number")
    val trainNumber: String,
    @Column(name = "last_updated_time")
    val updatedAt: LocalDateTime,
    @Column(name = "is_express_train")
    val express: Boolean,
    @Column(name = "is_last_train")
    val last: Boolean,
    @Column(name = "status_code")
    val status: Int,

    @ManyToOne
    @JoinColumn(name = "station_id", referencedColumnName = "station_id")
    val station: SubwayRouteStation? = null,
)