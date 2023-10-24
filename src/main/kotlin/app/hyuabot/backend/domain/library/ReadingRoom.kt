package app.hyuabot.backend.domain.library

import app.hyuabot.backend.domain.Campus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reading_room")
data class ReadingRoom (
    @Id
    @Column(name = "room_id")
    val id: Int,
    @Column(name = "room_name")
    var name: String,
    @Column(name = "campus_id")
    var campusID: Int,
    @Column(name = "is_active")
    var isActive: Boolean,
    @Column(name = "is_reservable")
    var isReservable: Boolean,
    @Column(name = "total")
    var total: Int,
    @Column(name = "active_total")
    var active: Int,
    @Column(name = "occupied")
    var occupied: Int,
    @Column(name = "available", insertable = false, updatable = false)
    var available: Int = active - occupied,
    @Column(name = "last_updated_time")
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "campus_id", referencedColumnName = "campus_id", insertable = false, updatable = false)
    val campus: Campus? = null,
)