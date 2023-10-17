package app.hyuabot.backend.service

import app.hyuabot.backend.dto.response.ShuttleTimetableViewItem
import app.hyuabot.backend.repository.shuttle.TimetableViewRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ShuttleService(
    private val shuttleTimetableViewRepository: TimetableViewRepository
) {
    @Transactional
    fun getShuttleTimetableView(
        periodType: String?,
        isWeekdays: Boolean?,
        stopName: String?,
        page: Int,
    ): Page<ShuttleTimetableViewItem> {
        return shuttleTimetableViewRepository.filterByCondition(
            periodType = periodType,
            isWeekdays = isWeekdays,
            stopName = stopName,
            pageable = PageRequest.of(page, 100, Sort.by("seq")),
        ).map {
            ShuttleTimetableViewItem(
                it.seq,
                it.stopName,
                it.isWeekday,
                it.routeName,
                it.routeType,
                it.departureTime.toString(),
            )
        }
    }
}