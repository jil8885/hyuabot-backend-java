package app.hyuabot.backend.domain.admin

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime


@Entity
@Table(name = "notices")
data class Notice (
    @Id
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
)
