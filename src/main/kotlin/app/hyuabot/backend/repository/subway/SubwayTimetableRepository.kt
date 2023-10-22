package app.hyuabot.backend.repository.subway

import app.hyuabot.backend.domain.subway.SubwayTimetable
import app.hyuabot.backend.dto.database.SubwayTimetablePK
import org.springframework.data.jpa.repository.JpaRepository

interface SubwayTimetableRepository :
    JpaRepository<SubwayTimetable, SubwayTimetablePK>