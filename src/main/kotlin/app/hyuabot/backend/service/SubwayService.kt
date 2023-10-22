package app.hyuabot.backend.service

import app.hyuabot.backend.domain.subway.*
import app.hyuabot.backend.dto.database.SubwayRealtimePK
import app.hyuabot.backend.dto.database.SubwayTimetablePK
import app.hyuabot.backend.repository.subway.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

@Service
@Transactional(readOnly = true)
class SubwayService(
    private val subwayRealtimeRepository: SubwayRealtimeRepository,
    private val subwayRouteRepository: SubwayRouteRepository,
    private val subwayRouteStationRepository: SubwayRouteStationRepository,
    private val subwayStationRepository: SubwayStationRepository,
    private val subwayTimetableRepository: SubwayTimetableRepository
) {
    @Transactional
    fun getSubwayRouteList(): List<SubwayRoute> = subwayRouteRepository.findAll()

    @Transactional
    fun getSubwayRouteListByName(name: String): List<SubwayRoute> = subwayRouteRepository.findAllByName(name)

    @Transactional
    fun getSubwayRouteByID(id: Int): SubwayRoute = subwayRouteRepository.findById(id).orElseThrow()

    @Transactional
    fun postSubwayRoute(subwayRoute: SubwayRoute): SubwayRoute {
        if (subwayRouteRepository.existsById(subwayRoute.id)) {
            throw Exception("DUPLICATED")
        } else {
            return subwayRouteRepository.save(subwayRoute)
        }
    }

    @Transactional
    fun patchSubwayRoute(routeID: Int, name: String?) {
        subwayRouteRepository.findById(routeID).orElseThrow().let {
            it.name = name ?: it.name
            subwayRouteRepository.save(it)
        }
    }

    @Transactional
    fun deleteSubwayRoute(routeID: Int) {
        if (!subwayRouteRepository.existsById(routeID)) {
            throw Exception("NOT_FOUND")
        } else if (subwayRouteStationRepository.existsByRouteID(routeID)) {
            throw Exception("ROUTE_STATION_REFERENCES_ROUTE")
        } else {
            subwayRouteRepository.deleteById(routeID)
        }
    }

    @Transactional
    fun getSubwayStationList(): List<SubwayStation> = subwayStationRepository.findAll()

    @Transactional
    fun getSubwayStationByName(name: String): SubwayStation = subwayStationRepository.findById(name).orElseThrow()

    @Transactional
    fun postSubwayStation(stationName: String): SubwayStation {
        if (subwayStationRepository.existsById(stationName)) {
            throw Exception("DUPLICATED")
        } else {
            return subwayStationRepository.save(SubwayStation(stationName))
        }
    }

    @Transactional
    fun deleteSubwayStation(stationName: String) {
        if (!subwayStationRepository.existsById(stationName)) {
            throw Exception("NOT_FOUND")
        } else if (subwayRouteStationRepository.existsByName(stationName)) {
            throw Exception("ROUTE_STATION_REFERENCES_STATION")
        } else {
            subwayStationRepository.deleteById(stationName)
        }
    }

    @Transactional
    fun getSubwayRouteStationList(): List<SubwayRouteStation> = subwayRouteStationRepository.findAll()

    @Transactional
    fun getSubwayRouteStationListByRouteID(routeID: Int): List<SubwayRouteStation> = subwayRouteStationRepository.findAllByRouteID(routeID)

    @Transactional
    fun getSubwayRouteStationListByStationID(stationName: String): List<SubwayRouteStation> = subwayRouteStationRepository.findAllByName(stationName)

    @Transactional
    fun getSubwayRouteStationListByRouteIDAndStationID(routeID: Int, stationName: String): List<SubwayRouteStation> = subwayRouteStationRepository.findAllByRouteIDAndName(routeID, stationName)

    @Transactional
    fun getSubwayRouteStationByID(stationID: String): SubwayRouteStation = subwayRouteStationRepository.findById(stationID).orElseThrow()

    @Transactional
    fun postSubwayRouteStation(
        stationID: String,
        stationName: String,
        routeID: Int,
        seq: Int,
        cumulativeTime: Duration
    ) {
        if (subwayRouteStationRepository.existsById(stationID)) {
            throw Exception("DUPLICATED")
        } else if (!subwayStationRepository.existsById(stationName)) {
            throw Exception("STATION_NOT_FOUND")
        } else if (!subwayRouteRepository.existsById(routeID)) {
            throw Exception("ROUTE_NOT_FOUND")
        } else {
            subwayRouteStationRepository.save(SubwayRouteStation(stationID, stationName, routeID, seq, cumulativeTime))
        }
    }

    @Transactional
    fun patchSubwayRouteStation(
        stationID: String,
        stationName: String?,
        routeID: Int?,
        seq: Int?,
        cumulativeTime: Duration?
    ) {
        subwayRouteStationRepository.findById(stationID).orElseThrow().let {
            it.name = stationName ?: it.name
            it.routeID = routeID ?: it.routeID
            it.seq = seq ?: it.seq
            it.cumulativeTime = cumulativeTime ?: it.cumulativeTime
            subwayRouteStationRepository.save(it)
        }
    }

    @Transactional
    fun deleteSubwayRouteStation(stationID: String) {
        if (!subwayRouteStationRepository.existsById(stationID)) {
            throw Exception("NOT_FOUND")
        } else if (subwayTimetableRepository.existsByStationID(stationID)) {
            throw Exception("TIMETABLE_REFERENCES_ROUTE_STATION")
        } else if (subwayRealtimeRepository.existsByStationID(stationID)) {
            throw Exception("REALTIME_REFERENCES_ROUTE_STATION")
        } else {
            subwayRouteStationRepository.deleteById(stationID)
        }
    }

    @Transactional
    fun getSubwayTimetableList(): List<SubwayTimetable> = subwayTimetableRepository.findAll()

    @Transactional
    fun getSubwayTimetableListByStationID(stationID: String): List<SubwayTimetable> = subwayTimetableRepository.findAllByStationID(stationID)

    @Transactional
    fun getSubwayTimetableListByStationIDAndHeading(stationID: String, heading: String): List<SubwayTimetable> = subwayTimetableRepository.findAllByStationIDAndHeading(stationID, heading)

    @Transactional
    fun getSubwayTimetableListByStationIDAndHeadingAndWeekdays(stationID: String, heading: String, weekdays: String): List<SubwayTimetable> = subwayTimetableRepository.findAllByStationIDAndHeadingAndWeekdays(stationID, heading, weekdays)

    @Transactional
    fun postSubwayTimetable(
        stationID: String,
        startStationID: String,
        terminalStationID: String,
        heading: String,
        weekdays: String,
        time: LocalTime
    ) {
        if (subwayTimetableRepository.existsById(SubwayTimetablePK(stationID = stationID, heading = heading, weekdays = weekdays, departureTime = time))) {
            throw Exception("DUPLICATED")
        } else if (!subwayRouteStationRepository.existsById(stationID)) {
            throw Exception("ROUTE_STATION_NOT_FOUND")
        } else {
            subwayTimetableRepository.save(SubwayTimetable(
                stationID = stationID,
                startStationID = startStationID,
                terminalStationID = terminalStationID,
                heading = heading,
                weekdays = weekdays,
                departureTime = time
            ))
        }
    }

    @Transactional
    fun deleteSubwayTimetable(stationID: String, heading: String, weekdays: String, time: LocalTime) {
        if (!subwayTimetableRepository.existsById(SubwayTimetablePK(stationID = stationID, heading = heading, weekdays = weekdays, departureTime = time))) {
            throw Exception("NOT_FOUND")
        } else {
            subwayTimetableRepository.deleteById(SubwayTimetablePK(stationID = stationID, heading = heading, weekdays = weekdays, departureTime = time))
        }
    }

    @Transactional
    fun getSubwayRealtimeList(): List<SubwayRealtime> = subwayRealtimeRepository.findAll()

    @Transactional
    fun getSubwayRealtimeListByStationID(stationID: String): List<SubwayRealtime> = subwayRealtimeRepository.findAllByStationID(stationID)

    @Transactional
    fun getSubwayRealtimeListByStationIDAndHeading(stationID: String, heading: String): List<SubwayRealtime> = subwayRealtimeRepository.findAllByStationIDAndHeading(stationID, heading)

    @Transactional
    fun postSubwayRealtime(
        stationID: String,
        heading: String,
        seq: Int,
        currentStation: String,
        remainingStop: Int,
        remainingTime: Duration,
        terminalStationID: String,
        trainNumber: String,
        express: Boolean,
        last: Boolean,
        status: Int
    ) {
        subwayRealtimeRepository.save(
            SubwayRealtime(
                stationID = stationID,
                heading = heading,
                seq = seq,
                current = currentStation,
                stop = remainingStop,
                time = remainingTime,
                terminalStationID = terminalStationID,
                trainNumber = trainNumber,
                express = express,
                last = last,
                status = status,
                updatedAt = LocalDateTime.now()
            )
        )
    }

    @Transactional
    fun deleteSubwayRealtime(stationID: String, heading: String, seq: Int) {
        if (!subwayRealtimeRepository.existsById(SubwayRealtimePK(stationID = stationID, heading = heading, seq = seq))) {
            throw Exception("NOT_FOUND")
        } else {
            subwayRealtimeRepository.deleteById(SubwayRealtimePK(stationID = stationID, heading = heading, seq = seq))
        }
    }
}