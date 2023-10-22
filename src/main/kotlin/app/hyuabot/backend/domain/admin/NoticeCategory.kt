package app.hyuabot.backend.domain.admin

import jakarta.persistence.*

@Entity
@Table(name = "notice_category")
data class NoticeCategory (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    val id: Int,

    @Column(name = "category_name")
    var name: String,
) {
    constructor(name: String) : this(0, name)
}
