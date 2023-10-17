package app.hyuabot.backend.service

import app.hyuabot.backend.domain.shuttle.Timetable
import app.hyuabot.backend.dto.request.shuttle.PatchTimetableRequest
import app.hyuabot.backend.dto.response.ShuttleTimetableItem
import app.hyuabot.backend.dto.response.ShuttleTimetableViewItem
import app.hyuabot.backend.repository.shuttle.TimetableRepository
import app.hyuabot.backend.repository.shuttle.TimetableViewRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime

@Service
@Transactional(readOnly = true)
class ShuttleService(
    private val shuttleTimetableViewRepository: TimetableViewRepository,
    private val shuttleTimetableRepository: TimetableRepository,
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
                it.periodType,
                it.stopName,
                it.isWeekday,
                it.routeName,
                it.routeType,
                it.departureTime.toString(),
            )
        }
    }

    @Transactional
    fun getShuttleTimetable(): List<ShuttleTimetableItem> {
        return shuttleTimetableRepository.findAll().map {
            ShuttleTimetableItem(
                it.seq,
                it.periodType,
                it.isWeekday,
                it.routeName,
                it.departureTime.toString(),
            )
        }
    }

    @Transactional
    fun postShuttleTimetable(item: Timetable) {
        if (shuttleTimetableRepository.existsById(item.seq)) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            shuttleTimetableRepository.save(item)
        }
    }

    @Transactional
    fun deleteShuttleTimetable(seq: Int) {
        shuttleTimetableRepository.delete(
            shuttleTimetableRepository.findById(seq).orElseThrow {
                throw Exception(
                    "NOT_FOUND"
                )
            }
        )
    }

    @Transactional
    fun patchShuttleTimetable(
        seq: Int,
        item: PatchTimetableRequest,
    ) {
        try {
            shuttleTimetableRepository.findById(seq).orElseThrow {
                throw Exception(
                    "NOT_FOUND"
                )
            }.apply {
                this.periodType = item.periodType ?: this.periodType
                this.isWeekday = item.isWeekday ?: this.isWeekday
                this.routeName = item.routeName ?: this.routeName
                this.departureTime =
                    LocalTime.parse(item.departureTime) ?: this.departureTime
            }
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}