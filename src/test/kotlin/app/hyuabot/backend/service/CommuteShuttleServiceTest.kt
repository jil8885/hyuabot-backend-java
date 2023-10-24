package app.hyuabot.backend.service

import app.hyuabot.backend.domain.commute.CommuteShuttleRoute
import app.hyuabot.backend.domain.commute.CommuteShuttleStop
import app.hyuabot.backend.domain.commute.CommuteShuttleTimetable
import app.hyuabot.backend.dto.database.CommuteShuttleTimetablePK
import app.hyuabot.backend.repository.commute.CommuteShuttleRouteRepository
import app.hyuabot.backend.repository.commute.CommuteShuttleStopRepository
import app.hyuabot.backend.repository.commute.CommuteShuttleTimetableRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalTime

@SpringBootTest
@ActiveProfiles("test")
class CommuteShuttleServiceTest {
    @Autowired private lateinit var commuteShuttleService: CommuteShuttleService
    @Autowired private lateinit var commuteShuttleRouteRepository: CommuteShuttleRouteRepository
    @Autowired private lateinit var commuteShuttleStopRepository: CommuteShuttleStopRepository
    @Autowired private lateinit var commuteShuttleTimetableRepository: CommuteShuttleTimetableRepository

    @BeforeEach
    fun init() {
        clean()
        commuteShuttleRouteRepository.saveAll(listOf(
            CommuteShuttleRoute(name = "TEST1", descriptionKorean = "TEST1", descriptionEnglish = "TEST1"),
            CommuteShuttleRoute(name = "TEST2", descriptionKorean = "TEST2", descriptionEnglish = "TEST2"),
            CommuteShuttleRoute(name = "TEST3", descriptionKorean = "TEST3", descriptionEnglish = "TEST3"),
        ))
        commuteShuttleStopRepository.saveAll(listOf(
            CommuteShuttleStop(name = "TEST1", latitude = 0.0, longitude = 0.0, description = "TEST1"),
            CommuteShuttleStop(name = "TEST2", latitude = 0.0, longitude = 0.0, description = "TEST2"),
            CommuteShuttleStop(name = "TEST3", latitude = 0.0, longitude = 0.0, description = "TEST3"),
        ))
        commuteShuttleTimetableRepository.saveAll(listOf(
            CommuteShuttleTimetable(stopName = "TEST1", routeName = "TEST1", seq = 0, departureTime = LocalTime.parse("07:00")),
            CommuteShuttleTimetable(stopName = "TEST2", routeName = "TEST1", seq = 1, departureTime = LocalTime.parse("07:05")),
            CommuteShuttleTimetable(stopName = "TEST3", routeName = "TEST1", seq = 2, departureTime = LocalTime.parse("07:10")),
        ))
    }

    @AfterEach
    fun clean() {
        commuteShuttleTimetableRepository.deleteAllInBatch(listOf(
            CommuteShuttleTimetable(stopName = "TEST1", routeName = "TEST1", seq = 0, departureTime = LocalTime.parse("07:00")),
            CommuteShuttleTimetable(stopName = "TEST2", routeName = "TEST1", seq = 1, departureTime = LocalTime.parse("07:05")),
            CommuteShuttleTimetable(stopName = "TEST3", routeName = "TEST1", seq = 2, departureTime = LocalTime.parse("07:10")),
        ))
        commuteShuttleStopRepository.deleteAllInBatch(listOf(
            CommuteShuttleStop(name = "TEST1", latitude = 0.0, longitude = 0.0, description = "TEST1"),
            CommuteShuttleStop(name = "TEST2", latitude = 0.0, longitude = 0.0, description = "TEST2"),
            CommuteShuttleStop(name = "TEST3", latitude = 0.0, longitude = 0.0, description = "TEST3"),
        ))
        commuteShuttleRouteRepository.deleteAllInBatch(listOf(
            CommuteShuttleRoute(name = "TEST1", descriptionKorean = "TEST1", descriptionEnglish = "TEST1"),
            CommuteShuttleRoute(name = "TEST2", descriptionKorean = "TEST2", descriptionEnglish = "TEST2"),
            CommuteShuttleRoute(name = "TEST3", descriptionKorean = "TEST3", descriptionEnglish = "TEST3"),
        ))
    }

