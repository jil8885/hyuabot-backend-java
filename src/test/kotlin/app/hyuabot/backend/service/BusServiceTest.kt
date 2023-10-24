package app.hyuabot.backend.service

import app.hyuabot.backend.domain.bus.*
import app.hyuabot.backend.dto.database.BusRealtimePK
import app.hyuabot.backend.dto.database.BusRouteStopPK
import app.hyuabot.backend.dto.database.BusTimetablePK
import app.hyuabot.backend.repository.bus.*
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
class BusServiceTest {
    @Autowired private lateinit var busService: BusService
    @Autowired private lateinit var busRealtimeRepository: BusRealtimeRepository
    @Autowired private lateinit var busRouteRepository: BusRouteRepository
    @Autowired private lateinit var busRouteStopRepository: BusRouteStopRepository
    @Autowired private lateinit var busStopRepository: BusStopRepository
    @Autowired private lateinit var busTimetableRepository: BusTimetableRepository

    @BeforeEach
    fun init() {
        clean()
        busStopRepository.saveAll(listOf(
            BusStop(0, "test", 0.0, 0.0, 0, "00000", "test"),
            BusStop(1, "startStop", 0.0, 0.0, 0, "00001", "test"),
            BusStop(2, "endStop", 0.0, 0.0, 0, "00002", "test"),
        ))
        busRouteRepository.save(BusRoute(
            0,"test","test","13", 0,2,
                LocalTime.parse("00:00"), LocalTime.parse("00:00"),
                LocalTime.parse("00:00"), LocalTime.parse("00:00"),
                0, 0, "test", "test",
            )
        )
        busRouteStopRepository.saveAll(listOf(
            BusRouteStop(0, 0, 0, 0),
            BusRouteStop(0, 1, 1, 0),
            BusRouteStop(0, 2, 2, 0),
        ))
        busTimetableRepository.saveAll(listOf(
            BusTimetable(0, 0, "weekdays", LocalTime.parse("00:00")),
            BusTimetable(0, 0, "weekdays", LocalTime.parse("00:05")),
            BusTimetable(0, 0, "weekdays", LocalTime.parse("00:10")),
        ))

        val now = LocalDateTime.now()
        busRealtimeRepository.saveAll(listOf(
            BusRealtime(0, 0, 0, 0, 20, Duration.ofMinutes(3), true, now),
            BusRealtime(0, 0, 1, 1, 20, Duration.ofMinutes(5), true, now),
            BusRealtime(0, 0, 2, 2, 20, Duration.ofMinutes(7), true, now),
        ))
    }

    @AfterEach
    fun clean() {
        busRealtimeRepository.deleteAllInBatch(busRealtimeRepository.findAllByRouteID(0))
        busTimetableRepository.deleteAllInBatch(busTimetableRepository.findAllByRouteID(0))
        busRouteStopRepository.deleteAllInBatch(busRouteStopRepository.findAllByRouteID(0))
        busRouteRepository.deleteAllByIdInBatch(listOf(0))
        busStopRepository.deleteAllByIdInBatch(listOf(0, 1, 2))
    }

