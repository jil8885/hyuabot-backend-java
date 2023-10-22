package app.hyuabot.backend.repository.admin

import app.hyuabot.backend.domain.admin.Notice
import org.springframework.data.jpa.repository.JpaRepository

interface NoticeRepository : JpaRepository<Notice, Int>
