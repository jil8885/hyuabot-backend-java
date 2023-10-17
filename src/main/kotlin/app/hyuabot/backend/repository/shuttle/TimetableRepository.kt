package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.Timetable
import org.springframework.data.jpa.repository.JpaRepository

interface TimetableRepository: JpaRepository<Timetable, Int>
