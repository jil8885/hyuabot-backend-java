package app.hyuabot.backend.service

import app.hyuabot.backend.domain.commute.CommuteShuttleRoute
import app.hyuabot.backend.domain.commute.CommuteShuttleStop
import app.hyuabot.backend.domain.commute.CommuteShuttleTimetable
import app.hyuabot.backend.dto.database.CommuteShuttleTimetablePK
import app.hyuabot.backend.repository.commute.CommuteShuttleRouteRepository
import app.hyuabot.backend.repository.commute.CommuteShuttleStopRepository
import app.hyuabot.backend.repository.commute.CommuteShuttleTimetableRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime

@Service
@Transactional(readOnly = true)
class CommuteShuttleService(
    private val commuteShuttleRouteRepository: CommuteShuttleRouteRepository,
    private val commuteShuttleStopRepository: CommuteShuttleStopRepository,
    private val commuteShuttleTimetableRepository: CommuteShuttleTimetableRepository,
) {
    @Transactional
    fun getShuttleRouteList() = commuteShuttleRouteRepository.findAll()

    @Transactional
    fun getShuttleStopList() = commuteShuttleStopRepository.findAll()

    @Transactional
    fun getShuttleTimetableList() = commuteShuttleTimetableRepository.findAll()

    @Transactional
    fun getShuttleTimetableListByRoute(routeName: String) =
        commuteShuttleTimetableRepository.findByRouteName(routeName)

    @Transactional
    fun getShuttleTimetableListByStop(stopName: String) =
        commuteShuttleTimetableRepository.findByStopName(stopName)

    @Transactional
    fun getShuttleTimetableListByRouteAndStop(routeName: String, stopName: String) =
        commuteShuttleTimetableRepository.findById(CommuteShuttleTimetablePK(stopName = stopName, routeName = routeName))

    @Transactional
    fun postShuttleRoute(routeName: String, descriptionKorean: String, descriptionEnglish: String) {
        if (commuteShuttleRouteRepository.existsById(routeName)) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            commuteShuttleRouteRepository.save(
                CommuteShuttleRoute(
                    name = routeName,
                    descriptionKorean = descriptionKorean,
                    descriptionEnglish = descriptionEnglish,
                )
            )
        }
    }

    @Transactional
    fun postShuttleStop(stopName: String, latitude: Double, longitude: Double, description: String) {
        if (commuteShuttleStopRepository.existsById(stopName)) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            commuteShuttleStopRepository.save(
                CommuteShuttleStop(
                    name = stopName,
                    latitude = latitude,
                    longitude = longitude,
                    description = description,
                )
            )
        }
    }

    @Transactional
    fun postShuttleTimetable(routeName: String, stopName: String, seq: Int, time: LocalTime) {
        if (commuteShuttleTimetableRepository.existsById(CommuteShuttleTimetablePK(stopName = stopName, routeName = routeName))) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            commuteShuttleTimetableRepository.save(
                CommuteShuttleTimetable(
                    routeName = routeName,
                    stopName = stopName,
                    seq = seq,
                    departureTime = time,
                )
            )
        }
    }

    @Transactional
    fun patchShuttleRoute(routeName: String, descriptionKorean: String?, descriptionEnglish: String?) {
        commuteShuttleRouteRepository.findById(routeName).orElseThrow {
            throw NullPointerException("SPECIFIED_ROUTE_NOT_FOUND")
        }.let {
            it.descriptionKorean = descriptionKorean ?: it.descriptionKorean
            it.descriptionEnglish = descriptionEnglish ?: it.descriptionEnglish
            commuteShuttleRouteRepository.save(it)
        }
    }

    @Transactional
    fun patchShuttleStop(stopName: String, latitude: Double?, longitude: Double?, description: String?){
        commuteShuttleStopRepository.findById(stopName).orElseThrow {
            throw NullPointerException("SPECIFIED_STOP_NOT_FOUND")
        }.let {
            it.latitude = latitude ?: it.latitude
            it.longitude = longitude ?: it.longitude
            it.description = description ?: it.description
            commuteShuttleStopRepository.save(it)
        }
    }

    @Transactional
    fun patchShuttleTimetable(routeName: String, stopName: String, seq: Int?, time: LocalTime?) {
        commuteShuttleTimetableRepository.findById(CommuteShuttleTimetablePK(stopName = stopName, routeName = routeName)).orElseThrow {
            throw NullPointerException("SPECIFIED_TIMETABLE_NOT_FOUND")
        }.let {
            it.seq = seq ?: it.seq
            it.departureTime = time ?: it.departureTime
            commuteShuttleTimetableRepository.save(it)
        }
    }

    @Transactional
    fun deleteShuttleRoute(routeName: String) {
        if (commuteShuttleRouteRepository.existsById(routeName)) {
            commuteShuttleRouteRepository.deleteById(routeName)
        } else if (commuteShuttleTimetableRepository.findByRouteName(routeName).isNotEmpty()) {
            throw Exception("ROUTE_HAS_TIMETABLES")
        } else {
            throw Exception("ROUTE_NOT_FOUND")
        }
    }

    @Transactional
    fun deleteShuttleStop(stopName: String) {
        if (commuteShuttleStopRepository.existsById(stopName)) {
            commuteShuttleStopRepository.deleteById(stopName)
        } else if (commuteShuttleTimetableRepository.findByStopName(stopName).isNotEmpty()) {
            throw Exception("STOP_HAS_TIMETABLES")
        } else {
            throw Exception("STOP_NOT_FOUND")
        }
    }

    @Transactional
    fun deleteShuttleTimetable(routeName: String, stopName: String) {
        if (commuteShuttleTimetableRepository.existsById(CommuteShuttleTimetablePK(stopName = stopName, routeName = routeName))) {
            commuteShuttleTimetableRepository.deleteById(CommuteShuttleTimetablePK(stopName = stopName, routeName = routeName))
        } else {
            throw Exception("TIMETABLE_NOT_FOUND")
        }
    }
}