package app.hyuabot.backend.domain.subway

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "subway_route")
data class SubwayRoute (
    @Id
    @Column(name = "route_id")
    val id: Int,
    @Column(name = "route_name")
    var name: String,
)