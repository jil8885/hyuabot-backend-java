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
        shuttleHolidayRepository.save(Holiday(testDate, "weekends", "test"))
        shuttlePeriodTypeRepository.save(PeriodType("test"))
        shuttlePeriodRepository.save(Period("test", testStartDateTime, testEndDateTime))
        shuttleStopRepository.save(Stop("startStop", 0.0, 0.0))
        shuttleStopRepository.save(Stop("endStop", 0.0, 0.0))
        shuttleRouteRepository.save(Route("test", "test", "test", "test", "startStop", "endStop"))
        shuttleRouteStopRepository.save(RouteStop("test", "startStop", 0, Duration.ofMinutes(0)))
        shuttleRouteStopRepository.save(RouteStop("test", "endStop", 1, Duration.ofMinutes(10)))
        shuttleTimetableRepository.save(Timetable(9999, "test", true, "test", LocalTime.now()))
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
    fun testGetShuttleHoliday() {
        assertTrue(shuttleService.getShuttleHoliday().isNotEmpty())
    }

    @Test
    fun testPostShuttleHoliday() {
        val previousSize = shuttleHolidayRepository.findAll().size
        // Test duplicated
        assertTrue(
            assertThrows<Exception> {
                shuttleService.postShuttleHoliday(ShuttleHolidayItem(testDate.toString(), "weekends", "test"))
            }.message == "DUPLICATED"
        )
        // Test success
        shuttleService.postShuttleHoliday(ShuttleHolidayItem((testDate.plusDays(10)).toString(), "weekends", "test"))
        assertTrue(shuttleHolidayRepository.findAll().size == previousSize + 1)
        shuttleHolidayRepository.deleteById(testDate.plusDays(10))
    }

    @Test
    fun testDeleteShuttleHoliday() {
        // Test success
        val previousSize = shuttleHolidayRepository.findAll().size
        shuttleService.deleteShuttleHoliday(testDate.toString())
        assertTrue(shuttleHolidayRepository.findAll().size == previousSize - 1)
        // Test not found
        assertTrue(
            assertThrows<NullPointerException> {
                shuttleService.deleteShuttleHoliday(testDate.toString())
            }.message == "SPECIFIED_DATE_NOT_FOUND"
        )
    }

    @Test
    fun testGetShuttlePeriod() {
        assertTrue(shuttleService.getShuttlePeriod().isNotEmpty())
    }

    @Test
    fun testPostShuttlePeriod() {
        val previousSize = shuttlePeriodRepository.findAll().size
        // Test success
        shuttleService.postShuttlePeriod(Period(
            "test",
            testStartDateTime.plusDays(10),
            testEndDateTime.plusDays(10),
        ))
        assertTrue(shuttlePeriodRepository.findAll().size == previousSize + 1)
        shuttlePeriodRepository.deleteById(ShuttlePeriodPK("test", testStartDateTime.plusDays(10), testEndDateTime.plusDays(10)))
    }

    @Test
    fun testDeleteShuttlePeriod() {
        // Test success
        val previousSize = shuttlePeriodRepository.findAll().size
        shuttleTimetableRepository.deleteAll(shuttleTimetableRepository.findAllByRouteNameAndPeriodType("test", "test"))
        shuttleService.deleteShuttlePeriod(ShuttlePeriodPK("test", testStartDateTime, testEndDateTime))
        assertTrue(shuttlePeriodRepository.findAll().size == previousSize - 1)

        // Test not found
        assertTrue(
            assertThrows<NullPointerException> {
                shuttleService.deleteShuttlePeriod(ShuttlePeriodPK("test", testStartDateTime, testEndDateTime))
            }.message == "SPECIFIED_PERIOD_ITEM_NOT_FOUND"
        )
    }

    @Test
    fun testGetShuttleRoute() {
        assertTrue(shuttleService.getShuttleRoute().isNotEmpty())
    }

    @Test
    fun testPostShuttleRoute() {
        val previousSize = shuttleRouteRepository.findAll().size
        // Test duplicated
        assertTrue(
            assertThrows<Exception> {
                shuttleService.postShuttleRoute(Route("test", "test", "test", "test", "startStop", "endStop"))
            }.message == "DUPLICATED"
        )
        // Test success
        shuttleService.postShuttleRoute(Route("test1", "test", "test", "test", "startStop", "endStop"))
        assertTrue(shuttleRouteRepository.findAll().size == previousSize + 1)
        shuttleRouteRepository.deleteById("test1")
    }

    @Test
    fun testDeleteShuttleRoute() {
        // Should delete all related data
        val previousSize = shuttleRouteRepository.findAll().size
        assertThrows<DataIntegrityViolationException> {
            shuttleService.deleteShuttleRoute("test")
        }
        // Test success
        shuttleTimetableRepository.deleteAll(shuttleTimetableRepository.findAllByRouteNameAndPeriodType("test", "test"))
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "startStop"))
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "endStop"))
        shuttleService.deleteShuttleRoute("test")
        assertTrue(shuttleRouteRepository.findAll().size == previousSize - 1)
        // Test not found
        assertTrue(
            assertThrows<NullPointerException> {
                shuttleService.deleteShuttleRoute("test")
            }.message == "SPECIFIED_ROUTE_NOT_FOUND"
        )
    }

    @Test
    fun testPatchShuttleRoute() {
        // 1. Change route description in Korean
        shuttleService.patchShuttleRoute("test", PatchRouteRequest(
            routeDescriptionKorean = "test1"
        ))
        assertTrue(shuttleRouteRepository.findById("test").get().routeDescriptionKorean == "test1")
        // 2. Change route description in English
        shuttleService.patchShuttleRoute("test", PatchRouteRequest(
            routeDescriptionEnglish = "test2"
        ))
        assertTrue(shuttleRouteRepository.findById("test").get().routeDescriptionEnglish == "test2")
        // 3. Change route type
        shuttleService.patchShuttleRoute("test", PatchRouteRequest(
            routeType = "test3"
        ))
        assertTrue(shuttleRouteRepository.findById("test").get().routeType == "test3")
        // 4. Change start stop
        shuttleService.patchShuttleRoute("test", PatchRouteRequest(
            startStop = "endStop"
        ))
        assertTrue(shuttleRouteRepository.findById("test").get().startStop == "endStop")
        // 5. Change end stop
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
    fun testPostShuttleStop() {
        val previousSize = shuttleStopRepository.findAll().size
        // Test duplicated
        assertTrue(
            assertThrows<Exception> {
                shuttleService.postShuttleStop(Stop("startStop", 0.0, 0.0))
            }.message == "DUPLICATED"
        )
        println(shuttleStopRepository.findById("test"))
        // Test success
        shuttleService.postShuttleStop(Stop("test", 0.0, 0.0))
        assertTrue(shuttleStopRepository.findAll().size == previousSize + 1)
        shuttleStopRepository.deleteById("test")
    }

    @Test
    fun testDeleteShuttleStop() {
        // Should delete all related data
        val previousSize = shuttleStopRepository.findAll().size
        assertThrows<DataIntegrityViolationException> {
            shuttleService.deleteShuttleStop("startStop")
        }
        // Test success
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
    fun testGetShuttleRouteStop() {
        assertTrue(shuttleService.getShuttleRouteStop("test").isNotEmpty())
    }

    @Test
    fun testPostShuttleRouteStop() {
        val previousSize = shuttleRouteStopRepository.findAll().size
        // Test duplicated
        assertTrue(
            assertThrows<Exception> {
                shuttleService.postShuttleRouteStop(RouteStop("test", "startStop", 0, Duration.ofMinutes(0)))
            }.message == "DUPLICATED"
        )
        // Test success
        shuttleStopRepository.save(Stop("test", 0.0, 0.0))
        shuttleService.postShuttleRouteStop(RouteStop("test", "test", 2, Duration.ofMinutes(20)))
        assertTrue(shuttleRouteStopRepository.findAll().size == previousSize + 1)
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "test"))
    }

    @Test
    fun testDeleteShuttleRouteStop() {
        val previousSize = shuttleRouteStopRepository.findAll().size
        shuttleService.deleteShuttleRouteStop("test", "startStop")
        assertTrue(shuttleRouteStopRepository.findAll().size == previousSize - 1)
        // Test not found
        assertTrue(
            assertThrows<NullPointerException> {
                shuttleService.deleteShuttleRouteStop("test", "startStop")
            }.message == "SPECIFIED_ROUTE_STOP_NOT_FOUND"
        )
    }

    @Test
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
    fun testGetShuttleTimetable() {
        assertTrue(shuttleService.getShuttleTimetable().isNotEmpty())
    }

    @Test
    fun testPostShuttleTimetable() {
        val previousSize = shuttleTimetableRepository.findAll().size
        println(shuttleTimetableRepository.findAllByRouteNameAndPeriodType("test", "test"))
        // Test duplicated
        assertTrue(
            assertThrows<Exception> {
                shuttleService.postShuttleTimetable(Timetable(9999, "test", true, "test", LocalTime.now()))
            }.message == "DUPLICATED"
        )
        // Test success
        shuttleService.postShuttleTimetable(Timetable(10000, "test", true, "test", LocalTime.now().plusHours(1)))
        assertTrue(shuttleTimetableRepository.findAll().size == previousSize + 1)
        shuttleTimetableRepository.deleteAll(shuttleTimetableRepository.findAllByRouteNameAndPeriodType("test", "test"))
    }

    @Test
    fun testDeleteShuttleTimetable() {
        // Test success
        val previousSize = shuttleTimetableRepository.findAll().size
        shuttleService.deleteShuttleTimetable(9999)
        assertTrue(shuttleTimetableRepository.findAll().size == previousSize - 1)
        // Test not found
        assertTrue(
            assertThrows<NullPointerException> {
                shuttleService.deleteShuttleTimetable(9999)
            }.message == "SPECIFIED_SEQ_NOT_FOUND"
        )
    }

    @Test
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
    fun testGetShuttleTimetableView() {
        assertTrue(shuttleService.getShuttleTimetableView(
            "test",
            true,
            "test",
            3,
        ).size > 0)
    }
}