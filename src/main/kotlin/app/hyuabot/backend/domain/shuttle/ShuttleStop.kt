package app.hyuabot.backend.domain.shuttle

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "shuttle_stop")
data class ShuttleStop (
    @Column(name = "stop_name")
    @Id
    val stopName: String,
    @Column(name = "latitude")
    var latitude: Double,
    @Column(name = "longitude")
    var longitude: Double,
)