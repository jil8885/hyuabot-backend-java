package app.hyuabot.backend.service

import app.hyuabot.backend.domain.shuttle.*
import app.hyuabot.backend.dto.database.ShuttlePeriodPK
import app.hyuabot.backend.dto.database.ShuttleRouteStopPK
import app.hyuabot.backend.dto.request.shuttle.PatchRouteRequest
import app.hyuabot.backend.dto.request.shuttle.PatchRouteStopRequest
import app.hyuabot.backend.dto.request.shuttle.PatchStopRequest
import app.hyuabot.backend.dto.request.shuttle.PatchTimetableRequest
import app.hyuabot.backend.dto.response.ShuttleHolidayItem
import app.hyuabot.backend.repository.shuttle.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.context.ActiveProfiles
import java.lang.Exception
import java.lang.NullPointerException
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@ActiveProfiles("test")
class ShuttleServiceTest {
    @Autowired private lateinit var shuttleService: ShuttleService
    @Autowired private lateinit var shuttleHolidayRepository: HolidayRepository
    @Autowired private lateinit var shuttlePeriodTypeRepository: PeriodTypeRepository
    @Autowired private lateinit var shuttlePeriodRepository: PeriodRepository
    @Autowired private lateinit var shuttleRouteRepository: RouteRepository
    @Autowired private lateinit var shuttleRouteStopRepository: RouteStopRepository
    @Autowired private lateinit var shuttleStopRepository: StopRepository
    @Autowired private lateinit var shuttleTimetableRepository: TimetableRepository

    private val testDate = LocalDate.of(2999, 1, 1)
    private val testStartDateTime = testDate.atTime(0, 0)
    private val testEndDateTime = testDate.atTime(23, 59)
    @BeforeEach
    fun init() {
        shuttleHolidayRepository.save(ShuttleHoliday(testDate, "weekends", "test"))
        shuttlePeriodTypeRepository.save(ShuttlePeriodType("test"))
        shuttlePeriodRepository.save(ShuttlePeriod("test", testStartDateTime, testEndDateTime))
        shuttleStopRepository.save(ShuttleStop("startStop", 0.0, 0.0))
        shuttleStopRepository.save(ShuttleStop("endStop", 0.0, 0.0))
        shuttleRouteRepository.save(ShuttleRoute("test", "test", "test", "test", "startStop", "endStop"))
        shuttleRouteStopRepository.save(ShuttleRouteStop("test", "startStop", 0, Duration.ofMinutes(0)))
        shuttleRouteStopRepository.save(ShuttleRouteStop("test", "endStop", 1, Duration.ofMinutes(10)))
        shuttleTimetableRepository.save(ShuttleTimetable(9999, "test", true, "test", LocalTime.now()))
    }

