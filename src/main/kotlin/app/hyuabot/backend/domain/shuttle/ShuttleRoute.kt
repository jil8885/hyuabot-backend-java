package app.hyuabot.backend.domain.shuttle

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "shuttle_route")
data class ShuttleRoute (
    @Column(name = "route_name")
    @Id
    var routeName: String,
    @Column(name = "route_tag")
    var routeType: String,
    @Column(name = "route_description_korean")
    var routeDescriptionKorean: String,
    @Column(name = "route_description_english")
    var routeDescriptionEnglish: String,
    @Column(name = "start_stop")
    var startStop: String,
    @Column(name = "end_stop")
    var endStop: String,
)