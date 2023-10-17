package app.hyuabot.backend.controller

import app.hyuabot.backend.domain.shuttle.Timetable
import app.hyuabot.backend.dto.request.shuttle.PatchTimetableRequest
import app.hyuabot.backend.dto.response.Response
import app.hyuabot.backend.service.ShuttleService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @GetMapping(
        "/timetable",
        produces = ["application/json"],
    )
    fun getShuttleTimetable(): ResponseEntity<String> {
        val query = shuttleService.getShuttleTimetable()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(Response.objectMapper.writeValueAsString(Response.ContentResponse(query)))
    }

    @PostMapping(
        "/timetable",
        produces = ["application/json"],
    )
    fun postShuttleTimetable(@RequestBody payload: Timetable): ResponseEntity<String> {
        return try {
            val query = shuttleService.postShuttleTimetable(payload)
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.objectMapper.writeValueAsString(query))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Response.objectMapper.writeValueAsString(e.message))
        }
    }

    @PatchMapping(
        "/timetable/{seq}",
        produces = ["application/json"],
    )
    fun patchShuttleTimetable(
        @PathVariable("seq") seq: Int,
        @RequestBody payload: PatchTimetableRequest,
    ): ResponseEntity<String> {
        return try {
            shuttleService.patchShuttleTimetable(seq, payload)
            ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "UPDATED"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.objectMapper.writeValueAsString(e.message))
        }
    }

    @DeleteMapping(
        "/timetable/{seq}",
        produces = ["application/json"],
    )
    fun deleteShuttleTimetable(
        @PathVariable("seq") seq: Int,
    ): ResponseEntity<String> {
        return try {
            shuttleService.deleteShuttleTimetable(seq)
            ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "DELETED"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.objectMapper.writeValueAsString(e.message))
        }
    }
}