package app.hyuabot.backend.service

import app.hyuabot.backend.domain.subway.*
import app.hyuabot.backend.dto.database.SubwayRealtimePK
import app.hyuabot.backend.dto.database.SubwayTimetablePK
import app.hyuabot.backend.repository.subway.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

@SpringBootTest
@ActiveProfiles("test")
class SubwayServiceTest {
    @Autowired private lateinit var subwayService: SubwayService
    @Autowired private lateinit var subwayRealtimeRepository: SubwayRealtimeRepository
    @Autowired private lateinit var subwayTimetableRepository: SubwayTimetableRepository
    @Autowired private lateinit var subwayRouteRepository: SubwayRouteRepository
    @Autowired private lateinit var subwayRouteStationRepository: SubwayRouteStationRepository
    @Autowired private lateinit var subwayStationRepository: SubwayStationRepository

    @BeforeEach
    fun init() {
        clean()
        subwayRouteRepository.saveAll(listOf(
            SubwayRoute(id = 1, name = "TEST_ROUTE_1"),
        ))
        subwayStationRepository.saveAll(listOf(
            SubwayStation(name = "TEST_STATION_1"),
            SubwayStation(name = "TEST_STATION_2"),
            SubwayStation(name = "TEST_STATION_3"),
        ))
        subwayRouteStationRepository.saveAll(listOf(
            SubwayRouteStation(id = "T001", name = "TEST_STATION_1", routeID = 1, seq = 0, cumulativeTime = Duration.ZERO),
            SubwayRouteStation(id = "T002", name = "TEST_STATION_2", routeID = 1, seq = 1, cumulativeTime = Duration.ofMinutes(1)),
            SubwayRouteStation(id = "T003", name = "TEST_STATION_3", routeID = 1, seq = 2, cumulativeTime = Duration.ofMinutes(2)),
        ))
        subwayTimetableRepository.saveAll(listOf(
            SubwayTimetable(stationID = "T001", startStationID = "T001", terminalStationID = "T003", weekdays = "weekends", heading = "up", departureTime = LocalTime.parse("00:00")),
            SubwayTimetable(stationID = "T001", startStationID = "T001", terminalStationID = "T003", weekdays = "weekends", heading = "up", departureTime = LocalTime.parse("00:10")),
            SubwayTimetable(stationID = "T001", startStationID = "T001", terminalStationID = "T003", weekdays = "weekends", heading = "up", departureTime = LocalTime.parse("00:20")),
        ))
        subwayRealtimeRepository.saveAll(listOf(
            SubwayRealtime(stationID = "T002", seq = 0, heading = "up", current = "T001", stop = 1, time = Duration.ofMinutes(10), terminalStationID = "T003", trainNumber = "K001", updatedAt = LocalDateTime.now(), express = false, last = false, status = 0),
            SubwayRealtime(stationID = "T002", seq = 1, heading = "up", current = "T002", stop = 1, time = Duration.ofMinutes(25), terminalStationID = "T003", trainNumber = "K003", updatedAt = LocalDateTime.now(), express = false, last = false, status = 0),
            SubwayRealtime(stationID = "T002", seq = 2, heading = "up", current = "T003", stop = 1, time = Duration.ofMinutes(40), terminalStationID = "T003", trainNumber = "K005", updatedAt = LocalDateTime.now(), express = false, last = false, status = 0),
        ))
    }

    @AfterEach
    fun clean() {
        subwayRealtimeRepository.deleteAllInBatch(subwayRealtimeRepository.findAllByStationID("T002"))
        subwayTimetableRepository.deleteAllInBatch(subwayTimetableRepository.findAllByStationID("T001"))
        subwayRouteStationRepository.deleteAllByIdInBatch(listOf(
            "T001", "T002", "T003"
        ))
        subwayStationRepository.deleteAllByIdInBatch(listOf(
            "TEST_STATION_1", "TEST_STATION_2", "TEST_STATION_3"
        ))
        subwayRouteRepository.deleteAllByIdInBatch(listOf(
            1
        ))
    }

