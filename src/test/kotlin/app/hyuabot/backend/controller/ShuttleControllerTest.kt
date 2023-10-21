package app.hyuabot.backend.controller

import app.hyuabot.backend.domain.shuttle.*
import app.hyuabot.backend.dto.database.ShuttlePeriodPK
import app.hyuabot.backend.dto.database.ShuttleRouteStopPK
import app.hyuabot.backend.dto.request.LoginRequest
import app.hyuabot.backend.dto.request.SignUpRequest
import app.hyuabot.backend.dto.request.shuttle.*
import app.hyuabot.backend.dto.response.Response
import app.hyuabot.backend.dto.response.ShuttleHolidayItem
import app.hyuabot.backend.dto.response.ShuttlePeriodItem
import app.hyuabot.backend.repository.UserRepository
import app.hyuabot.backend.repository.shuttle.*
import app.hyuabot.backend.service.UserService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ShuttleControllerTest {
    @Autowired private lateinit var mockmvc: MockMvc
    @Autowired private lateinit var userService: UserService
    @Autowired private lateinit var userRepository: UserRepository
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
        // Create test user
        userService.signUp(
            SignUpRequest(
            "test",
            BCryptPasswordEncoder().encode("test"),
            "test",
            "test@email.com",
            "010-0000-0000",
        ))
        userRepository.findByUserID("test")?.apply {
            active = true
            userRepository.save(this)
        }
        shuttleHolidayRepository.save(Holiday(testDate, "weekends", "test"))
        shuttlePeriodTypeRepository.save(PeriodType("test"))
        shuttlePeriodRepository.save(Period("test", testStartDateTime, testEndDateTime))
        shuttleStopRepository.save(Stop("startStop", 0.0, 0.0))
        shuttleStopRepository.save(Stop("endStop", 0.0, 0.0))
        shuttleStopRepository.save(Stop("testStop", 0.0, 0.0))
        shuttleRouteRepository.save(Route("test", "test", "test", "test", "startStop", "endStop"))
        shuttleRouteStopRepository.save(RouteStop("test", "startStop", 0, Duration.ofMinutes(0)))
        shuttleRouteStopRepository.save(RouteStop("test", "endStop", 1, Duration.ofMinutes(10)))
        shuttleTimetableRepository.save(Timetable(9999, "test", true, "test", LocalTime.now()))
    }

    @AfterEach
    fun clean() {
        // Delete test user
        userRepository.deleteById("test")
        shuttleTimetableRepository.deleteAll(shuttleTimetableRepository.findAllByRouteNameAndPeriodType("test", "test"))
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "startStop"))
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "endStop"))
        shuttleRouteRepository.deleteById("test")
        shuttleStopRepository.deleteById("startStop")
        shuttleStopRepository.deleteById("endStop")
        shuttleStopRepository.deleteById("testStop")
        shuttleHolidayRepository.deleteById(testDate)
        shuttlePeriodRepository.deleteById(ShuttlePeriodPK("test", testStartDateTime, testEndDateTime))
        shuttlePeriodTypeRepository.deleteById("test")
    }

    fun getAccessToken(): String {
        val request = LoginRequest("test", "test")
        val response = mockmvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(request))
        )
            .andDo(print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.header().exists("Authorization"))
            .andExpect(MockMvcResultMatchers.header().exists("Set-Cookie"))
            .andExpect(MockMvcResultMatchers.cookie().exists("refresh-token"))
            .andReturn()
        return response.response.getHeader("Authorization")!!
    }

    @Test
    @DisplayName("GET_SHUTTLE_HOLIDAY_UNAUTHORIZED")
    fun testGetShuttleHolidayUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/holiday")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("GET_SHUTTLE_HOLIDAY_SUCCESS")
    fun testGetShuttleHolidaySuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/holiday")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].date").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].type").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].calendar").isNotEmpty)
    }

    @Test
    @DisplayName("POST_SHUTTLE_HOLIDAY_UNAUTHORIZED")
    fun testPostShuttleHolidayUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/holiday")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    ShuttleHolidayItem("2999-01-01", "weekends", "test")
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("POST_SHUTTLE_HOLIDAY_DUPLICATED")
    fun testPostShuttleHolidayDuplicated() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/holiday")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    ShuttleHolidayItem("2999-01-01", "weekends", "test")
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isConflict)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("DUPLICATED"))
    }

    @Test
    @DisplayName("POST_SHUTTLE_HOLIDAY_SUCCESS")
    fun testPostShuttleHolidaySuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/holiday")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    ShuttleHolidayItem("2999-01-02", "weekends", "test")
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated)

        shuttleHolidayRepository.deleteById(LocalDate.of(2999, 1, 2))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_HOLIDAY_UNAUTHORIZED")
    fun testDeleteShuttleHolidayUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/holiday/2999-01-01")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_HOLIDAY_NOT_FOUND")
    fun testDeleteShuttleHolidayNotFound() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/holiday/2999-01-02")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SPECIFIED_DATE_NOT_FOUND"))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_HOLIDAY_SUCCESS")
    fun testDeleteShuttleHolidaySuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/holiday/2999-01-01")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertTrue(shuttleHolidayRepository.findById(LocalDate.of(2999, 1, 1)).isEmpty)
    }

    @Test
    @DisplayName("GET_SHUTTLE_PERIOD_UNAUTHORIZED")
    fun testGetShuttlePeriodUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/period")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("GET_SHUTTLE_PERIOD_SUCCESS")
    fun testGetShuttlePeriodSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/period")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].period").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].start").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].end").isNotEmpty)
    }

    @Test
    @DisplayName("POST_SHUTTLE_PERIOD_UNAUTHORIZED")
    fun testPostShuttlePeriodUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/period")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    ShuttlePeriodItem("test", testStartDateTime.toString(), testEndDateTime.toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("POST_SHUTTLE_PERIOD_DUPLICATED")
    fun testPostShuttlePeriodDuplicated() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/period")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    ShuttlePeriodItem("test", testStartDateTime.toString(), testEndDateTime.toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isConflict)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("DUPLICATED"))
    }

    @Test
    @DisplayName("POST_SHUTTLE_PERIOD_SUCCESS")
    fun testPostShuttlePeriodSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/period")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    ShuttlePeriodItem("test", testStartDateTime.plusMonths(1).toString(), testEndDateTime.plusMonths(1).toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated)

        shuttlePeriodRepository.deleteById(ShuttlePeriodPK("test", testStartDateTime.plusMonths(1), testEndDateTime.plusMonths(1)))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_PERIOD_UNAUTHORIZED")
    fun testDeleteShuttlePeriodUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/period/test/${testStartDateTime}/${testEndDateTime}")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_PERIOD_NOT_FOUND")
    fun testDeleteShuttlePeriodNotFound() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/period/test2/${testStartDateTime.plusMonths(1)}/${testEndDateTime.plusMonths(1)}")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SPECIFIED_PERIOD_ITEM_NOT_FOUND"))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_PERIOD_SUCCESS")
    fun testDeleteShuttlePeriodSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/period/test/${testStartDateTime}/${testEndDateTime}")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertTrue(shuttlePeriodRepository.findById(ShuttlePeriodPK("test", testStartDateTime, testEndDateTime)).isEmpty)
    }

    @Test
    @DisplayName("GET_SHUTTLE_ROUTE_UNAUTHORIZED")
    fun testGetShuttleRouteUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/route")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("GET_SHUTTLE_ROUTE_SUCCESS")
    fun testGetShuttleRouteSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/route")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].routeName").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].routeType").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].startStop").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].endStop").isNotEmpty)
    }

    @Test
    @DisplayName("POST_SHUTTLE_ROUTE_UNAUTHORIZED")
    fun testPostShuttleRouteUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/route")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    Route("test", "test", "test", "test", "startStop", "endStop")
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("POST_SHUTTLE_ROUTE_DUPLICATED")
    fun testPostShuttleRouteDuplicated() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/route")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    Route("test", "test", "test", "test", "startStop", "endStop")
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isConflict)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("DUPLICATED"))
    }

    @Test
    @DisplayName("POST_SHUTTLE_ROUTE_SUCCESS")
    fun testPostShuttleRouteSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/route")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    Route("test2", "test", "test", "test", "startStop", "endStop")
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated)

        shuttleRouteRepository.deleteById("test2")
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_ROUTE_UNAUTHORIZED")
    fun testDeleteShuttleRouteUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/route/test")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_ROUTE_NOT_FOUND")
    fun testDeleteShuttleRouteNotFound() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/route/test2")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SPECIFIED_ROUTE_NOT_FOUND"))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_ROUTE_SUCCESS")
    fun testDeleteShuttleRouteSuccess() {
        shuttleTimetableRepository.deleteAll(shuttleTimetableRepository.findAllByRouteNameAndPeriodType("test", "test"))
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "startStop"))
        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "endStop"))
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/route/test")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertTrue(shuttleRouteRepository.findById("test").isEmpty)
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_UNAUTHORIZED")
    fun testPatchShuttleRouteUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/route/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PatchRouteRequest("test", "korean", "english", "endStop", "startStop")
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_NOT_FOUND")
    fun testPatchShuttleRouteNotFound() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/route/test2")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PatchRouteRequest("test", "korean", "english", "endStop", "startStop")
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SPECIFIED_ROUTE_NOT_FOUND"))
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_SUCCESS")
    fun testPatchShuttleRouteSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/route/test")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PatchRouteRequest("test", "korean", "english", "endStop", "startStop")
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isAccepted)

        val changedItem = shuttleRouteRepository.findById("test").get()
        assertTrue(changedItem.routeDescriptionKorean == "korean")
        assertTrue(changedItem.routeDescriptionEnglish == "english")
        assertTrue(changedItem.startStop == "endStop")
        assertTrue(changedItem.endStop == "startStop")
    }

    @Test
    @DisplayName("GET_SHUTTLE_STOP_UNAUTHORIZED")
    fun testGetShuttleStopUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/stop")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("GET_SHUTTLE_STOP_SUCCESS")
    fun testGetShuttleStopSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/stop")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].stopName").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].latitude").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].longitude").isNotEmpty)
    }

    @Test
    @DisplayName("POST_SHUTTLE_STOP_UNAUTHORIZED")
    fun testPostShuttleStopUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    Stop("test", 0.0, 0.0)
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("POST_SHUTTLE_STOP_DUPLICATED")
    fun testPostShuttleStopDuplicated() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/stop")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    Stop("startStop", 0.0, 0.0)
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isConflict)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("DUPLICATED"))
    }

    @Test
    @DisplayName("POST_SHUTTLE_STOP_SUCCESS")
    fun testPostShuttleStopSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/stop")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    Stop("test2", 0.0, 0.0)
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))

        shuttleStopRepository.deleteById("test2")
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_STOP_UNAUTHORIZED")
    fun testDeleteShuttleStopUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/stop/test")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_STOP_NOT_FOUND")
    fun testDeleteShuttleStopNotFound() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/stop/test2")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SPECIFIED_STOP_NOT_FOUND"))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_STOP_SUCCESS")
    fun testDeleteShuttleStopSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/stop/test")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertTrue(shuttleStopRepository.findById("test").isEmpty)
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_STOP_UNAUTHORIZED")
    fun testPatchShuttleStopUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/stop/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    Stop("test", 0.0, 0.0)
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_STOP_NOT_FOUND")
    fun testPatchShuttleStopNotFound() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/stop/test2")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    Stop("test", 0.0, 0.0)
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SPECIFIED_STOP_NOT_FOUND"))
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_STOP_SUCCESS")
    fun testPatchShuttleStopSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/stop/testStop")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    Stop("testStop", 0.5, 0.5)
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isAccepted)

        val changedItem = shuttleStopRepository.findById("testStop").get()
        assertTrue(changedItem.latitude == 0.5)
        assertTrue(changedItem.longitude == 0.5)
    }

    @Test
    @DisplayName("GET_SHUTTLE_ROUTE_STOP_UNAUTHORIZED")
    fun testGetShuttleRouteStopUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/route/test/stop")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("GET_SHUTTLE_ROUTE_STOP_SUCCESS")
    fun testGetShuttleRouteStopSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/route/test/stop")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].route").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].stop").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].order").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].cumulativeTime").isNotEmpty)
    }

    @Test
    @DisplayName("POST_SHUTTLE_ROUTE_STOP_UNAUTHORIZED")
    fun testPostShuttleRouteStopUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/route/test/stop")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PostRouteStop("test", "endStop", 2, Duration.ofMinutes(5).toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("POST_SHUTTLE_ROUTE_STOP_DUPLICATED")
    fun testPostShuttleRouteStopDuplicated() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/route/test/stop")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PostRouteStop("test", "endStop", 2, Duration.ofMinutes(5).toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isConflict)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("DUPLICATED"))
    }

    @Test
    @DisplayName("POST_SHUTTLE_ROUTE_STOP_SUCCESS")
    fun testPostShuttleRouteStopSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/route/test/stop")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PostRouteStop("test", "testStop", 2, Duration.ofMinutes(5).toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated)

        shuttleRouteStopRepository.deleteById(ShuttleRouteStopPK("test", "testStop"))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_ROUTE_STOP_UNAUTHORIZED")
    fun testDeleteShuttleRouteStopUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/route/test/stop/startStop")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_ROUTE_STOP_NOT_FOUND")
    fun testDeleteShuttleRouteStopNotFound() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/route/test/stop/test")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SPECIFIED_ROUTE_STOP_NOT_FOUND"))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_ROUTE_STOP_SUCCESS")
    fun testDeleteShuttleRouteStopSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/route/test/stop/startStop")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent)

        assertTrue(shuttleRouteStopRepository.findById(ShuttleRouteStopPK("test", "startStop")).isEmpty)
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_STOP_UNAUTHORIZED")
    fun testPatchShuttleRouteStopUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/route/test/stop/startStop")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PatchRouteStopRequest(2, Duration.ofMinutes(5).toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_STOP_NOT_FOUND")
    fun testPatchShuttleRouteStopNotFound() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/route/test/stop/test")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PatchRouteStopRequest(2, Duration.ofMinutes(5).toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_ROUTE_STOP_SUCCESS")
    fun testPatchShuttleRouteStopSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/route/test/stop/startStop")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PatchRouteStopRequest(2, Duration.ofMinutes(5).toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isAccepted)

        val changedItem = shuttleRouteStopRepository.findById(ShuttleRouteStopPK("test", "startStop")).get()
        assertTrue(changedItem.seq == 2)
        assertTrue(changedItem.cumulativeTime == Duration.ofMinutes(5))
    }

    @Test
    @DisplayName("GET_SHUTTLE_TIMETABLE_UNAUTHORIZED")
    fun testGetShuttleTimetableUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/timetable")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("GET_SHUTTLE_TIMETABLE_SUCCESS")
    fun testGetShuttleTimetableSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/timetable?period=test&route=test&stop=startStop")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].seq").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].periodType").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].weekdays").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].routeName").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].departureTime").isNotEmpty)
    }

    @Test
    @DisplayName("POST_SHUTTLE_TIMETABLE_UNAUTHORIZED")
    fun testPostShuttleTimetableUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/timetable")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PostTimetableRequest(9999, "test", true, "test", LocalTime.now().toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("POST_SHUTTLE_TIMETABLE_DUPLICATED")
    fun testPostShuttleTimetableDuplicated() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/timetable")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PostTimetableRequest(9999, "test", true, "test", LocalTime.now().toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isConflict)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("DUPLICATED"))
    }

    @Test
    @DisplayName("POST_SHUTTLE_TIMETABLE_SUCCESS")
    fun testPostShuttleTimetableSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.post("/api/shuttle/timetable")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(
                    PostTimetableRequest(9998, "test", true, "test", LocalTime.now().toString())
                ))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isCreated)

        shuttleTimetableRepository.deleteById(9998)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_TIMETABLE_UNAUTHORIZED")
    fun testDeleteShuttleTimetableUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/timetable/9999")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_TIMETABLE_NOT_FOUND")
    fun testDeleteShuttleTimetableNotFound() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/timetable/9998")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SPECIFIED_SEQ_NOT_FOUND"))
    }

    @Test
    @DisplayName("DELETE_SHUTTLE_TIMETABLE_SUCCESS")
    fun testDeleteShuttleTimetableSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.delete("/api/shuttle/timetable/9999")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_TIMETABLE_UNAUTHORIZED")
    fun testPatchShuttleTimetableUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/timetable/9999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(PatchTimetableRequest("test", true, "test")))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_TIMETABLE_NOT_FOUND")
    fun testPatchShuttleTimetableNotFound() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/timetable/9998")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(PatchTimetableRequest("test", true, "test")))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isNotFound)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("SPECIFIED_SEQ_NOT_FOUND"))
    }

    @Test
    @DisplayName("PATCH_SHUTTLE_TIMETABLE_SUCCESS")
    fun testPatchShuttleTimetableSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.patch("/api/shuttle/timetable/9999")
                .header("Authorization", getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(Response.objectMapper.writeValueAsString(PatchTimetableRequest("test", true, "test")))
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isAccepted)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("UPDATED"))
    }

        @Test
    @DisplayName("GET_SHUTTLE_TIMETABLE_VIEW_UNAUTHORIZED")
    fun testGetShuttleTimetableViewUnauthorized() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/timetable/view")
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    @DisplayName("GET_SHUTTLE_TIMETABLE_VIEW_SUCCESS")
    fun testGetShuttleTimetableViewSuccess() {
        mockmvc.perform(
            MockMvcRequestBuilders.get("/api/shuttle/timetable/view?page=1&period=test")
                .header("Authorization", getAccessToken())
        )
        .andDo(print())
        .andExpect(MockMvcResultMatchers.status().isOk)
        .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].periodType").value("test"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].stopName").value("startStop"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].routeName").value("test"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].routeType").value("test"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].departureTime").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].weekdays").value(true))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].periodType").value("test"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].stopName").value("endStop"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].routeName").value("test"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].routeType").value("test"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].departureTime").isNotEmpty)
        .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].weekdays").value(true))
    }
}