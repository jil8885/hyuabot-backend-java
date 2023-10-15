package app.hyuabot.backend.controller

import app.hyuabot.backend.dto.response.Response
import app.hyuabot.backend.service.ShuttleService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/shuttle")
class ShuttleAPIController(
    private val shuttleService: ShuttleService,
) {
    @GetMapping(
        "/timetable/view",
        produces = ["application/json"],
    )
    fun getShuttleTimetableView(
        @RequestParam("period") periodType: String? = null,
        @RequestParam("weekday") isWeekdays: Boolean? = null,
        @RequestParam("stop") stopName: String? = null,
        @RequestParam("page") page: Int = 1,
    ): ResponseEntity<String> {
        val query = shuttleService.getShuttleTimetableView(
            periodType = periodType,
            isWeekdays = isWeekdays,
            stopName = stopName,
            page = page - 1,
        )
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(Response.objectMapper.writeValueAsString(query))
    }
}