package app.hyuabot.backend.domain.cafeteria

import app.hyuabot.backend.domain.Campus
import jakarta.persistence.*
import org.apache.commons.lang3.builder.ToStringExclude

@Entity
@Table(name = "restaurant")
data class Cafeteria (
    @Id
    @Column(name = "restaurant_id")
    val id: Int,
    @Column(name = "restaurant_name")
    val name: String,
    @Column(name = "campus_id")
    val campusID: Int,
    @Column(name = "latitude")
    val latitude: Double,
    @Column(name = "longitude")
    val longitude: Double,

    @ManyToOne
    @JoinColumn(name = "campus_id", referencedColumnName = "campus_id", insertable = false, updatable = false)
    val campus: Campus,

    @OneToMany(mappedBy = "cafeteria", fetch = FetchType.LAZY)
    @ToStringExclude
    val menuList: List<Menu>,
)