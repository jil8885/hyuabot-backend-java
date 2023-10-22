package app.hyuabot.backend.domain.commute

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "commute_shuttle_stop")
data class CommuteShuttleStop (
    @Id
    @Column(name = "stop_name")
    val name: String,
    @Column(name = "description")
    val description: String,
    @Column(name = "latitude")
    val latitude: Double,
    @Column(name = "longitude")
    val longitude: Double,
)