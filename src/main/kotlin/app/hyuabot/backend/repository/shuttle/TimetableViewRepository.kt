package app.hyuabot.backend.repository.shuttle

import app.hyuabot.backend.domain.shuttle.TimetableView
import app.hyuabot.backend.dto.database.ShuttleTimetableViewPK
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface TimetableViewRepository: JpaRepository<TimetableView, ShuttleTimetableViewPK> {
    @Query("SELECT * FROM shuttle_timetable_view WHERE " +
        "(period_type = :periodType OR :periodType IS NULL) AND " +
        "(weekday = :isWeekdays OR :isWeekdays IS NULL) AND " +
        "(stop_name = :stopName OR :stopName IS NULL) ",
        nativeQuery = true)
    fun filterByCondition (
        periodType: String?,
        isWeekdays: Boolean?,
        stopName: String?,
        pageable: Pageable,
    ): Page<TimetableView>
}