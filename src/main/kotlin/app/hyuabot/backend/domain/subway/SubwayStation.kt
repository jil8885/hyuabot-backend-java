package app.hyuabot.backend.domain.subway

import jakarta.persistence.*
import org.apache.commons.lang3.builder.ToStringExclude

@Entity
@Table(name = "subway_station")
data class SubwayStation (
    @Id
    @Column(name = "station_name")
    val name: String,

    @OneToMany(mappedBy = "station")
    @ToStringExclude
    val routes: List<SubwayRouteStation>,
)