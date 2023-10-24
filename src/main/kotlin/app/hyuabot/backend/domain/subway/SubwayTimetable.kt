package app.hyuabot.backend.domain.subway

import app.hyuabot.backend.dto.database.SubwayTimetablePK
import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "subway_timetable")
@IdClass(SubwayTimetablePK::class)
data class SubwayTimetable (
    @Id
    @Column(name = "station_id")
    val stationID: String,
    @Column(name = "start_station_id")
    var startStationID: String,
    @Column(name = "terminal_station_id")
    var terminalStationID: String,
    @Id
    @Column(name = "weekday")
    val weekdays: String,
    @Id
    @Column(name = "up_down_type")
    val heading: String,
    @Id
    @Column(name = "departure_time")
    val departureTime: LocalTime,
)