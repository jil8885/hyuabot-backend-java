package app.hyuabot.backend.repository.admin

import app.hyuabot.backend.domain.admin.NoticeCategory
import org.springframework.data.jpa.repository.JpaRepository

interface NoticeCategoryRepository : JpaRepository<NoticeCategory, Int> {
    fun existsByName(name: String): Boolean
}