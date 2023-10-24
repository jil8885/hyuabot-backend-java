package app.hyuabot.backend.domain.bus

import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "bus_route")
data class BusRoute (
    @Id
    @Column(name = "route_id")
    val id: Int,
    @Column(name = "route_name")
    var name: String,
    @Column(name = "route_type_code")
    var typeCode: String,
    @Column(name = "route_type_name")
    var typeName: String,
    @Column(name = "start_stop_id")
    var startStopID: Int,
    @Column(name = "end_stop_id")
    var endStopID: Int,
    @Column(name = "up_first_time")
    var upFirstTime: LocalTime,
    @Column(name = "up_last_time")
    var upLastTime: LocalTime,
    @Column(name = "down_first_time")
    var downFirstTime: LocalTime,
    @Column(name = "down_last_time")
    var downLastTime: LocalTime,
    @Column(name = "district_code")
    var districtCode: Int,
    @Column(name = "company_id")
    var companyID: Int,
    @Column(name = "company_name")
    var companyName: String,
    @Column(name = "company_telephone")
    var companyTelephone: String,

    @OneToOne(optional = false)
    @JoinColumn(name = "start_stop_id", referencedColumnName = "stop_id", insertable = false, updatable = false)
    var startStop: BusStop? = null,

    @OneToOne(optional = false)
    @JoinColumn(name = "end_stop_id", referencedColumnName = "stop_id", insertable = false, updatable = false)
    var endStop: BusStop? = null,
)