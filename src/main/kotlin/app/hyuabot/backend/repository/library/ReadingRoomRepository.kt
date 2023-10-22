package app.hyuabot.backend.repository.library

import app.hyuabot.backend.domain.library.ReadingRoom
import org.springframework.data.jpa.repository.JpaRepository

interface ReadingRoomRepository : JpaRepository<ReadingRoom, Int>