    @Test
    @DisplayName("GET_COMMUTE_SHUTTLE_ROUTE_LIST")
    fun getShuttleRouteList() {
        val result = commuteShuttleService.getShuttleRouteList()
        assertTrue(result.isNotEmpty())
    }

    @Test
    @DisplayName("GET_COMMUTE_SHUTTLE_STOP_LIST")
    fun getShuttleStopList() {
        val result = commuteShuttleService.getShuttleStopList()
        assertTrue(result.isNotEmpty())
    }

    @Test
    @DisplayName("GET_COMMUTE_SHUTTLE_TIMETABLE_LIST")
    fun getShuttleTimetableList() {
        val result = commuteShuttleService.getShuttleTimetableList()
        assertTrue(result.isNotEmpty())
    }

    @Test
    @DisplayName("GET_COMMUTE_SHUTTLE_TIMETABLE_LIST_BY_ROUTE")
    fun getShuttleTimetableListByRoute() {
        val result = commuteShuttleService.getShuttleTimetableListByRoute("TEST1")
        assertTrue(result.size == 3)
        assertTrue(result[0].stopName == "TEST1")
        assertTrue(result[1].stopName == "TEST2")
        assertTrue(result[2].stopName == "TEST3")
    }

    @Test
    @DisplayName("GET_COMMUTE_SHUTTLE_TIMETABLE_LIST_BY_STOP")
    fun getShuttleTimetableListByStop() {
        val result = commuteShuttleService.getShuttleTimetableListByStop("TEST1")
        assertTrue(result.size == 1)
        assertTrue(result[0].stopName == "TEST1")
    }

    @Test
    @DisplayName("GET_COMMUTE_SHUTTLE_TIMETABLE_LIST_BY_ROUTE_AND_STOP")
    fun getShuttleTimetableListByRouteAndStop() {
        val result = commuteShuttleService.getShuttleTimetableListByRouteAndStop("TEST1", "TEST1")
        assertTrue(result.get().stopName == "TEST1")
        assertTrue(result.get().routeName == "TEST1")
    }

    @Test
    @DisplayName("POST_COMMUTE_SHUTTLE_ROUTE")
    fun postShuttleRoute() {
        commuteShuttleService.postShuttleRoute("TEST4", "TEST4", "TEST4")
        val result = commuteShuttleRouteRepository.findById("TEST4")
        assertTrue(result.isPresent)
        assertTrue(result.get().name == "TEST4")
        assertTrue(result.get().descriptionKorean == "TEST4")
        assertTrue(result.get().descriptionEnglish == "TEST4")
        commuteShuttleRouteRepository.deleteById("TEST4")
    }

