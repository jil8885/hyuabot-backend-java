package app.hyuabot.backend.domain.commute

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "commute_shuttle_route")
data class CommuteShuttleRoute (
    @Id
    @Column(name = "route_name")
    val name: String,
    @Column(name = "route_description_korean")
    val descriptionKorean: String,
    @Column(name = "route_description_english")
    val descriptionEnglish: String,
)