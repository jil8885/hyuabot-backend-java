package app.hyuabot.backend.service

import app.hyuabot.backend.domain.shuttle.*
import app.hyuabot.backend.dto.database.ShuttlePeriodPK
import app.hyuabot.backend.dto.database.ShuttleRouteStopPK
import app.hyuabot.backend.dto.request.shuttle.*
import app.hyuabot.backend.dto.response.ShuttleHolidayItem
import app.hyuabot.backend.dto.response.ShuttlePeriodItem
import app.hyuabot.backend.dto.response.ShuttleTimetableItem
import app.hyuabot.backend.dto.response.ShuttleTimetableViewItem
import app.hyuabot.backend.repository.shuttle.*
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.NullPointerException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Service
@Transactional(readOnly = true)
class ShuttleService(
    private val shuttleHolidayRepository: HolidayRepository,
    private val shuttlePeriodRepository: PeriodRepository,
    private val shuttleRouteRepository: RouteRepository,
    private val shuttleRouteStopRepository: RouteStopRepository,
    private val shuttleStopRepository: StopRepository,
    private val shuttleTimetableRepository: TimetableRepository,
    private val shuttleTimetableViewRepository: TimetableViewRepository,
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
            throw Exception(
                "DUPLICATED"
            )
        } else {
            shuttleHolidayRepository.save(
                Holiday(
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
        try {
            shuttleHolidayRepository.delete(item)
        } catch (e: DataIntegrityViolationException) {
            throw Exception("TIMETABLE_ITEM_REFERENCES_HOLIDAY")
        } catch (e: Exception) {
            throw Exception("CANNOT_DELETE_HOLIDAY_ITEM")
        }
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
    fun postShuttlePeriod(item: Period) {
        shuttlePeriodRepository.save(item)
    }

    @Transactional
    fun deleteShuttlePeriod(key: ShuttlePeriodPK) {
        val item = shuttlePeriodRepository.findById(key).orElseThrow {
            throw NullPointerException("SPECIFIED_PERIOD_ITEM_NOT_FOUND")
        }
        try {
            shuttlePeriodRepository.delete(item)
        } catch (e: DataIntegrityViolationException) {
            throw Exception("TIMETABLE_ITEM_REFERENCES_PERIOD")
        } catch (e: Exception) {
            throw Exception("CANNOT_DELETE_PERIOD_ITEM")
        }
    }

    @Transactional
    fun patchShuttlePeriod(key: ShuttlePeriodPK, payload: PatchPeriodRequest) {
        val item = shuttlePeriodRepository.findById(key).orElseThrow {
            throw NullPointerException("SPECIFIED_PERIOD_ITEM_NOT_FOUND")
        }
        payload.period?.let { item.periodType = it }
        payload.start?.let { item.periodStart = LocalDateTime.parse(it) }
        payload.end?.let { item.periodEnd = LocalDateTime.parse(it) }
    }

    @Transactional
    fun getShuttleRoute(): List<Route> = shuttleRouteRepository.findAll()

    @Transactional
    fun postShuttleRoute(item: Route) {
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
        try {
            shuttleRouteRepository.delete(item)
        } catch (e: DataIntegrityViolationException) {
            throw Exception("ROUTE_STOP_ITEM_REFERENCES_ROUTE")
        } catch (e: Exception) {
            throw Exception("CANNOT_DELETE_ROUTE_ITEM")
        }
    }

    @Transactional
    fun patchShuttleRoute (
        routeName: String,
        payload: PatchRouteRequest,
    ) {
        val item = shuttleRouteRepository.findById(routeName).orElseThrow {
            throw NullPointerException("SPECIFIED_ROUTE_NOT_FOUND")
        }
        payload.routeName?.let { item.routeName = it }
        payload.routeDescriptionKorean?.let { item.routeDescriptionKorean = it }
        payload.routeDescriptionEnglish?.let { item.routeDescriptionEnglish = it }
        payload.startStop?.let { item.startStop = it }
        payload.endStop?.let { item.endStop = it }
    }

    @Transactional
    fun getShuttleStop(): List<Stop> = shuttleStopRepository.findAll()

    @Transactional
    fun postShuttleStop(item: Stop) {
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
        try {
            shuttleStopRepository.findById(stopName).orElseThrow {
                throw NullPointerException("SPECIFIED_STOP_NOT_FOUND")
            }.let {
                shuttleStopRepository.delete(it)
            }
        } catch (e: DataIntegrityViolationException) {
            throw Exception("ROUTE_STOP_ITEM_REFERENCES_STOP")
        } catch (e: Exception) {
            throw Exception("CANNOT_DELETE_STOP_ITEM")
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
    fun getShuttleRouteStop(route: String): List<RouteStop> = shuttleRouteStopRepository.findAllByRouteName(route)

    @Transactional
    fun postShuttleRouteStop(item: RouteStop) {
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
        try {
                val item = shuttleRouteStopRepository.findById(ShuttleRouteStopPK(
                routeName = routeName,
                stopName = stopName,
            )).orElseThrow {
                throw NullPointerException("SPECIFIED_ROUTE_STOP_NOT_FOUND")
            }
            shuttleRouteStopRepository.delete(item)
        } catch (e: DataIntegrityViolationException) {
            throw Exception("TIMETABLE_ITEM_REFERENCES_ROUTE_STOP")
        } catch (e: Exception) {
            throw Exception("CANNOT_DELETE_ROUTE_STOP_ITEM")
        }
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