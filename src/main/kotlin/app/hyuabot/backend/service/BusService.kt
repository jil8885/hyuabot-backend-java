package app.hyuabot.backend.service

import app.hyuabot.backend.domain.bus.*
import app.hyuabot.backend.dto.database.BusRealtimePK
import app.hyuabot.backend.dto.database.BusRouteStopPK
import app.hyuabot.backend.dto.database.BusTimetablePK
import app.hyuabot.backend.repository.bus.*
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

@Service
@Transactional(readOnly = true)
class BusService(
    private val busRealtimeRepository: BusRealtimeRepository,
    private val busRouteRepository: BusRouteRepository,
    private val busRouteStopRepository: BusRouteStopRepository,
    private val busStopRepository: BusStopRepository,
    private val busTimetableRepository: BusTimetableRepository,
) {
    @Transactional
    fun getBusRouteList(): List<BusRoute> = busRouteRepository.findAll()

    @Transactional
    fun getBusRouteListByName(name: String) =
        busRouteRepository.findAllByName(name)

    @Transactional
    fun getBusRouteListByCompany(company: String) =
        busRouteRepository.findAllByCompanyName(company)

    @Transactional
    fun getBusRouteListByType(type: String) =
        busRouteRepository.findAllByTypeName(type)

    @Transactional
    fun postBusRoute(
        routeID: Int,
        name: String,
        typeName: String,
        typeCode: String,
        startStopID: Int,
        endStopID: Int,
        upFirstTime: LocalTime,
        upLastTime: LocalTime,
        downFirstTime: LocalTime,
        downLastTime: LocalTime,
        districtCode: Int,
        companyID: Int,
        companyName: String,
        companyTelephone: String,
    ) {
        if (busRouteRepository.existsById(routeID)) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            busRouteRepository.save(
                BusRoute(
                    id = routeID,
                    name = name,
                    typeName = typeName,
                    typeCode = typeCode,
                    startStopID = startStopID,
                    endStopID = endStopID,
                    upFirstTime = upFirstTime,
                    upLastTime = upLastTime,
                    downFirstTime = downFirstTime,
                    downLastTime = downLastTime,
                    districtCode = districtCode,
                    companyID = companyID,
                    companyName = companyName,
                    companyTelephone = companyTelephone,
                )
            )
        }
    }

    @Transactional
    fun patchBusRoute(
        routeID: Int,
        name: String? = null,
        typeName: String? = null,
        typeCode: String? = null,
        startStopID: Int? = null,
        endStopID: Int? = null,
        upFirstTime: LocalTime? = null,
        upLastTime: LocalTime? = null,
        downFirstTime: LocalTime? = null,
        downLastTime: LocalTime? = null,
        districtCode: Int? = null,
        companyID: Int? = null,
        companyName: String? = null,
        companyTelephone: String? = null,
    ) {
        if (busRouteRepository.existsById(routeID)) {
            busRouteRepository.findById(routeID).map {
                it.name = name ?: it.name
                it.typeName = typeName ?: it.typeName
                it.typeCode = typeCode ?: it.typeCode
                it.startStopID = startStopID ?: it.startStopID
                it.endStopID = endStopID ?: it.endStopID
                it.upFirstTime = upFirstTime ?: it.upFirstTime
                it.upLastTime = upLastTime ?: it.upLastTime
                it.downFirstTime = downFirstTime ?: it.downFirstTime
                it.downLastTime = downLastTime ?: it.downLastTime
                it.districtCode = districtCode ?: it.districtCode
                it.companyID = companyID ?: it.companyID
                it.companyName = companyName ?: it.companyName
                it.companyTelephone = companyTelephone ?: it.companyTelephone
                busRouteRepository.save(it)
            }
        } else {
            throw Exception(
                "NOT_FOUND"
            )
        }
    }

    @Transactional
    fun deleteBusRoute(routeID: Int) {
        if (!busRouteRepository.existsById(routeID)) {
            throw Exception(
                "NOT_FOUND"
            )
        } else if (busTimetableRepository.existsByRouteID(routeID)) {
            throw Exception(
                "BUS_TIMETABLE_EXISTS"
            )
        } else {
            busRouteRepository.deleteAllByIdInBatch(listOf(routeID))
        }
    }

    @Transactional
    fun getBusStopList(): List<BusStop> = busStopRepository.findAll()

    @Transactional
    fun getBusStopListByName(name: String) =
        busStopRepository.findAllByName(name)

    @Transactional
    fun postBusStop(
        stopID: Int,
        name: String,
        latitude: Double,
        longitude: Double,
        districtCode: Int,
        districtName: String,
        mobileNumber: String,
    ) {
        if (busStopRepository.existsById(stopID)) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            busStopRepository.save(
                BusStop(
                    id = stopID,
                    name = name,
                    latitude = latitude,
                    longitude = longitude,
                    districtCode = districtCode,
                    regionName = districtName,
                    mobileNumber = mobileNumber,
                )
            )
        }
    }

    @Transactional
    fun patchBusStop(
        stopID: Int,
        name: String?,
        latitude: Double?,
        longitude: Double?,
        districtCode: Int?,
        districtName: String?,
        mobileNumber: String?,
    ) {
        if (busStopRepository.existsById(stopID)) {
            busStopRepository.findById(stopID).map {
                it.name = name ?: it.name
                it.latitude = latitude ?: it.latitude
                it.longitude = longitude ?: it.longitude
                it.districtCode = districtCode ?: it.districtCode
                it.regionName = districtName ?: it.regionName
                it.mobileNumber = mobileNumber ?: it.mobileNumber
                busStopRepository.save(it)
            }
        } else {
            throw Exception(
                "NOT_FOUND"
            )
        }
    }

    @Transactional
    fun deleteBusStop(stopID: Int) {
        try {
            if (!busStopRepository.existsById(stopID)) {
                throw Exception(
                    "NOT_FOUND"
                )
            } else if (busRouteStopRepository.existsByStopID(stopID)) {
                throw Exception(
                    "BUS_ROUTE_STOP_EXISTS"
                )
            } else {
                busStopRepository.deleteAllByIdInBatch(listOf(stopID))
            }
        } catch (e: DataIntegrityViolationException) {
            throw Exception(
                "BUS_ROUTE_REFERENCES_STOP"
            )
        }
    }

    @Transactional
    fun getBusRouteStopList(): List<BusRouteStop> = busRouteStopRepository.findAll()

    @Transactional
    fun getBusRouteStopListByRouteID(routeID: Int) =
        busRouteStopRepository.findAllByRouteID(routeID)

    @Transactional
    fun getBusRouteStopListByStopID(stopID: Int) =
        busRouteStopRepository.findAllByStopID(stopID)

    @Transactional
    fun getBusRouteStopListByRouteIDAndStopID(routeID: Int, stopID: Int) =
        busRouteStopRepository.findAllByRouteIDAndStopID(routeID, stopID)

    @Transactional
    fun postBusRouteStop(
        routeID: Int,
        stopID: Int,
        stopOrder: Int,
        startStopID: Int,
    ) {
        if (busRouteStopRepository.existsByRouteIDAndStopID(routeID, stopID)) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            busRouteStopRepository.save(
                BusRouteStop(
                    routeID = routeID,
                    stopID = stopID,
                    seq = stopOrder,
                    startStopID = startStopID,
                )
            )
        }
    }

    @Transactional
    fun patchBusRouteStop(
        routeID: Int,
        stopID: Int,
        stopOrder: Int?,
        startStopID: Int?,
    ) {
        if (busRouteStopRepository.existsById(
                BusRouteStopPK(
                    routeID = routeID,
                    stopID = stopID
                )
            )
        ) {
            busRouteStopRepository.findById(
                BusRouteStopPK(
                    routeID = routeID,
                    stopID = stopID
                )
            ).map {
                it.seq = stopOrder ?: it.seq
                it.startStopID = startStopID ?: it.startStopID
                busRouteStopRepository.save(it)
            }
        } else {
            throw Exception(
                "NOT_FOUND"
            )
        }
    }

    @Transactional
    fun deleteBusRouteStop(routeID: Int, stopID: Int) {
        if (!busRouteStopRepository.existsById(
                BusRouteStopPK(
                    routeID = routeID,
                    stopID = stopID
                )
            )
        ) {
            throw Exception(
                "NOT_FOUND"
            )
        }

        val item = busRouteStopRepository.findById(
            BusRouteStopPK(
                routeID = routeID,
                stopID = stopID
            )
        ).get()
        if (item.timetable.isNotEmpty()) {
            throw Exception(
                "BUS_TIMETABLE_EXISTS"
            )
        } else if (item.realtime.isNotEmpty()) {
            throw Exception(
                "BUS_REALTIME_EXISTS"
            )
        } else {
            busRouteStopRepository.deleteById(
                BusRouteStopPK(
                    routeID = routeID,
                    stopID = stopID
                )
            )
        }
    }

    @Transactional
    fun getBusTimetableList(): List<BusTimetable> = busTimetableRepository.findAll()

    @Transactional
    fun getBusTimetableListByRouteID(routeID: Int) =
        busTimetableRepository.findAllByRouteID(routeID)

    @Transactional
    fun getBusTimetableListByRouteIDAndStartStopID(
        routeID: Int,
        startStopID: Int
    ) = busTimetableRepository.findAllByRouteIDAndStartStopID(
        routeID,
        startStopID
    )

    @Transactional
    fun getBusTimetableListByConditions(
        routeID: Int,
        startStopID: Int,
        weekdays: String,
    ) = busTimetableRepository.findAllByRouteIDAndStartStopIDAndWeekdays(
        routeID,
        startStopID,
        weekdays
    )

    @Transactional
    fun postBusTimetable(
        routeID: Int,
        startStopID: Int,
        weekdays: String,
        departureTime: LocalTime,
    ) {
        if (busTimetableRepository.existsById(
                BusTimetablePK(
                    routeID = routeID,
                    startStopID = startStopID,
                    weekdays = weekdays,
                    departureTime = departureTime
                )
            )
        ) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            busTimetableRepository.save(
                BusTimetable(
                    routeID = routeID,
                    startStopID = startStopID,
                    weekdays = weekdays,
                    departureTime = departureTime,
                )
            )
        }
    }

    @Transactional
    fun deleteBusTimetable(
        routeID: Int,
        startStopID: Int,
        weekdays: String,
        departureTime: LocalTime
    ) {
        if (!busTimetableRepository.existsById(
                BusTimetablePK(
                    routeID = routeID,
                    startStopID = startStopID,
                    weekdays = weekdays,
                    departureTime = departureTime
                )
            )
        ) {
            throw Exception(
                "NOT_FOUND"
            )
        } else {
            busTimetableRepository.deleteById(
                BusTimetablePK(
                    routeID = routeID,
                    startStopID = startStopID,
                    weekdays = weekdays,
                    departureTime = departureTime
                )
            )
        }
    }

    @Transactional
    fun getBusRealtimeList(): List<BusRealtime> = busRealtimeRepository.findAll()

    @Transactional
    fun getBusRealtimeListByRouteID(routeID: Int) =
        busRealtimeRepository.findAllByRouteID(routeID)

    @Transactional
    fun getBusRealtimeListByRouteIDAndStopID(routeID: Int, stopID: Int) =
        busRealtimeRepository.findAllByRouteIDAndStopID(routeID, stopID)

    @Transactional
    fun getBusRealtimeListByStopID(stopID: Int) =
        busRealtimeRepository.findAllByStopID(stopID)

    @Transactional
    fun postBusRealtime(
        routeID: Int,
        stopID: Int,
        seq: Int,
        stop: Int,
        seat: Int,
        time: Duration,
        lowFloor: Boolean,
    ) {
        busRealtimeRepository.save(BusRealtime(
            routeID = routeID,
            stopID = stopID,
            seq = seq,
            stop = stop,
            seat = seat,
            time = time,
            lowFloor = lowFloor,
            updatedAt = LocalDateTime.now(),
        ))
    }

    @Transactional
    fun deleteBusRealtime(
        routeID: Int,
        stopID: Int,
        seq: Int,
    ) {
        if (!busRealtimeRepository.existsById(
                BusRealtimePK(
                    routeID = routeID,
                    stopID = stopID,
                    seq = seq,
                )
            )
        ) {
            throw Exception(
                "NOT_FOUND"
            )
        } else {
            busRealtimeRepository.deleteById(
                BusRealtimePK(
                    routeID = routeID,
                    stopID = stopID,
                    seq = seq,
                )
            )
        }
    }
}