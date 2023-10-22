package app.hyuabot.backend.domain.admin

import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "notices")
data class Notice (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    val id: Int,
    @Column(name = "category_id")
    var categoryID: Int,
    @Column(name = "title")
    var title: String,
    @Column(name = "url")
    var url: String,
    @Column(name = "expired_at")
    var expiredAt: LocalDateTime,
    @Column(name = "user_id")
    var createdBy: String,
) {
    constructor(categoryID: Int, title: String, url: String, expiredAt: LocalDateTime, createdBy: String) : this(0, categoryID, title, url, expiredAt, createdBy)
}
