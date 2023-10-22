package app.hyuabot.backend.domain.bus

import jakarta.persistence.*
import org.apache.commons.lang3.builder.ToStringExclude

@Entity
@Table(name = "bus_stop")
data class BusStop (
    @Id
    @Column(name = "stop_id")
    val id: Int,
    @Column(name = "stop_name")
    var name: String,
    @Column(name = "latitude")
    var latitude: Double,
    @Column(name = "longitude")
    var longitude: Double,
    @Column(name = "district_code")
    var districtCode: Int,
    @Column(name = "mobile_number")
    var mobileNumber: String,
    @Column(name = "region_name")
    var regionName: String,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "stop")
    @ToStringExclude
    val routes: List<BusRouteStop> = emptyList(),
)