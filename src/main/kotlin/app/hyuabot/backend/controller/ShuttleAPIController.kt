package app.hyuabot.backend.controller

import app.hyuabot.backend.domain.shuttle.Route
import app.hyuabot.backend.domain.shuttle.RouteStop
import app.hyuabot.backend.domain.shuttle.Timetable
import app.hyuabot.backend.dto.database.ShuttlePeriodPK
import app.hyuabot.backend.dto.request.shuttle.PatchPeriodRequest
import app.hyuabot.backend.dto.request.shuttle.PatchRouteRequest
import app.hyuabot.backend.dto.request.shuttle.PatchRouteStopRequest
import app.hyuabot.backend.dto.request.shuttle.PatchTimetableRequest
import app.hyuabot.backend.dto.response.Response
import app.hyuabot.backend.dto.response.ShuttleHolidayItem
import app.hyuabot.backend.dto.response.ShuttlePeriodItem
import app.hyuabot.backend.service.ShuttleService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime

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
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "CONFLICT",
                    "/api/shuttle/timetable"
                )))
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
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "NOT FOUND",
                    "/api/shuttle/timetable/$seq"
                )))
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
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "NOT FOUND",
                    "/api/shuttle/timetable/$seq"
                )))
        }
    }

    @GetMapping(
        "/holiday",
        produces = ["application/json"],
    )
    fun getShuttleHoliday(): ResponseEntity<String> {
        val query = shuttleService.getShuttleHoliday()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(Response.objectMapper.writeValueAsString(Response.ContentResponse(query)))
    }

    @PostMapping(
        "/holiday",
        produces = ["application/json"],
    )
    fun postShuttleHoliday(@RequestBody payload: ShuttleHolidayItem): ResponseEntity<String> {
        return try {
            shuttleService.postShuttleHoliday(payload)
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "CREATED"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "CONFLICT",
                    "/api/shuttle/holiday"
                )))
        }
    }

    @DeleteMapping(
        "/holiday/{date}",
        produces = ["application/json"],
    )
    fun deleteShuttleHoliday(
        @PathVariable("date") date: LocalDate,
    ): ResponseEntity<String> {
        return try {
            shuttleService.deleteShuttleHoliday(date.toString())
            ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "DELETED"
                )))
        } catch (e: DataIntegrityViolationException) {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "BAD REQUEST",
                    "/api/shuttle/holiday/$date"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "NOT FOUND",
                    "/api/shuttle/holiday/$date"
                )))
        }
    }

    @GetMapping(
        "/period",
        produces = ["application/json"],
    )
    fun getShuttlePeriod(): ResponseEntity<String> {
        val query = shuttleService.getShuttlePeriod()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(Response.objectMapper.writeValueAsString(Response.ContentResponse(query)))
    }

    @PostMapping(
        "/period",
        produces = ["application/json"],
    )
    fun postShuttlePeriod(@RequestBody payload: ShuttlePeriodItem): ResponseEntity<String> {
        return try {
            shuttleService.postShuttlePeriod(
                app.hyuabot.backend.domain.shuttle.Period(
                    periodType = payload.period,
                    periodStart = LocalDateTime.parse(payload.start),
                    periodEnd = LocalDateTime.parse(payload.end),
                )
            )
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "CREATED"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "CONFLICT",
                    "/api/shuttle/period"
                )))
        }
    }

    @PatchMapping(
        "/period/{period}/{start}/{end}",
        produces = ["application/json"],
    )
    fun patchShuttlePeriod(
        @PathVariable("period") period: String,
        @PathVariable("start") start: String,
        @PathVariable("end") end: String,
        @RequestBody payload: PatchPeriodRequest,
    ): ResponseEntity<String> {
        return try {
            shuttleService.patchShuttlePeriod(ShuttlePeriodPK(
                periodType = period,
                periodStart = LocalDateTime.parse(start),
                periodEnd = LocalDateTime.parse(end),
            ), payload)
            ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "UPDATED"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "NOT FOUND",
                    "/api/shuttle/period"
                )))
        }
    }

    @DeleteMapping(
        "/period/{period}/{start}/{end}",
        produces = ["application/json"],
    )
    fun deleteShuttlePeriod(
        @PathVariable("period") period: String,
        @PathVariable("start") start: String,
        @PathVariable("end") end: String,
    ): ResponseEntity<String> {
        return try {
            shuttleService.deleteShuttlePeriod(ShuttlePeriodPK(
                periodType = period,
                periodStart = LocalDateTime.parse(start),
                periodEnd = LocalDateTime.parse(end),
            ))
            ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "DELETED"
                )))
        } catch (e: DataIntegrityViolationException) {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "BAD REQUEST",
                    "/api/shuttle/period"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "NOT FOUND",
                    "/api/shuttle/period"
                )))
        }
    }

    @GetMapping("/route", produces = ["application/json"])
    fun getShuttleRoute(): ResponseEntity<String> {
        val query = shuttleService.getShuttleRoute()
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(Response.objectMapper.writeValueAsString(Response.ContentResponse(query)))
    }

    @PostMapping("/route", produces = ["application/json"])
    fun postShuttleRoute(@RequestBody payload: Route): ResponseEntity<String> {
        return try {
            shuttleService.postShuttleRoute(payload)
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "CREATED"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "CONFLICT",
                    "/api/shuttle/route"
                )))
        }
    }

    @PatchMapping("/route/{name}", produces = ["application/json"])
    fun patchShuttleRoute(
        @PathVariable("name") name: String,
        @RequestBody payload: PatchRouteRequest,
    ): ResponseEntity<String> {
        return try {
            shuttleService.patchShuttleRoute(name, payload)
            ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "UPDATED"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "NOT FOUND",
                    "/api/shuttle/route/$name"
                )))
        }
    }

    @DeleteMapping("/route/{name}", produces = ["application/json"])
    fun deleteShuttleRoute(
        @PathVariable("name") name: String,
    ): ResponseEntity<String> {
        return try {
            shuttleService.deleteShuttleRoute(name)
            ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "DELETED"
                )))
        } catch (e: DataIntegrityViolationException) {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "BAD REQUEST",
                    "/api/shuttle/route/$name"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "NOT FOUND",
                    "/api/shuttle/route/$name"
                )))
        }
    }

    @GetMapping("/route/{name}/stop", produces = ["application/json"])
    fun getShuttleRouteStop(
        @PathVariable("name") name: String,
    ): ResponseEntity<String> {
        val query = shuttleService.getShuttleRouteStop(name)
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(Response.objectMapper.writeValueAsString(Response.ContentResponse(query)))
    }

    @PostMapping("/route/{name}/stop", produces = ["application/json"])
    fun postShuttleRouteStop(
        @PathVariable("name") name: String,
        @RequestBody payload: RouteStop,
    ): ResponseEntity<String> {
        return try {
            shuttleService.postShuttleRouteStop(payload)
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "CREATED"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "CONFLICT",
                    "/api/shuttle/route/$name/stop"
                )))
        }
    }

    @PatchMapping("/route/{route}/stop/{stop}", produces = ["application/json"])
    fun patchShuttleRouteStop(
        @PathVariable("route") route: String,
        @PathVariable("stop") stop: String,
        @RequestBody payload: PatchRouteStopRequest,
    ): ResponseEntity<String> {
        return try {
            shuttleService.patchShuttleRouteStop(route, stop, payload)
            ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "UPDATED"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "NOT FOUND",
                    "/api/shuttle/route/$route/stop/$stop"
                )))
        }
    }

    @DeleteMapping("/route/{route}/stop/{stop}", produces = ["application/json"])
    fun deleteShuttleRouteStop(
        @PathVariable("route") route: String,
        @PathVariable("stop") stop: String,
    ): ResponseEntity<String> {
        return try {
            shuttleService.deleteShuttleRouteStop(route, stop)
            ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(Response.objectMapper.writeValueAsString(Response.SuccessResponse(
                    "DELETED"
                )))
        } catch (e: DataIntegrityViolationException) {
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "BAD REQUEST",
                    "/api/shuttle/route/$route/stop/$stop"
                )))
        } catch (e: Exception) {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Response.objectMapper.writeValueAsString(Response.ErrorResponse(
                    e.message ?: "NOT FOUND",
                    "/api/shuttle/route/$route/stop/$stop"
                )))
        }
    }
}