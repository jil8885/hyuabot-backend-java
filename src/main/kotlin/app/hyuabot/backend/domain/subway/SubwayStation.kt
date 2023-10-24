package app.hyuabot.backend.domain.subway

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "subway_station")
data class SubwayStation (
    @Id
    @Column(name = "station_name")
    val name: String,
)