    @Test
    @DisplayName("GET_SUBWAY_ROUTE_LIST")
    fun getSubwayRouteList() {
        assertTrue(subwayService.getSubwayRouteList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_SUBWAY_ROUTE_LIST_BY_NAME")
    fun getSubwayRouteListByName() {
        assertTrue(subwayService.getSubwayRouteListByName("TEST_ROUTE_1").isNotEmpty())
    }

    @Test
    @DisplayName("GET_SUBWAY_ROUTE")
    fun getSubwayRoute() {
        assertTrue(subwayService.getSubwayRouteByID(1).id == 1)
        assertTrue(subwayService.getSubwayRouteByID(1).name == "TEST_ROUTE_1")
    }

    @Test
    @DisplayName("POST_SUBWAY_ROUTE_SUCCESS")
    fun postSubwayRouteSuccess() {
        subwayService.postSubwayRoute(SubwayRoute(2, "TEST_ROUTE_2"))
        assertTrue(subwayRouteRepository.existsById(2))
        subwayRouteRepository.deleteById(2)
    }

    @Test
    @DisplayName("POST_SUBWAY_ROUTE_DUPLICATE")
    fun postSubwayRouteDuplicate() {
        assertTrue(assertThrows<Exception> {
            subwayService.postSubwayRoute(SubwayRoute(1, "TEST_ROUTE_1"))
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("PATCH_SUBWAY_ROUTE_SUCCESS")
    fun patchSubwayRouteSuccess() {
        subwayService.patchSubwayRoute(1, "TEST_ROUTE_2")
        assertTrue(subwayRouteRepository.findById(1).get().name == "TEST_ROUTE_2")
    }

    @Test
    @DisplayName("PATCH_SUBWAY_ROUTE_NOT_FOUND")
    fun patchSubwayRouteNotFound() {
        assertTrue(assertThrows<Exception> {
            subwayService.patchSubwayRoute(2, "TEST_ROUTE_2")
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_SUBWAY_ROUTE_SUCCESS")
    fun deleteSubwayRouteSuccess() {
        subwayRealtimeRepository.deleteAllInBatch(subwayRealtimeRepository.findAllByStationID("T002"))
        subwayTimetableRepository.deleteAllInBatch(subwayTimetableRepository.findAllByStationID("T001"))
        subwayRouteStationRepository.deleteAllByIdInBatch(listOf(
            "T001", "T002", "T003"
        ))
        subwayService.deleteSubwayRoute(1)
        assertTrue(subwayRouteRepository.findById(1).isEmpty)
    }

    @Test
    @DisplayName("DELETE_SUBWAY_ROUTE_NOT_FOUND")
    fun deleteSubwayRouteNotFound() {
        assertTrue(assertThrows<Exception> {
            subwayService.deleteSubwayRoute(2)
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_SUBWAY_ROUTE_STATION_REFERENCES_ROUTE")
    fun deleteSubwayRouteStationReferencesRoute() {
        assertTrue(assertThrows<Exception> {
            subwayService.deleteSubwayRoute(1)
        }.message == "ROUTE_STATION_REFERENCES_ROUTE")
    }

    @Test
    @DisplayName("GET_SUBWAY_STATION_LIST")
    fun getSubwayStationList() {
        assertTrue(subwayService.getSubwayStationList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_SUBWAY_STATION_LIST_BY_NAME")
    fun getSubwayStationListByName() {
        assertTrue(subwayService.getSubwayStationByName("TEST_STATION_1").name == "TEST_STATION_1")
    }

    @Test
    @DisplayName("GET_SUBWAY_STATION_LIST_BY_NAME_NOT_FOUND")
    fun getSubwayStationListByNameNotFound() {
        assertTrue(assertThrows<Exception> {
            subwayService.getSubwayStationByName("TEST_STATION_4")
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("POST_SUBWAY_STATION_SUCCESS")
    fun postSubwayStationSuccess() {
        subwayService.postSubwayStation("TEST_STATION_4")
        assertTrue(subwayStationRepository.existsById("TEST_STATION_4"))
        subwayStationRepository.deleteById("TEST_STATION_4")
    }

    @Test
    @DisplayName("POST_SUBWAY_STATION_DUPLICATE")
    fun postSubwayStationDuplicate() {
        assertTrue(assertThrows<Exception> {
            subwayService.postSubwayStation("TEST_STATION_1")
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("DELETE_SUBWAY_STATION_SUCCESS")
    fun deleteSubwayStationSuccess() {
        subwayTimetableRepository.deleteAllInBatch(subwayTimetableRepository.findAllByStationID("T001"))
        subwayRealtimeRepository.deleteAllInBatch(subwayRealtimeRepository.findAllByStationID("T001"))
        subwayRouteStationRepository.deleteAllByIdInBatch(listOf(
            "T001",
        ))
        subwayService.deleteSubwayStation("TEST_STATION_1")
        assertTrue(subwayStationRepository.findById("TEST_STATION_1").isEmpty)
    }

    @Test
    @DisplayName("DELETE_SUBWAY_STATION_NOT_FOUND")
    fun deleteSubwayStationNotFound() {
        assertTrue(assertThrows<Exception> {
            subwayService.deleteSubwayStation("TEST_STATION_4")
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_SUBWAY_STATION_ROUTE_STATION_REFERENCES_STATION")
    fun deleteSubwayStationRouteStationReferencesStation() {
        assertTrue(assertThrows<Exception> {
            subwayService.deleteSubwayStation("TEST_STATION_2")
        }.message == "ROUTE_STATION_REFERENCES_STATION")
    }

    @Test
    @DisplayName("GET_SUBWAY_ROUTE_STATION_LIST")
    fun getSubwayRouteStationList() {
        assertTrue(subwayService.getSubwayRouteStationList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_SUBWAY_ROUTE_STATION_LIST_BY_ROUTE_ID")
    fun getSubwayRouteStationListByRouteID() {
        assertTrue(subwayService.getSubwayRouteStationListByRouteID(1).isNotEmpty())
    }

    @Test
    @DisplayName("GET_SUBWAY_ROUTE_STATION_BY_STATION_ID")
    fun getSubwayRouteStationByStationID() {
        assertTrue(subwayService.getSubwayRouteStationListByStationID("T001").name == "TEST_STATION_1")
        assertTrue(subwayService.getSubwayRouteStationListByStationID("T001").routeID == 1)
        assertTrue(subwayService.getSubwayRouteStationListByStationID("T001").seq == 0)
    }

    @Test
    @DisplayName("GET_SUBWAY_ROUTE_STATION")
    fun getSubwayRouteStation() {
        assertTrue(subwayService.getSubwayRouteStationByID("T001").id == "T001")
        assertTrue(subwayService.getSubwayRouteStationByID("T001").name == "TEST_STATION_1")
        assertTrue(subwayService.getSubwayRouteStationByID("T001").routeID == 1)
        assertTrue(subwayService.getSubwayRouteStationByID("T001").seq == 0)
        assertTrue(subwayService.getSubwayRouteStationByID("T001").cumulativeTime == Duration.ZERO)
    }

    @Test
    @DisplayName("POST_SUBWAY_ROUTE_STATION_SUCCESS")
    fun postSubwayRouteStation() {
        subwayService.postSubwayRouteStation("T004", "TEST_STATION_3", 1, 3, Duration.ofMinutes(3))
        assertTrue(subwayRouteStationRepository.existsById("T004"))
        subwayRouteStationRepository.deleteById("T004")
    }

    @Test
    @DisplayName("POST_SUBWAY_ROUTE_STATION_DUPLICATED")
    fun postSubwayRouteStationDuplicated() {
        assertTrue(assertThrows<Exception> {
            subwayService.postSubwayRouteStation("T001", "TEST_STATION_1", 1, 0, Duration.ZERO)
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("POST_SUBWAY_ROUTE_STATION_STATION_NOT_FOUND")
    fun postSubwayRouteStationStationNotFound() {
        assertTrue(assertThrows<Exception> {
            subwayService.postSubwayRouteStation("T004", "TEST_STATION_4", 1, 3, Duration.ofMinutes(3))
        }.message == "STATION_NOT_FOUND")
    }

    @Test
    @DisplayName("POST_SUBWAY_ROUTE_STATION_ROUTE_NOT_FOUND")
    fun postSubwayRouteStationRouteNotFound() {
        assertTrue(assertThrows<Exception> {
            subwayService.postSubwayRouteStation("T004", "TEST_STATION_3", 2, 3, Duration.ofMinutes(3))
        }.message == "ROUTE_NOT_FOUND")
    }

    @Test
    @DisplayName("PATCH_SUBWAY_ROUTE_STATION_SUCCESS")
    fun patchSubwayRouteStation() {
        subwayService.patchSubwayRouteStation("T001", "TEST_STATION_3", 1, 3, Duration.ofMinutes(3))
        assertTrue(subwayRouteStationRepository.findById("T001").get().name == "TEST_STATION_3")
        assertTrue(subwayRouteStationRepository.findById("T001").get().routeID == 1)
        assertTrue(subwayRouteStationRepository.findById("T001").get().seq == 3)
        assertTrue(subwayRouteStationRepository.findById("T001").get().cumulativeTime == Duration.ofMinutes(3))
    }

    @Test
    @DisplayName("PATCH_SUBWAY_ROUTE_STATION_NOT_FOUND")
    fun patchSubwayRouteStationNotFound() {
        assertTrue(assertThrows<Exception> {
            subwayService.patchSubwayRouteStation("T004", "TEST_STATION_4", 1, 3, Duration.ofMinutes(3))
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_SUBWAY_ROUTE_STATION_SUCCESS")
    fun deleteSubwayRouteStationSuccess() {
        subwayTimetableRepository.deleteAllInBatch(subwayTimetableRepository.findAllByStationID("T001"))
        subwayRealtimeRepository.deleteAllInBatch(subwayRealtimeRepository.findAllByStationID("T002"))
        subwayService.deleteSubwayRouteStation("T001")
        assertTrue(subwayRouteStationRepository.findById("T001").isEmpty)
    }

    @Test
    @DisplayName("DELETE_SUBWAY_ROUTE_STATION_NOT_FOUND")
    fun deleteSubwayRouteStationNotFound() {
        assertTrue(assertThrows<Exception> {
            subwayService.deleteSubwayRouteStation("T004")
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_SUBWAY_ROUTE_STATION_REALTIME_REFERENCES_ROUTE_STATION")
    fun deleteSubwayRouteStationRealtimeReferencesRouteStation() {
        assertTrue(assertThrows<Exception> {
            subwayService.deleteSubwayRouteStation("T002")
        }.message == "REALTIME_REFERENCES_ROUTE_STATION")
    }

    @Test
    @DisplayName("DELETE_SUBWAY_ROUTE_STATION_TIMETABLE_REFERENCES_ROUTE_STATION")
    fun deleteSubwayRouteStationTimetableReferencesRouteStation() {
        assertTrue(assertThrows<Exception> {
            subwayService.deleteSubwayRouteStation("T001")
        }.message == "TIMETABLE_REFERENCES_ROUTE_STATION")
    }

    @Test
    @DisplayName("GET_SUBWAY_TIMETABLE_LIST")
    fun getSubwayTimetableList() {
        assertTrue(subwayService.getSubwayTimetableList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_SUBWAY_TIMETABLE_LIST_BY_STATION_ID")
    fun getSubwayTimetableListByStationID() {
        assertTrue(subwayService.getSubwayTimetableListByStationID("T001").isNotEmpty())
    }

    @Test
    @DisplayName("GET_SUBWAY_TIMETABLE_LIST_BY_STATION_ID_AND_HEADING")
    fun getSubwayTimetableListByStationIDAndHeading() {
        assertTrue(subwayService.getSubwayTimetableListByStationIDAndHeading("T001", "up").isNotEmpty())
    }

    @Test
    @DisplayName("GET_SUBWAY_TIMETABLE_LIST_BY_STATION_ID_AND_HEADING_AND_WEEKDAYS")
    fun getSubwayTimetableListByStationIDAndHeadingAndWeekdays() {
        assertTrue(subwayService.getSubwayTimetableListByStationIDAndHeadingAndWeekdays("T001", "up", "weekends").isNotEmpty())
    }

    @Test
    @DisplayName("POST_SUBWAY_TIMETABLE_SUCCESS")
    fun postSubwayTimetableSuccess() {
        subwayService.postSubwayTimetable("T001", "T001", "T003", "up", "weekends", LocalTime.parse("00:30"))
        assertTrue(subwayTimetableRepository.existsById(SubwayTimetablePK(stationID = "T001", heading = "up", weekdays = "weekends", departureTime = LocalTime.parse("00:30"))))

        subwayTimetableRepository.deleteById(SubwayTimetablePK(stationID = "T001", heading = "up", weekdays = "weekends", departureTime = LocalTime.parse("00:30")))
    }

    @Test
    @DisplayName("POST_SUBWAY_TIMETABLE_DUPLICATED")
    fun postSubwayTimetableDuplicated() {
        assertTrue(assertThrows<Exception> {
            subwayService.postSubwayTimetable("T001", "T001", "T003", "up", "weekends", LocalTime.parse("00:00"))
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("POST_SUBWAY_TIMETABLE_ROUTE_STATION_NOT_FOUND")
    fun postSubwayTimetableRouteStationNotFound() {
        assertTrue(assertThrows<Exception> {
            subwayService.postSubwayTimetable("T004", "T001", "T003", "up", "weekends", LocalTime.parse("00:30"))
        }.message == "ROUTE_STATION_NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_SUBWAY_TIMETABLE_SUCCESS")
    fun deleteSubwayTimetableSuccess() {
        subwayService.deleteSubwayTimetable("T001", "up", "weekends", LocalTime.parse("00:00"))
        assertTrue(subwayTimetableRepository.findById(SubwayTimetablePK(stationID = "T001", heading = "up", weekdays = "weekends", departureTime = LocalTime.parse("00:00"))).isEmpty)
    }

    @Test
    @DisplayName("DELETE_SUBWAY_TIMETABLE_NOT_FOUND")
    fun deleteSubwayTimetableNotFound() {
        assertTrue(assertThrows<Exception> {
            subwayService.deleteSubwayTimetable("T004", "up", "weekends", LocalTime.parse("00:30"))
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("GET_SUBWAY_REALTIME_LIST")
    fun getSubwayRealtimeList() {
        assertTrue(subwayService.getSubwayRealtimeList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_SUBWAY_REALTIME_LIST_BY_STATION_ID")
    fun getSubwayRealtimeListByStationID() {
        assertTrue(subwayService.getSubwayRealtimeListByStationID("T002").isNotEmpty())
    }

    @Test
    @DisplayName("GET_SUBWAY_REALTIME_LIST_BY_STATION_ID_AND_HEADING")
    fun getSubwayRealtimeListByStationIDAndHeading() {
        assertTrue(subwayService.getSubwayRealtimeListByStationIDAndHeading("T002", "up").isNotEmpty())
    }

    @Test
    @DisplayName("POST_SUBWAY_REALTIME_SUCCESS")
    fun postSubwayRealtimeSuccess() {
        subwayService.postSubwayRealtime(stationID = "T002", seq = 3, heading = "up", currentStation = "T003", remainingStop = 1, remainingTime = Duration.ofMinutes(55), terminalStationID = "T003", trainNumber = "K007", express = false, last = false, status = 0)
        assertTrue(subwayRealtimeRepository.existsById(SubwayRealtimePK(stationID = "T002", seq = 3, heading = "up")))
        subwayRealtimeRepository.deleteById(SubwayRealtimePK(stationID = "T002", seq = 3, heading = "up"))
    }

    @Test
    @DisplayName("DELETE_SUBWAY_REALTIME_SUCCESS")
    fun deleteSubwayRealtimeSuccess() {
        subwayService.deleteSubwayRealtime("T002", "up", 0)
        assertTrue(subwayRealtimeRepository.findById(SubwayRealtimePK(stationID = "T002", seq = 0, heading = "up")).isEmpty)
    }

    @Test
    @DisplayName("DELETE_SUBWAY_REALTIME_NOT_FOUND")
    fun deleteSubwayRealtimeNotFound() {
        assertTrue(assertThrows<Exception> {
            subwayService.deleteSubwayRealtime("T002", "up", 3)
        }.message == "NOT_FOUND")
    }
}