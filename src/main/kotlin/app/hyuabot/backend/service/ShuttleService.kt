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
import java.lang.NullPointerException
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
        val item = shuttleTimetableRepository.findById(seq).orElseThrow {
            throw NullPointerException("SPECIFIED_SEQ_NOT_FOUND")
        }
        shuttleTimetableRepository.delete(item)
    }

    @Transactional
    fun patchShuttleTimetable(
        seq: Int,
        payload: PatchTimetableRequest,
    ) {
        val item = shuttleTimetableRepository.findById(seq).orElseThrow {
            throw NullPointerException("SPECIFIED_SEQ_NOT_FOUND")
        }
        payload.periodType?.let { item.periodType = it }
        payload.isWeekday?.let { item.isWeekday = it }
        payload.routeName?.let { item.routeName = it }
        payload.departureTime?.let { item.departureTime = LocalTime.parse(it) }
    }
}