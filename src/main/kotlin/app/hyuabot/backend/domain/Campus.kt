package app.hyuabot.backend.domain

import app.hyuabot.backend.domain.cafeteria.Cafeteria
import app.hyuabot.backend.domain.library.ReadingRoom
import jakarta.persistence.*
import org.apache.commons.lang3.builder.ToStringExclude

@Entity
@Table(name = "campus")
data class Campus (
    @Id
    @Column(name = "campus_id")
    val id: Int,
    @Column(name = "campus_name")
    var name: String,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "campus")
    @ToStringExclude
    @OrderBy("restaurant_id ASC")
    val cafeteriaList: List<Cafeteria>,

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "campus")
    @ToStringExclude
    @OrderBy("room_id ASC")
    val readingRoomList: List<ReadingRoom>,
)