    @Test
    @DisplayName("POST_COMMUTE_SHUTTLE_ROUTE_DUPLICATED")
    fun postShuttleRouteDuplicated() {
        assertTrue(assertThrows<Exception> {
            commuteShuttleService.postShuttleRoute("TEST1", "TEST1", "TEST1")
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("POST_COMMUTE_SHUTTLE_STOP")
    fun postShuttleStop() {
        commuteShuttleService.postShuttleStop("TEST4", 0.0, 0.0, "TEST4")
        val result = commuteShuttleStopRepository.findById("TEST4")
        assertTrue(result.isPresent)
        assertTrue(result.get().name == "TEST4")
        assertTrue(result.get().latitude == 0.0)
        assertTrue(result.get().longitude == 0.0)
        assertTrue(result.get().description == "TEST4")
        commuteShuttleStopRepository.deleteById("TEST4")
    }

    @Test
    @DisplayName("POST_COMMUTE_SHUTTLE_STOP_DUPLICATED")
    fun postShuttleStopDuplicated() {
        assertTrue(assertThrows<Exception> {
            commuteShuttleService.postShuttleStop("TEST1", 0.0, 0.0, "TEST1")
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("POST_COMMUTE_SHUTTLE_TIMETABLE")
    fun postShuttleTimetable() {
        commuteShuttleStopRepository.save(CommuteShuttleStop(name = "TEST4", latitude = 0.0, longitude = 0.0, description = "TEST4"))
        commuteShuttleService.postShuttleTimetable("TEST1", "TEST4", 3, LocalTime.parse("07:15"))
        val result = commuteShuttleTimetableRepository.findById(CommuteShuttleTimetablePK(stopName = "TEST4", routeName = "TEST1"))
        assertTrue(result.isPresent)
        assertTrue(result.get().stopName == "TEST4")
        assertTrue(result.get().routeName == "TEST1")
        assertTrue(result.get().seq == 3)
        assertTrue(result.get().departureTime == LocalTime.parse("07:15"))
        commuteShuttleTimetableRepository.deleteById(CommuteShuttleTimetablePK(stopName = "TEST4", routeName = "TEST1"))
        commuteShuttleStopRepository.deleteById("TEST4")
    }

    @Test
    @DisplayName("POST_COMMUTE_SHUTTLE_TIMETABLE_DUPLICATED")
    fun postShuttleTimetableDuplicated() {
        assertTrue(assertThrows<Exception> {
            commuteShuttleService.postShuttleTimetable("TEST1", "TEST1", 0, LocalTime.parse("07:00"))
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("PATCH_COMMUTE_SHUTTLE_ROUTE")
    fun patchShuttleRoute() {
        commuteShuttleService.patchShuttleRoute("TEST1", "TEST4", "TEST4")
        val result = commuteShuttleRouteRepository.findById("TEST1")
        assertTrue(result.isPresent)
        assertTrue(result.get().name == "TEST1")
        assertTrue(result.get().descriptionKorean == "TEST4")
        assertTrue(result.get().descriptionEnglish == "TEST4")
    }

    @Test
    @DisplayName("PATCH_COMMUTE_SHUTTLE_ROUTE_NOT_FOUND")
    fun patchShuttleRouteNotFound() {
        assertTrue(assertThrows<Exception> {
            commuteShuttleService.patchShuttleRoute("TEST4", "TEST4", "TEST4")
        }.message == "SPECIFIED_ROUTE_NOT_FOUND")
    }

    @Test
    @DisplayName("PATCH_COMMUTE_SHUTTLE_STOP")
    fun patchShuttleStop() {
        commuteShuttleService.patchShuttleStop("TEST1", 0.0, 0.0, "TEST4")
        val result = commuteShuttleStopRepository.findById("TEST1")
        assertTrue(result.isPresent)
        assertTrue(result.get().name == "TEST1")
        assertTrue(result.get().latitude == 0.0)
        assertTrue(result.get().longitude == 0.0)
        assertTrue(result.get().description == "TEST4")
    }

    @Test
    @DisplayName("PATCH_COMMUTE_SHUTTLE_STOP_NOT_FOUND")
    fun patchShuttleStopNotFound() {
        assertTrue(assertThrows<Exception> {
            commuteShuttleService.patchShuttleStop("TEST4", 0.0, 0.0, "TEST4")
        }.message == "SPECIFIED_STOP_NOT_FOUND")
    }

    @Test
    @DisplayName("PATCH_COMMUTE_SHUTTLE_TIMETABLE")
    fun patchShuttleTimetable() {
        commuteShuttleService.patchShuttleTimetable("TEST1", "TEST1", 3, LocalTime.parse("07:15"))
        val result = commuteShuttleTimetableRepository.findById(CommuteShuttleTimetablePK(stopName = "TEST1", routeName = "TEST1"))
        assertTrue(result.isPresent)
        assertTrue(result.get().stopName == "TEST1")
        assertTrue(result.get().routeName == "TEST1")
        assertTrue(result.get().seq == 3)
        assertTrue(result.get().departureTime == LocalTime.parse("07:15"))
    }

    @Test
    @DisplayName("PATCH_COMMUTE_SHUTTLE_TIMETABLE_NOT_FOUND")
    fun patchShuttleTimetableNotFound() {
        assertTrue(assertThrows<Exception> {
            commuteShuttleService.patchShuttleTimetable("TEST4", "TEST4", 0, LocalTime.parse("07:00"))
        }.message == "SPECIFIED_TIMETABLE_NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_COMMUTE_SHUTTLE_ROUTE")
    fun deleteShuttleRoute() {
        commuteShuttleTimetableRepository.deleteAllInBatch(listOf(
            CommuteShuttleTimetable(stopName = "TEST1", routeName = "TEST1", seq = 0, departureTime = LocalTime.parse("07:00")),
            CommuteShuttleTimetable(stopName = "TEST2", routeName = "TEST1", seq = 1, departureTime = LocalTime.parse("07:05")),
            CommuteShuttleTimetable(stopName = "TEST3", routeName = "TEST1", seq = 2, departureTime = LocalTime.parse("07:10")),
        ))
        commuteShuttleService.deleteShuttleRoute("TEST1")
        val result = commuteShuttleRouteRepository.findById("TEST1")
        assertTrue(!result.isPresent)
    }

    @Test
    @DisplayName("DELETE_COMMUTE_SHUTTLE_ROUTE_HAS_TIMETABLE")
    fun deleteShuttleRouteHasTimetable() {
        assertTrue(assertThrows<Exception> {
            commuteShuttleService.deleteShuttleRoute("TEST1")
        }.message == "ROUTE_HAS_TIMETABLE")
    }

    @Test
    @DisplayName("DELETE_COMMUTE_SHUTTLE_ROUTE_NOT_FOUND")
    fun deleteShuttleRouteNotFound() {
        assertTrue(assertThrows<Exception> {
            commuteShuttleService.deleteShuttleRoute("TEST4")
        }.message == "ROUTE_NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_COMMUTE_SHUTTLE_STOP")
    fun deleteShuttleStop() {
        commuteShuttleTimetableRepository.deleteAllInBatch(listOf(
            CommuteShuttleTimetable(stopName = "TEST1", routeName = "TEST1", seq = 0, departureTime = LocalTime.parse("07:00")),
            CommuteShuttleTimetable(stopName = "TEST2", routeName = "TEST1", seq = 1, departureTime = LocalTime.parse("07:05")),
            CommuteShuttleTimetable(stopName = "TEST3", routeName = "TEST1", seq = 2, departureTime = LocalTime.parse("07:10")),
        ))
        commuteShuttleService.deleteShuttleStop("TEST1")
        val result = commuteShuttleStopRepository.findById("TEST1")
        assertTrue(!result.isPresent)
    }

    @Test
    @DisplayName("DELETE_COMMUTE_SHUTTLE_STOP_HAS_TIMETABLE")
    fun deleteShuttleStopHasTimetable() {
        assertTrue(assertThrows<Exception> {
            commuteShuttleService.deleteShuttleStop("TEST1")
        }.message == "STOP_HAS_TIMETABLE")
    }

    @Test
    @DisplayName("DELETE_COMMUTE_SHUTTLE_STOP_NOT_FOUND")
    fun deleteShuttleStopNotFound() {
        assertTrue(assertThrows<Exception> {
            commuteShuttleService.deleteShuttleStop("TEST4")
        }.message == "STOP_NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_COMMUTE_SHUTTLE_TIMETABLE")
    fun deleteShuttleTimetable() {
        commuteShuttleService.deleteShuttleTimetable("TEST1", "TEST1")
        val result = commuteShuttleTimetableRepository.findById(CommuteShuttleTimetablePK(stopName = "TEST1", routeName = "TEST1"))
        assertTrue(!result.isPresent)
    }

    @Test
    @DisplayName("DELETE_COMMUTE_SHUTTLE_TIMETABLE_NOT_FOUND")
    fun deleteShuttleTimetableNotFound() {
        assertTrue(assertThrows<Exception> {
            commuteShuttleService.deleteShuttleTimetable("TEST4", "TEST4")
        }.message == "TIMETABLE_NOT_FOUND")
    }
}