    @AfterEach
    fun clean() {
        shuttleTimetableRepository.deleteAll(shuttleTimetableRepository.findAllByRouteNameAndPeriodType("test", "test"))
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "startStop"))
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "endStop"))
        shuttleRouteRepository.deleteById("test")
        shuttleStopRepository.deleteById("startStop")
        shuttleStopRepository.deleteById("endStop")
        shuttleHolidayRepository.deleteById(testDate)
        shuttlePeriodRepository.deleteById(ShuttlePeriodPK("test", testStartDateTime, testEndDateTime))
        shuttlePeriodTypeRepository.deleteById("test")
    }

    @Test
    @DisplayName("GET_SHUTTLE_HOLIDAY_SUCCESS")
    fun testGetShuttleHoliday() {
        assertTrue(shuttleService.getShuttleHoliday().isNotEmpty())
    }

    @Test
    @DisplayName("POST_SHUTTLE_HOLIDAY_FAIL_DUPLICATED")
    fun testPostShuttleHolidayDuplicated() {
        assertTrue(
            assertThrows<Exception> {
                shuttleService.postShuttleHoliday(
                    ShuttleHolidayItem(
                        testDate.toString(),
                        "weekends",
                        "test"
                    )
                )
            }.message == "DUPLICATED"
        )
    }

    @Test
    @DisplayName("POST_SHUTTLE_HOLIDAY_SUCCESS")
    fun testPostShuttleHolidaySuccess() {
        val previousSize = shuttleHolidayRepository.findAll().size
        shuttleService.postShuttleHoliday(ShuttleHolidayItem((testDate.plusDays(10)).toString(), "weekends", "test"))
        assertTrue(shuttleHolidayRepository.findAll().size == previousSize + 1)
        shuttleHolidayRepository.deleteById(testDate.plusDays(10))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_HOLIDAY_SUCCESS")
    fun testDeleteShuttleHoliday() {
        val previousSize = shuttleHolidayRepository.findAll().size
        shuttleService.deleteShuttleHoliday(testDate.toString())
        assertTrue(shuttleHolidayRepository.findAll().size == previousSize - 1)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_HOLIDAY_FAIL_NOT_FOUND")
    fun testDeleteShuttleHolidayFail() {
        shuttleHolidayRepository.deleteById(testDate)
        assertTrue(
            assertThrows<NullPointerException> {
                shuttleService.deleteShuttleHoliday(testDate.toString())
            }.message == "SPECIFIED_DATE_NOT_FOUND"
        )
    }

    @Test
    @DisplayName("GET_SHUTTLE_PERIOD_SUCCESS")
    fun testGetShuttlePeriod() {
        assertTrue(shuttleService.getShuttlePeriod().isNotEmpty())
    }

    @Test
    @DisplayName("POST_SHUTTLE_PERIOD")
    fun testPostShuttlePeriod() {
        val previousSize = shuttlePeriodRepository.findAll().size
        // Test success
        shuttleService.postShuttlePeriod(ShuttlePeriod(
            "test",
            testStartDateTime.plusDays(10),
            testEndDateTime.plusDays(10),
        ))
        assertTrue(shuttlePeriodRepository.findAll().size == previousSize + 1)
        shuttlePeriodRepository.deleteById(ShuttlePeriodPK("test", testStartDateTime.plusDays(10), testEndDateTime.plusDays(10)))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_PERIOD_SUCCESS")
    fun testDeleteShuttlePeriod() {
        val previousSize = shuttlePeriodRepository.findAll().size
        shuttleTimetableRepository.deleteAll(
            shuttleTimetableRepository.findAllByRouteNameAndPeriodType(
                "test",
                "test"
            )
        )
        shuttleService.deleteShuttlePeriod(
            ShuttlePeriodPK(
                "test",
                testStartDateTime,
                testEndDateTime
            )
        )
        assertTrue(shuttlePeriodRepository.findAll().size == previousSize - 1)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_PERIOD_FAIL_NOT_FOUND")
    fun testDeleteShuttlePeriodFail() {
        shuttlePeriodRepository.deleteById(ShuttlePeriodPK("test", testStartDateTime, testEndDateTime))
        assertTrue(
            assertThrows<NullPointerException> {
                shuttleService.deleteShuttlePeriod(ShuttlePeriodPK("test", testStartDateTime, testEndDateTime))
            }.message == "SPECIFIED_PERIOD_ITEM_NOT_FOUND"
        )
    }

    @Test
    @DisplayName("GET_SHUTTLE_ROUTE_SUCCESS")
    fun testGetShuttleRoute() {
        assertTrue(shuttleService.getShuttleRoute().isNotEmpty())
    }

    @Test
    @DisplayName("POST_SHUTTLE_ROUTE_FAIL_DUPLICATED")
    fun testPostShuttleRoute() {
        assertTrue(
            assertThrows<Exception> {
                shuttleService.postShuttleRoute(
                    ShuttleRoute(
                        "test",
                        "test",
                        "test",
                        "test",
                        "startStop",
                        "endStop"
                    )
                )
            }.message == "DUPLICATED"
        )
    }

    @Test
    fun testPostShuttleRouteSuccess() {
        val previousSize = shuttleRouteRepository.findAll().size
        shuttleService.postShuttleRoute(ShuttleRoute("test1", "test", "test", "test", "startStop", "endStop"))
        assertTrue(shuttleRouteRepository.findAll().size == previousSize + 1)
        shuttleRouteRepository.deleteById("test1")
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_ROUTE_FAIL_INTEGRITY_VIOLATION")
    fun testDeleteShuttleRouteFail() {
        // Should delete all related data
        assertThrows<DataIntegrityViolationException> {
            shuttleService.deleteShuttleRoute("test")
        }
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_ROUTE_SUCCESS")
    fun testDeleteShuttleRouteSuccess() {
        val previousSize = shuttleRouteRepository.findAll().size
        shuttleTimetableRepository.deleteAll(
            shuttleTimetableRepository.findAllByRouteNameAndPeriodType(
                "test",
                "test"
            )
        )
        shuttleRouteStopRepository.deleteById(
            ShuttleRouteStopPK(
                "test",
                "startStop"
            )
        )
        shuttleRouteStopRepository.deleteById(
            ShuttleRouteStopPK(
                "test",
                "endStop"
            )
        )
        shuttleService.deleteShuttleRoute("test")
        assertTrue(shuttleRouteRepository.findAll().size == previousSize - 1)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_ROUTE_FAIL_NOT_FOUND")
    fun testDeleteShuttleRouteFailNotFound() {
        shuttleTimetableRepository.deleteAll(
            shuttleTimetableRepository.findAllByRouteNameAndPeriodType(
                "test",
                "test"
            )
        )
        shuttleRouteStopRepository.deleteById(
            ShuttleRouteStopPK(
                "test",
                "startStop"
            )
        )
        shuttleRouteStopRepository.deleteById(
            ShuttleRouteStopPK(
                "test",
                "endStop"
            )
        )
        shuttleRouteRepository.deleteById("test")
        assertTrue(
            assertThrows<NullPointerException> {
                shuttleService.deleteShuttleRoute("test")
            }.message == "SPECIFIED_ROUTE_NOT_FOUND"
        )
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_DESCRIPTION_KOREAN_SUCCESS")
    fun testPatchShuttleRouteDescriptionKorean() {
        shuttleService.patchShuttleRoute("test", PatchRouteRequest(
            routeDescriptionKorean = "test1"
        ))
        assertTrue(shuttleRouteRepository.findById("test").get().routeDescriptionKorean == "test1")
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_DESCRIPTION_ENGLISH_SUCCESS")
    fun testPatchShuttleRouteDescriptionEnglish() {
        shuttleService.patchShuttleRoute(
            "test",
            PatchRouteRequest(
                routeDescriptionEnglish = "test2"
            )
        )
        assertTrue(shuttleRouteRepository.findById("test")
                .get().routeDescriptionEnglish == "test2"
        )
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_TYPE_SUCCESS")
    fun testPatchShuttleRouteType() {
        shuttleService.patchShuttleRoute(
            "test", PatchRouteRequest(
                routeType = "test3"
            )
        )
        assertTrue(shuttleRouteRepository.findById("test").get().routeType == "test3")
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_START_STOP_SUCCESS")
    fun testPatchShuttleRouteStartStop() {
        shuttleService.patchShuttleRoute(
            "test", PatchRouteRequest(
                startStop = "endStop"
            )
        )
        assertTrue(shuttleRouteRepository.findById("test").get().startStop == "endStop"
        )
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_END_STOP_SUCCESS")
    fun testPatchShuttleRouteEndStop() {
        shuttleService.patchShuttleRoute("test", PatchRouteRequest(
            endStop = "startStop"
        ))
        assertTrue(shuttleRouteRepository.findById("test").get().endStop == "startStop")
    }

    @Test
    fun testGetShuttleStop() {
        assertTrue(shuttleService.getShuttleStop().isNotEmpty())
    }

    @Test
    @DisplayName("POST_SHUTTLE_STOP_FAIL_DUPLICATED")
    fun testPostShuttleStop() {
        assertTrue(
            assertThrows<Exception> {
                shuttleService.postShuttleStop(ShuttleStop("startStop", 0.0, 0.0))
            }.message == "DUPLICATED"
        )
    }

    @Test
    @DisplayName("POST_SHUTTLE_STOP_SUCCESS")
    fun testPostShuttleStopSuccess() {
        val previousSize = shuttleStopRepository.findAll().size
        shuttleService.postShuttleStop(ShuttleStop("test", 0.0, 0.0))
        assertTrue(shuttleStopRepository.findAll().size == previousSize + 1)
        shuttleStopRepository.deleteById("test")
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_STOP_FAIL_INTEGRITY_VIOLATION")
    fun testDeleteShuttleStop() {
        assertThrows<DataIntegrityViolationException> {
            shuttleService.deleteShuttleStop("startStop")
        }
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_STOP_SUCCESS")
    fun testDeleteShuttleStopSuccess() {
        val previousSize = shuttleStopRepository.findAll().size
        shuttleTimetableRepository.deleteAll(shuttleTimetableRepository.findAllByRouteNameAndPeriodType("test", "test"))
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "startStop"))
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "endStop"))
        shuttleRouteRepository.deleteById("test")
        shuttleService.deleteShuttleStop("startStop")
        assertTrue(shuttleStopRepository.findAll().size == previousSize - 1)
        // Test not found
        assertTrue(
            assertThrows<NullPointerException> {
                shuttleService.deleteShuttleStop("startStop")
            }.message == "SPECIFIED_STOP_NOT_FOUND"
        )
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_STOP_SUCCESS")
    fun testPatchShuttleStop() {
        // 1. Change latitude
        shuttleService.patchShuttleStop("startStop", PatchStopRequest(
            latitude = 1.0
        ))
        assertTrue(shuttleStopRepository.findById("startStop").get().latitude == 1.0)
        // 2. Change longitude
        shuttleService.patchShuttleStop("startStop", PatchStopRequest(
            longitude = 1.0
        ))
        assertTrue(shuttleStopRepository.findById("startStop").get().longitude == 1.0)
    }

    @Test
    @DisplayName("GET_SHUTTLE_ROUTE_STOP_SUCCESS")
    fun testGetShuttleRouteStop() {
        assertTrue(shuttleService.getShuttleRouteStop("test").isNotEmpty())
    }

    @Test
    @DisplayName("POST_SHUTTLE_ROUTE_STOP_FAIL_DUPLICATED")
    fun testPostShuttleRouteStop() {
        assertTrue(
            assertThrows<Exception> {
                shuttleService.postShuttleRouteStop(
                    ShuttleRouteStop(
                        "test",
                        "startStop",
                        0,
                        Duration.ofMinutes(0)
                    )
                )
            }.message == "DUPLICATED"
        )
    }

    @Test
    @DisplayName("POST_SHUTTLE_ROUTE_STOP_SUCCESS")
    fun testPostShuttleRouteStopSuccess() {
        val previousSize = shuttleRouteStopRepository.findAll().size
        shuttleStopRepository.save(ShuttleStop("test", 0.0, 0.0))
        shuttleService.postShuttleRouteStop(ShuttleRouteStop("test", "test", 2, Duration.ofMinutes(20)))
        assertTrue(shuttleRouteStopRepository.findAll().size == previousSize + 1)
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "test"))
        shuttleStopRepository.deleteById("test")
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_ROUTE_STOP_SUCCESS")
    fun testDeleteShuttleRouteStop() {
        val previousSize = shuttleRouteStopRepository.findAll().size
        shuttleService.deleteShuttleRouteStop("test", "startStop")
        assertTrue(shuttleRouteStopRepository.findAll().size == previousSize - 1)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_ROUTE_STOP_FAIL_NOT_FOUND")
    fun testDeleteShuttleRouteStopFail() {
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "startStop"))
        assertTrue(
            assertThrows<NullPointerException> {
                shuttleService.deleteShuttleRouteStop("test", "startStop")
            }.message == "SPECIFIED_ROUTE_STOP_NOT_FOUND"
        )
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_STOP_SUCCESS")
    fun testPatchShuttleRouteStop() {
        // 1. Change seq
        shuttleService.patchShuttleRouteStop("test", "startStop", PatchRouteStopRequest(
            seq = 1
        ))
        assertTrue(shuttleRouteStopRepository.findById(ShuttleRouteStopPK("test", "startStop")).get().seq == 1)
        // 2. Change cumulative time
        shuttleService.patchShuttleRouteStop("test", "startStop", PatchRouteStopRequest(
            cumulativeTime = Duration.ofMinutes(10).toString()
        ))
        assertTrue(shuttleRouteStopRepository.findById(ShuttleRouteStopPK("test", "startStop")).get().cumulativeTime == Duration.ofMinutes(10))
    }

    @Test
    @DisplayName("GET_SHUTTLE_TIMETABLE_SUCCESS")
    fun testGetShuttleTimetable() {
        assertTrue(shuttleService.getShuttleTimetable().isNotEmpty())
    }

    @Test
    @DisplayName("POST_SHUTTLE_TIMETABLE_FAIL_DUPLICATED")
    fun testPostShuttleTimetable() {
        assertTrue(
            assertThrows<Exception> {
                shuttleService.postShuttleTimetable(
                    ShuttleTimetable(
                        9999,
                        "test",
                        true,
                        "test",
                        LocalTime.now()
                    )
                )
            }.message == "DUPLICATED"
        )
    }

    @Test
    @DisplayName("POST_SHUTTLE_TIMETABLE_SUCCESS")
    fun testPostShuttleTimetableSuccess() {
        val previousSize = shuttleTimetableRepository.findAll().size
        shuttleService.postShuttleTimetable(ShuttleTimetable(10000, "test", true, "test", LocalTime.now().plusHours(1)))
        assertTrue(shuttleTimetableRepository.findAll().size == previousSize + 1)
        shuttleTimetableRepository.deleteAll(shuttleTimetableRepository.findAllByRouteNameAndPeriodType("test", "test"))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_TIMETABLE_SUCCESS")
    fun testDeleteShuttleTimetable() {
        val previousSize = shuttleTimetableRepository.findAll().size
        shuttleService.deleteShuttleTimetable(9999)
        assertTrue(shuttleTimetableRepository.findAll().size == previousSize - 1)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_TIMETABLE_FAIL_NOT_FOUND")
    fun testDeleteShuttleTimetableFail() {
        shuttleTimetableRepository.deleteById(9999)
        assertTrue(
            assertThrows<NullPointerException> {
                shuttleService.deleteShuttleTimetable(9999)
            }.message == "SPECIFIED_SEQ_NOT_FOUND"
        )
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_TIMETABLE_SUCCESS")
    fun testPatchShuttleTimetable() {
        // 1. Change period type
        shuttleService.patchShuttleTimetable(9999, PatchTimetableRequest(
            periodType = "test"
        ))
        assertTrue(shuttleTimetableRepository.findById(9999).get().periodType == "test")
        // 2. Change is weekday
        shuttleService.patchShuttleTimetable(9999, PatchTimetableRequest(
            isWeekday = false
        ))
        assertTrue(shuttleTimetableRepository.findById(9999).get().isWeekday == false)
        // 3. Change route name
        shuttleService.patchShuttleTimetable(9999, PatchTimetableRequest(
            routeName = "test"
        ))
        assertTrue(shuttleTimetableRepository.findById(9999).get().routeName == "test")
        // 4. Change departure time
        shuttleService.patchShuttleTimetable(9999, PatchTimetableRequest(
            departureTime = "12:00:00"
        ))
        assertTrue(shuttleTimetableRepository.findById(9999).get().departureTime == LocalTime.parse("12:00:00"))
    }

    @Test
    @DisplayName("GET_SHUTTLE_TIMETABLE_VIEW_SUCCESS")
    fun testGetShuttleTimetableView() {
        assertTrue(shuttleService.getShuttleTimetableView(
            "test",
            true,
            "test",
            3,
        ).size > 0)
    }
}