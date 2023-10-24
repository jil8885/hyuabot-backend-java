package app.hyuabot.backend.repository.admin

import app.hyuabot.backend.domain.admin.Notice
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface NoticeRepository : JpaRepository<Notice, Int> {
    fun getNoticesByCategoryID(categoryID: Int): List<Notice> {
        return getNoticesByCategoryID(categoryID).filter { it.expiredAt > LocalDateTime.now() }
    }

    fun deleteAllByCategoryID(categoryID: Int)
    fun deleteAllByTitle(title: String)
    fun existsByTitle(title: String): Boolean

    override fun findAll(): List<Notice> {
        return findAll().filter { it.expiredAt > LocalDateTime.now() }
    }
}
