package app.hyuabot.backend.service

import app.hyuabot.backend.domain.shuttle.*
import app.hyuabot.backend.dto.database.ShuttlePeriodPK
import app.hyuabot.backend.dto.database.ShuttleRouteStopPK
import app.hyuabot.backend.dto.request.shuttle.PatchRouteRequest
import app.hyuabot.backend.dto.request.shuttle.PatchRouteStopRequest
import app.hyuabot.backend.dto.request.shuttle.PatchStopRequest
import app.hyuabot.backend.dto.request.shuttle.PatchTimetableRequest
import app.hyuabot.backend.dto.response.*
import app.hyuabot.backend.repository.shuttle.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@Service
@Transactional(readOnly = true)
class ShuttleService(
    private val shuttleHolidayRepository: ShuttleHolidayRepository,
    private val shuttlePeriodRepository: ShuttlePeriodRepository,
    private val shuttleRouteRepository: ShuttleRouteRepository,
    private val shuttleRouteStopRepository: ShuttleRouteStopRepository,
    private val shuttleStopRepository: ShuttleStopRepository,
    private val shuttleTimetableRepository: ShuttleTimetableRepository,
    private val shuttleTimetableViewRepository: ShuttleTimetableViewRepository,
) {
    @Transactional
    fun getShuttleHoliday(): List<ShuttleHolidayItem> {
        return shuttleHolidayRepository.findAll().map {
            ShuttleHolidayItem(
                it.holidayDate.toString(),
                it.holidayType,
                it.calendarType,
            )
        }
    }

    @Transactional
    fun postShuttleHoliday(item: ShuttleHolidayItem) {
        if (shuttleHolidayRepository.existsById(LocalDate.parse(item.date))) {
            println(item.date)
            throw Exception(
                "DUPLICATED"
            )
        } else {
            shuttleHolidayRepository.save(
                ShuttleHoliday(
                    LocalDate.parse(item.date),
                    item.type,
                    item.calendar,
                )
            )
        }
    }

    @Transactional
    fun deleteShuttleHoliday(date: String) {
        val item = shuttleHolidayRepository.findById(LocalDate.parse(date)).orElseThrow {
            throw NullPointerException("SPECIFIED_DATE_NOT_FOUND")
        }
        shuttleHolidayRepository.delete(item)
    }

    @Transactional
    fun getShuttlePeriod(): List<ShuttlePeriodItem> {
        return shuttlePeriodRepository.findAll().map {
            ShuttlePeriodItem(
                it.periodType,
                it.periodStart.toString(),
                it.periodEnd.toString(),
            )
        }
    }

    @Transactional
    fun postShuttlePeriod(item: ShuttlePeriod) {
        if (shuttlePeriodRepository.existsById(
            ShuttlePeriodPK(
                periodType = item.periodType,
                periodStart = item.periodStart,
                periodEnd = item.periodEnd
        ))) {
            throw Exception("DUPLICATED")
        } else {
            shuttlePeriodRepository.save(item)
        }
    }

    @Transactional
    fun deleteShuttlePeriod(key: ShuttlePeriodPK) {
        val item = shuttlePeriodRepository.findById(key).orElseThrow {
            throw NullPointerException("SPECIFIED_PERIOD_ITEM_NOT_FOUND")
        }
        shuttlePeriodRepository.delete(item)
    }

    @Transactional
    fun getShuttleRoute(): List<ShuttleRoute> = shuttleRouteRepository.findAll()

    @Transactional
    fun postShuttleRoute(item: ShuttleRoute) {
        if (shuttleRouteRepository.existsById(item.routeName)) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            shuttleRouteRepository.save(item)
        }
    }

    @Transactional
    fun deleteShuttleRoute(routeName: String) {
        val item = shuttleRouteRepository.findById(routeName).orElseThrow {
            throw NullPointerException("SPECIFIED_ROUTE_NOT_FOUND")
        }
        shuttleRouteRepository.delete(item)
    }

    @Transactional
    fun patchShuttleRoute (
        routeName: String,
        payload: PatchRouteRequest,
    ) {
        val item = shuttleRouteRepository.findById(routeName).orElseThrow {
            throw NullPointerException("SPECIFIED_ROUTE_NOT_FOUND")
        }
        payload.routeType?.let { item.routeType = it }
        payload.routeDescriptionKorean?.let { item.routeDescriptionKorean = it }
        payload.routeDescriptionEnglish?.let { item.routeDescriptionEnglish = it }
        payload.startStop?.let { item.startStop = it }
        payload.endStop?.let { item.endStop = it }
    }

    @Transactional
    fun getShuttleStop(): List<ShuttleStop> = shuttleStopRepository.findAll()

    @Transactional
    fun postShuttleStop(item: ShuttleStop) {
        if (shuttleStopRepository.existsById(item.stopName)) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            shuttleStopRepository.save(item)
        }
    }

    @Transactional
    fun deleteShuttleStop(stopName: String) {
        shuttleStopRepository.findById(stopName).orElseThrow {
                throw NullPointerException("SPECIFIED_STOP_NOT_FOUND")
            }.let {
                shuttleStopRepository.delete(it)
            }
    }

    @Transactional
    fun patchShuttleStop(stopName: String, payload: PatchStopRequest) {
        shuttleStopRepository.findById(stopName).orElseThrow {
            throw NullPointerException("SPECIFIED_STOP_NOT_FOUND")
        }.let {
            payload.latitude?.let { latitude -> it.latitude = latitude }
            payload.longitude?.let { longitude -> it.longitude = longitude }
        }
    }

    @Transactional
    fun getShuttleRouteStop(route: String): List<ShuttleRouteStopItem> = shuttleRouteStopRepository.findAllByRouteName(route).map {
        ShuttleRouteStopItem(
            it.routeName,
            it.stopName,
            it.seq,
            it.cumulativeTime.toString(),
        )
    }

    @Transactional
    fun postShuttleRouteStop(item: ShuttleRouteStop) {
        if (shuttleRouteStopRepository.existsById(ShuttleRouteStopPK(
            routeName = item.routeName,
            stopName = item.stopName,
        ))) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            shuttleRouteStopRepository.save(item)
        }
    }

    @Transactional
    fun deleteShuttleRouteStop(routeName: String, stopName: String) {
        val item = shuttleRouteStopRepository.findById(ShuttleRouteStopPK(
            routeName = routeName,
            stopName = stopName,
        )).orElseThrow {
            throw NullPointerException("SPECIFIED_ROUTE_STOP_NOT_FOUND")
        }
        shuttleRouteStopRepository.delete(item)
    }

    @Transactional
    fun patchShuttleRouteStop (
        routeName: String,
        stopName: String,
        payload: PatchRouteStopRequest,
    ) {
        val item = shuttleRouteStopRepository.findById(ShuttleRouteStopPK(
            routeName = routeName,
            stopName = stopName,
        )).orElseThrow {
            throw NullPointerException("SPECIFIED_ROUTE_STOP_NOT_FOUND")
        }
        payload.seq?.let { item.seq = it }
        payload.cumulativeTime?.let { item.cumulativeTime = Duration.parse(it) }
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
    fun postShuttleTimetable(item: ShuttleTimetable) {
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
}