    @Test
    @DisplayName("GET_BUS_ROUTE_LIST")
    fun getBusRouteList() {
        assertTrue(busService.getBusRouteList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_BUS_ROUTE_LIST_FILTER_BY_NAME")
    fun getBusRouteListFilterByName() {
        assertTrue(busService.getBusRouteListByName("test").isNotEmpty())
        busService.getBusRouteListByName("test").forEach {
            assertTrue(it.name.contains("test"))
        }
    }

    @Test
    @DisplayName("GET_BUS_ROUTE_LIST_FILTER_BY_COMPANY_NAME")
    fun getBusRouteListFilterByCompanyName() {
        assertTrue(busService.getBusRouteListByCompany("test").isNotEmpty())
        busService.getBusRouteListByCompany("test").forEach {
            assertTrue(it.companyName.contains("test"))
        }
    }

    @Test
    @DisplayName("GET_BUS_ROUTE_LIST_FILTER_BY_TYPE_NAME")
    fun getBusRouteListFilterByTypeName() {
        assertTrue(busService.getBusRouteListByType("13").isNotEmpty())
        busService.getBusRouteListByType("13").forEach {
            assertTrue(it.typeName == "13")
        }
    }

    @Test
    @DisplayName("POST_BUS_ROUTE_SUCCESS")
    fun postBusRouteSuccess() {
        busService.postBusRoute(
            1,
            "test",
            "test",
            "13",
            0,
            1,
            LocalTime.parse("00:00"),
            LocalTime.parse("00:00"),
            LocalTime.parse("00:00"),
            LocalTime.parse("00:00"),
            0,
            0,
            "test",
            "test",
        )
        assertTrue(busRouteRepository.existsById(1))
        busRouteRepository.deleteById(1)
    }

    @Test
    @DisplayName("POST_BUS_ROUTE_FAIL_DUPLICATE_ID")
    fun postBusRouteFailDuplicateID() {
        assertTrue(assertThrows<Exception> {
            busService.postBusRoute(
            0,
            "test",
            "test",
            "13",
            0,
            1,
            LocalTime.parse("00:00"),
            LocalTime.parse("00:00"),
            LocalTime.parse("00:00"),
            LocalTime.parse("00:00"),
            0,
            0,
            "test",
            "test",
        )}.message == "DUPLICATED")
    }

    @Test
    @DisplayName("PATCH_BUS_ROUTE_SUCCESS")
    fun patchBusRouteSuccess() {
        busService.patchBusRoute(
            0,
            "test2",
            "test2",
            "14",
            0,
            1,
            LocalTime.parse("01:00"),
            LocalTime.parse("02:00"),
            LocalTime.parse("01:00"),
            LocalTime.parse("02:00"),
            1,
            1,
            "test1",
            "test1",
        )
        assertTrue(busRouteRepository.existsById(0))
        busRouteRepository.findById(0).orElseThrow().let {
            assertTrue(it.name == "test2")
            assertTrue(it.companyName == "test1")
            assertTrue(it.typeName == "test2")
            assertTrue(it.typeCode == "14")
            assertTrue(it.startStopID == 0)
            assertTrue(it.endStopID == 1)
            assertTrue(it.upFirstTime == LocalTime.parse("01:00"))
            assertTrue(it.upLastTime == LocalTime.parse("02:00"))
            assertTrue(it.downFirstTime == LocalTime.parse("01:00"))
            assertTrue(it.downLastTime == LocalTime.parse("02:00"))
            assertTrue(it.districtCode == 1)
            assertTrue(it.companyID == 1)
            assertTrue(it.companyName == "test1")
            assertTrue(it.companyTelephone == "test1")
        }
    }

    @Test
    @DisplayName("PATCH_BUS_ROUTE_FAIL_NOT_FOUND")
    fun patchBusRouteFailNotFound() {
        assertTrue(assertThrows<Exception> {
            busService.patchBusRoute(1)}.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_BUS_ROUTE_SUCCESS")
    fun deleteBusRouteSuccess() {
        busRealtimeRepository.deleteAllInBatch(busRealtimeRepository.findAllByRouteID(0))
        busTimetableRepository.deleteAllInBatch(busTimetableRepository.findAllByRouteID(0))
        busRouteStopRepository.deleteAllInBatch(busRouteStopRepository.findAllByRouteID(0))
        busService.deleteBusRoute(0)
        assertTrue(busRouteRepository.findById(0).isEmpty)
    }

    @Test
    @DisplayName("DELETE_BUS_ROUTE_FAIL_NOT_FOUND")
    fun deleteBusRouteFailNotFound() {
        assertTrue(assertThrows<Exception> {
            busService.deleteBusRoute(1)}.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_BUS_ROUTE_FAIL_HAS_TIMETABLE")
    fun deleteBusRouteFailHasTimetable() {
        assertTrue(assertThrows<Exception> {
            busService.deleteBusRoute(0)}.message == "BUS_TIMETABLE_EXISTS")
    }

    @Test
    @DisplayName("GET_BUS_STOP_LIST")
    fun getBusStopList() {
        assertTrue(busService.getBusStopList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_BUS_STOP_LIST_FILTER_BY_NAME")
    fun getBusStopListByName() {
        assertTrue(busService.getBusStopListByName("test").isNotEmpty())
        busService.getBusStopListByName("test").forEach {
            assertTrue(it.name.contains("test"))
        }
    }

    @Test
    @DisplayName("POST_BUS_STOP_SUCCESS")
    fun postBusStop() {
        busService.postBusStop(3, "test", 0.0, 0.0, 0, "test", "00003")
        assertTrue(busStopRepository.existsById(3))
        busStopRepository.deleteById(3)
    }

    @Test
    @DisplayName("POST_BUS_STOP_FAIL_DUPLICATE_ID")
    fun postBusStopFailDuplicateID() {
        assertTrue(assertThrows<Exception> {
            busService.postBusStop(0, "test", 0.0, 0.0, 0, "test", "00000")
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("PATCH_BUS_STOP_SUCCESS")
    fun patchBusStopSuccess() {
        busService.patchBusStop(0, "test2", 1.0, 1.0, 1, "test1", "00001")
        assertTrue(busStopRepository.existsById(0))
        busStopRepository.findById(0).orElseThrow().let {
            assertTrue(it.name == "test2")
            assertTrue(it.latitude == 1.0)
            assertTrue(it.longitude == 1.0)
            assertTrue(it.districtCode == 1)
            assertTrue(it.mobileNumber == "00001")
            assertTrue(it.regionName == "test1")
        }
    }

    @Test
    @DisplayName("PATCH_BUS_STOP_FAIL_NOT_FOUND")
    fun patchBusStopFailNotFound() {
        assertTrue(assertThrows<Exception> {
            busService.patchBusStop(3, "test2", 1.0, 1.0, 1, "test1", "00001")
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_BUS_STOP_SUCCESS")
    fun deleteBusStopSuccess() {
        busTimetableRepository.deleteAllInBatch(busTimetableRepository.findAllByRouteID(0))
        busRealtimeRepository.deleteAllInBatch(busRealtimeRepository.findAllByRouteID(0))
        busRouteStopRepository.deleteAllInBatch(busRouteStopRepository.findAllByRouteID(0))
        busRouteRepository.deleteAllByIdInBatch(listOf(0))
        busService.deleteBusStop(0)
        assertTrue(busStopRepository.findById(0).isEmpty)
    }

    @Test
    @DisplayName("DELETE_BUS_STOP_FAIL_NOT_FOUND")
    fun deleteBusStopFailNotFound() {
        assertTrue(assertThrows<Exception> {
            busService.deleteBusStop(3)
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_BUS_STOP_FAIL_HAS_ROUTE_STOP")
    fun deleteBusStopFailHasRouteStop() {
        assertTrue(assertThrows<Exception> {
            busService.deleteBusStop(1)
        }.message == "BUS_ROUTE_STOP_EXISTS")
    }

    @Test
    @DisplayName("GET_BUS_ROUTE_STOP_LIST")
    fun getBusRouteStopList() {
        assertTrue(busService.getBusRouteStopList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_BUS_ROUTE_STOP_LIST_FILTER_BY_ROUTE_ID")
    fun getBusRouteStopListFilterByRouteID() {
        assertTrue(busService.getBusRouteStopListByRouteID(0).isNotEmpty())
        busService.getBusRouteStopListByRouteID(0).forEach {
            assertTrue(it.routeID == 0)
        }
    }

    @Test
    @DisplayName("GET_BUS_ROUTE_STOP_LIST_FILTER_BY_STOP_ID")
    fun getBusRouteStopListFilterByStopID() {
        assertTrue(busService.getBusRouteStopListByStopID(0).isNotEmpty())
        busService.getBusRouteStopListByStopID(0).forEach {
            assertTrue(it.stopID == 0)
        }
    }

    @Test
    @DisplayName("GET_BUS_ROUTE_STOP_LIST_FILTER_BY_ROUTE_ID_AND_STOP_ID")
    fun getBusRouteStopListFilterByRouteIDAndStopID() {
        assertTrue(busService.getBusRouteStopListByRouteIDAndStopID(0, 0).isNotEmpty())
        busService.getBusRouteStopListByRouteIDAndStopID(0, 0).forEach {
            assertTrue(it.routeID == 0)
            assertTrue(it.stopID == 0)
        }
    }

    @Test
    @DisplayName("POST_BUS_ROUTE_STOP_SUCCESS")
    fun postBusRouteStopSuccess() {
        busStopRepository.save(BusStop(3, "test", 0.0, 0.0, 0, "00003", "test"))
        busService.postBusRouteStop(0, 3, 3, 3)
        assertTrue(busRouteStopRepository.existsById(BusRouteStopPK(0, 3)))
        busRouteStopRepository.deleteById(BusRouteStopPK(0, 3))
        busStopRepository.deleteById(3)
    }

    @Test
    @DisplayName("POST_BUS_ROUTE_STOP_FAIL_DUPLICATE_ID")
    fun postBusRouteStopFailDuplicateID() {
        assertTrue(assertThrows<Exception> {
            busService.postBusRouteStop(0, 0, 0, 0)
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("PATCH_BUS_ROUTE_STOP_SUCCESS")
    fun patchBusRouteStopSuccess() {
        busService.patchBusRouteStop(0, 0, 0, 0)
        assertTrue(busRouteStopRepository.existsById(BusRouteStopPK(0, 0)))
        busRouteStopRepository.findById(BusRouteStopPK(0, 0)).orElseThrow().let {
            assertTrue(it.seq == 0)
            assertTrue(it.startStopID == 0)
        }
    }

    @Test
    @DisplayName("PATCH_BUS_ROUTE_STOP_FAIL_NOT_FOUND")
    fun patchBusRouteStopFailNotFound() {
        assertTrue(assertThrows<Exception> {
            busService.patchBusRouteStop(0, 3, 3, 3)
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_BUS_ROUTE_STOP_SUCCESS")
    fun deleteBusRouteStopSuccess() {
        busTimetableRepository.deleteAllInBatch(busTimetableRepository.findAllByRouteID(0))
        busRealtimeRepository.deleteAllInBatch(busRealtimeRepository.findAllByRouteID(0))
        busService.deleteBusRouteStop(0, 0)
        assertTrue(busRouteStopRepository.findById(BusRouteStopPK(0, 0)).isEmpty)
    }

    @Test
    @DisplayName("DELETE_BUS_ROUTE_STOP_FAIL_NOT_FOUND")
    fun deleteBusRouteStopFailNotFound() {
        assertTrue(assertThrows<Exception> {
            busService.deleteBusRouteStop(0, 3)
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_BUS_ROUTE_STOP_FAIL_HAS_TIMETABLE")
    fun deleteBusRouteStopFailHasTimetable() {
        assertTrue(assertThrows<Exception> {
            busService.deleteBusRouteStop(0, 0)
        }.message == "BUS_TIMETABLE_EXISTS")
    }

    @Test
    @DisplayName("DELETE_BUS_ROUTE_STOP_FAIL_HAS_REALTIME")
    fun deleteBusRouteStopFailHasRealtime() {
        busTimetableRepository.deleteAllInBatch(busTimetableRepository.findAllByRouteID(0))
        assertTrue(assertThrows<Exception> {
            busService.deleteBusRouteStop(0, 0)
        }.message == "BUS_REALTIME_EXISTS")
    }

    @Test
    @DisplayName("GET_BUS_TIMETABLE_LIST")
    fun getBusTimetableList() {
        assertTrue(busService.getBusTimetableList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_BUS_TIMETABLE_LIST_FILTER_BY_ROUTE_ID")
    fun getBusTimetableListFilterByRouteID() {
        assertTrue(busService.getBusTimetableListByRouteID(0).isNotEmpty())
        busService.getBusTimetableListByRouteID(0).forEach {
            assertTrue(it.routeID == 0)
        }
    }

    @Test
    @DisplayName("GET_BUS_TIMETABLE_LIST_FILTER_BY_ROUTE_ID_AND_START_STOP_ID")
    fun getBusTimetableListFilterByRouteIDAndStartStopID() {
        assertTrue(busService.getBusTimetableListByRouteIDAndStartStopID(0, 0).isNotEmpty())
        busService.getBusTimetableListByRouteIDAndStartStopID(0, 0).forEach {
            assertTrue(it.routeID == 0)
            assertTrue(it.startStopID == 0)
        }
    }

    @Test
    @DisplayName("GET_BUS_TIMETABLE_LIST_FILTER_BY_CONDITIONS")
    fun getBusTimetableListFilterByConditions() {
        assertTrue(busService.getBusTimetableListByConditions(0, 0, "weekdays").isNotEmpty())
        busService.getBusTimetableListByConditions(0, 0, "weekdays").forEach {
            assertTrue(it.routeID == 0)
            assertTrue(it.startStopID == 0)
            assertTrue(it.weekdays == "weekdays")
        }
    }

    @Test
    @DisplayName("POST_BUS_TIMETABLE_SUCCESS")
    fun postBusTimetableSuccess() {
        busService.postBusTimetable(0, 0, "weekdays", LocalTime.parse("00:15"))
        assertTrue(busTimetableRepository.existsById(BusTimetablePK(0, 0, LocalTime.parse("00:15"), "weekdays")))
        busTimetableRepository.deleteById(BusTimetablePK(0, 0, LocalTime.parse("00:15"), "weekdays"))
    }

    @Test
    @DisplayName("POST_BUS_TIMETABLE_FAIL_DUPLICATE_ID")
    fun postBusTimetableFailDuplicateID() {
        assertTrue(assertThrows<Exception> {
            busService.postBusTimetable(0, 0, "weekdays", LocalTime.parse("00:00"))
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("DELETE_BUS_TIMETABLE_SUCCESS")
    fun deleteBusTimetableSuccess() {
        busService.deleteBusTimetable(0, 0, "weekdays", LocalTime.parse("00:00"))
        assertTrue(busTimetableRepository.findById(BusTimetablePK(0, 0, LocalTime.parse("00:00"), "weekdays")).isEmpty)
    }

    @Test
    @DisplayName("DELETE_BUS_TIMETABLE_FAIL_NOT_FOUND")
    fun deleteBusTimetableFailNotFound() {
        assertTrue(assertThrows<Exception> {
            busService.deleteBusTimetable(0, 0, "weekdays", LocalTime.parse("00:15"))
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("GET_BUS_REALTIME_LIST")
    fun getBusRealtimeList() {
        assertTrue(busService.getBusRealtimeList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_BUS_REALTIME_LIST_FILTER_BY_ROUTE_ID")
    fun getBusRealtimeListFilterByRouteID() {
        assertTrue(busService.getBusRealtimeListByRouteID(0).isNotEmpty())
        busService.getBusRealtimeListByRouteID(0).forEach {
            assertTrue(it.routeID == 0)
        }
    }

    @Test
    @DisplayName("GET_BUS_REALTIME_LIST_FILTER_BY_STOP_ID")
    fun getBusRealtimeListFilterByStopID() {
        assertTrue(busService.getBusRealtimeListByStopID(0).isNotEmpty())
        busService.getBusRealtimeListByStopID(0).forEach {
            assertTrue(it.stopID == 0)
        }
    }

    @Test
    @DisplayName("GET_BUS_REALTIME_LIST_FILTER_BY_ROUTE_ID_AND_STOP_ID")
    fun getBusRealtimeListFilterByRouteIDAndStopID() {
        assertTrue(busService.getBusRealtimeListByRouteIDAndStopID(0, 0).isNotEmpty())
        busService.getBusRealtimeListByRouteIDAndStopID(0, 0).forEach {
            assertTrue(it.routeID == 0)
            assertTrue(it.stopID == 0)
        }
    }

    @Test
    @DisplayName("POST_BUS_REALTIME_SUCCESS")
    fun postBusRealtimeSuccess() {
        busService.postBusRealtime(0, 0, 0, 0, 0, Duration.ofMinutes(3), true)
        assertTrue(busRealtimeRepository.existsById(BusRealtimePK(0, 0, 0)))
        busRealtimeRepository.deleteById(BusRealtimePK(0, 0, 0))
    }

    @Test
    @DisplayName("DELETE_BUS_REALTIME_SUCCESS")
    fun deleteBusRealtimeSuccess() {
        busService.deleteBusRealtime(0, 0, 0)
        assertTrue(busRealtimeRepository.findById(BusRealtimePK(0, 0, 0)).isEmpty)
    }

    @Test
    @DisplayName("DELETE_BUS_REALTIME_FAIL_NOT_FOUND")
    fun deleteBusRealtimeFailNotFound() {
        assertTrue(assertThrows<Exception> {
            busService.deleteBusRealtime(0, 0, 3)
        }.message == "NOT_FOUND")
    }
}