package app.hyuabot.backend.domain.admin

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "notice_category")
data class NoticeCategory (
    @Id
    @Column(name = "category_id")
    val id: Int,

    @Column(name = "category_name")
    var name: String,
)
