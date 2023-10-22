package app.hyuabot.backend.domain.subway

import jakarta.persistence.*
import org.apache.commons.lang3.builder.ToStringExclude

@Entity
@Table(name = "subway_route")
data class SubwayRoute (
    @Id
    @Column(name = "route_id")
    val id: Int,
    @Column(name = "route_name")
    var name: String,

    @OneToMany(mappedBy = "route")
    @ToStringExclude
    val stations: List<SubwayRouteStation>? = emptyList(),
)