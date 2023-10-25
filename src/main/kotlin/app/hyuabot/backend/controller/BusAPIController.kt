package app.hyuabot.backend.controller

import app.hyuabot.backend.dto.response.Response
import app.hyuabot.backend.dto.response.bus.BusRouteDto
import app.hyuabot.backend.dto.response.bus.BusStopDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "버스 API", description = "캠퍼스 주변 버스 정보를 제공하는 API입니다.")
@RestController
@RequestMapping("/api/bus")
class BusAPIController {
    @Operation(summary = "버스 노선 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "조건에 맞는 버스 노선 목록을 반환합니다.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = BusRouteDto.BusRouteList::class))]
    )
    @GetMapping("/route", produces = ["application/json"])
    fun getBusRouteList(
        @Parameter(description = "버스 운행 회사", example = "경원여객")
        @RequestParam("company")
        company: String? = null,
        @Parameter(description = "버스 노선 유형", example = "직행좌석형시내버스")
        @RequestParam("type")
        type: String? = null,
        @Parameter(description = "버스 노선 이름", example = "3102")
        @RequestParam("name") name: String? = null,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 노선 항목 추가")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "버스 노선 항목을 추가합니다.",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Response.SuccessResponse::class),
                    examples = [ExampleObject(value = "{\n  \"message\": \"SUCCESS_TO_CREATE_BUS_ROUTE\"\n}")]
                ),
            ],
        ),
        ApiResponse(
            responseCode = "400",
            description = "필수 항목이 누락되었습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"MISSING_REQUIRED_FIELD\",\n  \"path\": \"/api/bus/route\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 버스 노선 항목입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"CONFLICT_ROUTE_ID\",\n  \"path\": \"/api/bus/route\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )]
        )
    ])
    @PostMapping("/route", consumes = ["application/json"], produces = ["application/json"])
    fun addBusRoute(
        @Parameter(description = "버스 노선 항목", required = true)
        @RequestBody
        busRoute: BusRouteDto.BusRouteItem,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 노선 항목 조회")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "특정 ID의 버스 노선 항목을 반환합니다.",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = BusRouteDto.BusRouteItem::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "ID에 해당하는 버스 노선 항목이 없습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"BUS_ROUTE_NOT_FOUND\",\n  \"path\": \"/api/bus/route/1\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )]
        )
    ])
    @GetMapping("/route/{id}", produces = ["application/json"])
    fun getBusRoute(
        @Parameter(description = "버스 노선 ID", example = "216000061")
        @PathVariable("id") id: Int,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 노선 항목 수정")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "특정 ID의 버스 노선 항목을 수정합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"SUCCESS_TO_UPDATE_BUS_ROUTE\"\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "ID에 해당하는 버스 노선 항목이 없습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"BUS_ROUTE_NOT_FOUND\",\n  \"path\": \"/api/bus/route/1\",\n}")]
            )]
        )
    ])
    @PatchMapping("/route/{id}", consumes = ["application/json"], produces = ["application/json"])
    fun updateBusRoute(
        @Parameter(description = "버스 노선 ID", example = "216000061")
        @PathVariable("id") id: Int,
        @Parameter(description = "버스 노선 변경 사항", required = true)
        @RequestBody
        busRoute: BusRouteDto.BusRouteItem,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 노선 항목 삭제")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "204",
            description = "특정 ID의 버스 노선 항목을 삭제합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"SUCCESS_TO_DELETE_BUS_ROUTE\"\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "406",
            description = "해당 버스 노선이 경유하도록 등록된 정류장이 존재합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"BUS_ROUTE_HAS_STOPS\",\n  \"path\": \"/api/bus/route/1\",\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "ID에 해당하는 버스 노선 항목이 없습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"BUS_ROUTE_NOT_FOUND\",\n  \"path\": \"/api/bus/route/1\",\n}")]
            )]
        )
    ])
    @DeleteMapping("/route/{id}")
    fun deleteBusRoute(
        @Parameter(description = "버스 노선 ID", example = "216000061")
        @PathVariable("id") id: Int,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 정류장 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "조건에 맞는 버스 정류장 목록을 반환합니다.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = BusStopDto.BusStopList::class))]
    )
    @GetMapping("/stop", produces = ["application/json"])
    fun getBusStopList(
        @Parameter(description = "버스 정류장 이름", example = "한양대학교")
        @RequestParam("name")
        name: String? = null,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 정류장 항목 추가")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "버스 정류장 항목을 추가합니다.",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Response.SuccessResponse::class),
                    examples = [ExampleObject(value = "{\n  \"message\": \"SUCCESS_TO_CREATE_BUS_STOP\"\n}")]
                ),
            ],
        ),
        ApiResponse(
            responseCode = "400",
            description = "필수 항목이 누락되었습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"MISSING_REQUIRED_FIELD\",\n  \"path\": \"/api/bus/stop\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 버스 정류장 항목입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"CONFLICT_STOP_ID\",\n  \"path\": \"/api/bus/stop\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )]
        )
    ])
    @PostMapping("/stop", consumes = ["application/json"], produces = ["application/json"])
    fun addBusStop(
        @Parameter(description = "버스 정류장 항목", required = true)
        @RequestBody
        busStop: BusStopDto.BusStopItem,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 정류장 항목 조회")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "특정 ID의 버스 정류장 항목을 반환합니다.",
            content = [Content(mediaType = "application/json", schema = Schema(implementation = BusStopDto.BusStopItem::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "ID에 해당하는 버스 정류장 항목이 없습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"BUS_STOP_NOT_FOUND\",\n  \"path\": \"/api/bus/stop/1\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )]
        )
    ])
    @GetMapping("/stop/{id}", produces = ["application/json"])
    fun getBusStop(
        @Parameter(description = "버스 정류장 ID", example = "216000061")
        @PathVariable("id") id: Int,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 정류장 항목 수정")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "특정 ID의 버스 정류장 항목을 수정합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"SUCCESS_TO_UPDATE_BUS_STOP\"\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "ID에 해당하는 버스 정류장 항목이 없습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"BUS_STOP_NOT_FOUND\",\n  \"path\": \"/api/bus/stop/1\",\n}")]
            )]
        )
    ])
    @PatchMapping("/stop/{id}", consumes = ["application/json"], produces = ["application/json"])
    fun updateBusStop(
        @Parameter(description = "버스 정류장 ID", example = "216000061")
        @PathVariable("id") id: Int,
        @Parameter(description = "버스 정류장 변경 사항", required = true)
        @RequestBody
        busStop: BusStopDto.BusStopItem,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 정류장 항목 삭제")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "204",
            description = "특정 ID의 버스 정류장 항목을 삭제합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"SUCCESS_TO_DELETE_BUS_STOP\"\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "ID에 해당하는 버스 정류장 항목이 없습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"BUS_STOP_NOT_FOUND\",\n  \"path\": \"/api/bus/stop/1\",\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "406",
            description = "해당 정류장을 경유하는 버스 노선이 존재합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"BUS_STOP_HAS_ROUTES\",\n  \"path\": \"/api/bus/stop/1\",\n}")]
            )]
        )
    ])
    @DeleteMapping("/stop/{id}")
    fun deleteBusStop(
        @Parameter(description = "버스 정류장 ID", example = "216000061")
        @PathVariable("id") id: Int,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 정류장 경유 노선 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "조건에 맞는 버스 정류장 경유 노선 목록을 반환합니다.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = BusStopDto.BusStopRouteList::class))]
    )
    @GetMapping("/stop/{id}/route", produces = ["application/json"])
    fun getBusStopRouteList(
        @Parameter(description = "버스 정류장 ID", example = "216000379")
        @PathVariable("id") id: Int,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 정류장 경유 노선 항목 추가")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "버스 정류장 경유 노선 항목을 추가합니다.",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Response.SuccessResponse::class),
                    examples = [ExampleObject(value = "{\n  \"message\": \"SUCCESS_TO_CREATE_BUS_STOP_ROUTE\"\n}")]
                ),
            ],
        ),
        ApiResponse(
            responseCode = "400",
            description = "필수 항목이 누락되었습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"MISSING_REQUIRED_FIELD\",\n  \"path\": \"/api/bus/stop/1/route\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 버스 정류장 경유 노선 항목입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(
                    value = "{\n  \"message\": \"CONFLICT_STOP_ROUTE_ID\",\n  \"path\": \"/api/bus/stop/1/route\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"
                )]
            )]
        )
    ])
    @PostMapping("/stop/{id}/route", consumes = ["application/json"], produces = ["application/json"])
    fun addBusStopRoute(
        @Parameter(description = "버스 정류장 ID", example = "216000379")
        @PathVariable("id") id: Int,
        @Parameter(description = "버스 정류장 경유 노선 항목", required = true)
        @RequestBody
        busStopRoute: BusStopDto.BusStopRouteListItem,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 정류장 경유 노선 항목 삭제")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "204",
            description = "특정 ID의 버스 정류장 경유 노선 항목을 삭제합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"SUCCESS_TO_DELETE_BUS_STOP_ROUTE\"\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "ID에 해당하는 버스 정류장 경유 노선 항목이 없습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"BUS_STOP_ROUTE_NOT_FOUND\",\n  \"path\": \"/api/bus/stop/1/route/1\",\n}")]
            )]
        )
    ])
    @DeleteMapping("/stop/{id}/route/{routeId}")
    fun deleteBusStopRoute(
        @Parameter(description = "버스 정류장 ID", example = "216000379")
        @PathVariable("id") id: Int,
        @Parameter(description = "버스 정류장 경유 노선 ID", example = "216000061")
        @PathVariable("routeId") routeId: Int,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 노선 시간표 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "조건에 맞는 버스 노선 시간표 목록을 반환합니다.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = BusRouteDto.BusTimetableList::class))]
    )
    @GetMapping("/route/{id}/timetable/{startStopID}", produces = ["application/json"])
    fun getBusRouteTimetableList(
        @Parameter(description = "버스 노선 ID", example = "216000061")
        @PathVariable("id") id: Int,
        @Parameter(description = "버스 출발 정류장 ID", example = "216000379")
        @PathVariable("startStopID") startStopID: Int,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 노선 시간표 항목 추가")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "버스 노선 시간표 항목을 추가합니다.",
            content = [
                Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Response.SuccessResponse::class),
                    examples = [ExampleObject(value = "{\n  \"message\": \"SUCCESS_TO_CREATE_BUS_ROUTE_TIMETABLE\"\n}")]
                ),
            ],
        ),
        ApiResponse(
            responseCode = "400",
            description = "필수 항목이 누락되었습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"MISSING_REQUIRED_FIELD\",\n  \"path\": \"/api/bus/route/1/timetable\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 버스 노선 시간표 항목입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"CONFLICT_ROUTE_TIMETABLE_ID\",\n  \"path\": \"/api/bus/route/1/timetable\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )]
        )
    ])
    @PostMapping("/route/{id}/timetable/{startStopID}", consumes = ["application/json"], produces = ["application/json"])
    fun addBusRouteTimetable(
        @Parameter(description = "버스 노선 ID", example = "216000061")
        @PathVariable("id") id: Int,
        @Parameter(description = "버스 출발 정류장 ID", example = "216000379")
        @PathVariable("startStopID") startStopID: Int,
        @Parameter(description = "버스 노선 시간표 항목", required = true)
        @RequestBody
        busRouteTimetable: BusRouteDto.BusTimetableListItem,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }

    @Operation(summary = "버스 노선 시간표 항목 삭제")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "204",
            description = "특정 ID의 버스 노선 시간표 항목을 삭제합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"SUCCESS_TO_DELETE_BUS_ROUTE_TIMETABLE\"\n}")]
            )]
        ),
        ApiResponse(
            responseCode = "404",
            description = "ID에 해당하는 버스 노선 시간표 항목이 없습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"BUS_ROUTE_TIMETABLE_NOT_FOUND\",\n  \"path\": \"/api/bus/route/1/timetable/1\",\n}")]
            )]
        )
    ])
    @DeleteMapping("/route/{id}/timetable/{startStopID}/{weekday}/{departureTime}")
    fun deleteBusRouteTimetable(
        @Parameter(description = "버스 노선 ID", example = "216000061")
        @PathVariable("id") id: Int,
        @Parameter(description = "버스 출발 정류장 ID", example = "216000379")
        @PathVariable("startStopID") startStopID: Int,
        @Parameter(description = "버스 출발 요일", example = "weekdays")
        @PathVariable("weekday") weekday: String,
        @Parameter(description = "버스 출발 시간", example = "05:00")
        @PathVariable("departureTime") departureTime: String,
    ): ResponseEntity<String> {
        return ResponseEntity.ok("")
    }
}
