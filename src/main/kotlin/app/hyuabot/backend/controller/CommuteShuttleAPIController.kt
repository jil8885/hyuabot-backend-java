package app.hyuabot.backend.controller

import app.hyuabot.backend.dto.response.Response
import app.hyuabot.backend.dto.response.commute.CommuteShuttleRouteDto
import app.hyuabot.backend.dto.response.commute.CommuteShuttleStopDto
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

@Tag(name = "통학버스 API", description = "등하교 시간대 운행하는 통학버스의 정보를 제공합니다.")
@RestController
@RequestMapping("/api/commute")
class CommuteShuttleAPIController {
    @Operation(summary = "통학버스 노선 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "통학버스 노선 목록을 반환합니다.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = CommuteShuttleRouteDto.CommuteShuttleRouteList::class))],
    )
    @GetMapping("/route")
    fun getShuttleRouteList(): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 노선 항목 추가")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "통학버스 노선 항목을 추가합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"success\": true,\n  \"message\": \"SUCCESS_TO_CREATE_SHUTTLE_ROUTE\"\n}")],
            )],
        ),
        ApiResponse(
            responseCode = "400",
            description = "필수 항목이 누락되었습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"MISSING_REQUIRED_FIELD\",\n  \"path\": \"/api/commute/route\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 노선 이름입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"CONFLICT_ROUTE_ID\",\n  \"path\": \"/api/commute/route\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
    ])
    @PostMapping("/route")
    fun createShuttleRoute(
        @Parameter(description = "추가할 노선 정보")
        @RequestBody body: CommuteShuttleRouteDto.CommuteShuttleRouteItem,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 노선 항목 조회")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "통학버스 노선 항목을 반환합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = CommuteShuttleRouteDto.CommuteShuttleRouteItem::class),
            )],
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 노선 ID입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_ROUTE_NOT_FOUND\",\n  \"path\": \"/api/commute/route/1\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
    ])
    @GetMapping("/route/{id}")
    fun getShuttleRouteItem(
        @Parameter(description = "노선 ID", example = "1")
        @PathVariable id: String,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 노선 항목 수정")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "통학버스 노선 항목을 수정합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"success\": true,\n  \"message\": \"SUCCESS_TO_UPDATE_COMMUTE_SHUTTLE_ROUTE\"\n}")],
            )],
        ),
        ApiResponse(
            responseCode = "400",
            description = "필수 항목이 누락되었습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"MISSING_REQUIRED_FIELD\",\n  \"path\": \"/api/commute/route/1\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 노선 ID입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_ROUTE_NOT_FOUND\",\n  \"path\": \"/api/commute/route/1\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
    ])
    @PatchMapping("/route/{id}")
    fun updateShuttleRoute(
        @Parameter(description = "노선 ID", example = "1")
        @PathVariable id: String,
        @Parameter(description = "노선 변경 정보")
        @RequestBody body: CommuteShuttleRouteDto.CommuteShuttleRouteItem,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 노선 항목 삭제")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "통학버스 노선 항목을 삭제합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"success\": true,\n  \"message\": \"SUCCESS_TO_DELETE_COMMUTE_SHUTTLE_ROUTE\"\n}")],
            )],
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 노선 ID입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_ROUTE_NOT_FOUND\",\n  \"path\": \"/api/commute/route/1\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
    ])
    @DeleteMapping("/route/{id}")
    fun deleteShuttleRoute(
        @Parameter(description = "노선 ID", example = "1")
        @PathVariable id: String,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 정류장 목록 조회")
    @ApiResponse(
        responseCode = "200",
        description = "통학버스 정류장 목록을 반환합니다.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = CommuteShuttleStopDto.CommuteShuttleStopList::class))],
    )
    @GetMapping("/stop")
    fun getShuttleStopList(): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 정류장 항목 추가")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "통학버스 정류장 항목을 추가합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"success\": true,\n  \"message\": \"SUCCESS_TO_CREATE_SHUTTLE_STOP\"\n}")],
            )],
        ),
        ApiResponse(
            responseCode = "400",
            description = "필수 항목이 누락되었습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"MISSING_REQUIRED_FIELD\",\n  \"path\": \"/api/commute/stop\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 정류장 이름입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"CONFLICT_STOP_NAME\",\n  \"path\": \"/api/commute/stop\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
    ])
    @PostMapping("/stop")
    fun createShuttleStop(
        @Parameter(description = "추가할 정류장 정보")
        @RequestBody body: CommuteShuttleStopDto.CommuteShuttleStopListItem,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 정류장 항목 조회")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "통학버스 정류장 항목을 반환합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = CommuteShuttleStopDto.CommuteShuttleStopListItem::class),
            )],
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 정류장 이름입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_STOP_NOT_FOUND\",\n  \"path\": \"/api/commute/stop/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
    ])
    @GetMapping("/stop/{name}")
    fun getShuttleStopItem(
        @Parameter(description = "정류장 이름", example = "한양대학교")
        @PathVariable name: String,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 정류장 항목 수정")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "통학버스 정류장 항목을 수정합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"success\": true,\n  \"message\": \"SUCCESS_TO_UPDATE_COMMUTE_SHUTTLE_STOP\"\n}")],
            )],
        ),
        ApiResponse(
            responseCode = "400",
            description = "필수 항목이 누락되었습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"MISSING_REQUIRED_FIELD\",\n  \"path\": \"/api/commute/stop/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 정류장 이름입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_STOP_NOT_FOUND\",\n  \"path\": \"/api/commute/stop/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
    ])
    @PatchMapping("/stop/{name}")
    fun updateShuttleStop(
        @Parameter(description = "정류장 이름", example = "한양대학교")
        @PathVariable name: String,
        @Parameter(description = "정류장 변경 정보")
        @RequestBody body: CommuteShuttleStopDto.CommuteShuttleStopListItem,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 정류장 항목 삭제")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "통학버스 정류장 항목을 삭제합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"success\": true,\n  \"message\": \"SUCCESS_TO_DELETE_COMMUTE_SHUTTLE_STOP\"\n}")],
            )],
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 정류장 이름입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_STOP_NOT_FOUND\",\n  \"path\": \"/api/commute/stop/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}")]
            )],
        ),
    ])
    @DeleteMapping("/stop/{name}")
    fun deleteShuttleStop(
        @Parameter(description = "정류장 이름", example = "한양대학교")
        @PathVariable name: String,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 운행 시간표 조회")
    @ApiResponse(
        responseCode = "200",
        description = "통학버스 운행 시간표를 반환합니다.",
        content = [Content(mediaType = "application/json", schema = Schema(implementation = CommuteShuttleRouteDto.CommuteShuttleTimeTableList::class))],
    )
    @GetMapping("/timetable")
    fun getShuttleTimeTableList(): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 운행 시간표 항목 추가")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "201",
            description = "통학버스 운행 시간표 항목을 추가합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"success\": true,\n  \"message\": \"SUCCESS_TO_CREATE_SHUTTLE_TIMETABLE\"\n}")],
            )],
        ),
        ApiResponse(
            responseCode = "400",
            description = "필수 항목이 누락되었습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"MISSING_REQUIRED_FIELD\",\n  \"path\": \"/api/commute/timetable\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                    ExampleObject(value = "{\n  \"message\": \"INVALID_DEPARTURE_TIME\",\n  \"path\": \"/api/commute/timetable\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                ]
            )],
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 노선 ID 또는 정류장 이름입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_ROUTE_NOT_FOUND\",\n  \"path\": \"/api/commute/timetable\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                    ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_STOP_NOT_FOUND\",\n  \"path\": \"/api/commute/timetable\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                ]
            )],
        ),
        ApiResponse(
            responseCode = "409",
            description = "이미 존재하는 시간표입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"CONFLICT_SHUTTLE_TIMETABLE\",\n  \"path\": \"/api/commute/timetable\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                ]
            )],
        )
    ])
    @PostMapping("/timetable")
    fun createShuttleTimeTable(
        @Parameter(description = "추가할 시간표 정보")
        @RequestBody body: CommuteShuttleRouteDto.CommuteShuttleTimeTableListItem,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 운행 시간표 항목 조회")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "통학버스 운행 시간표 항목을 반환합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = CommuteShuttleRouteDto.CommuteShuttleTimeTableListItem::class),
            )],
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 노선 ID 또는 정류장 이름입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_ROUTE_NOT_FOUND\",\n  \"path\": \"/api/commute/timetable/A/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                    ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_STOP_NOT_FOUND\",\n  \"path\": \"/api/commute/timetable/A/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                ]
            )],
        ),
    ])
    @GetMapping("/timetable/{routeID}/{stopName}")
    fun getShuttleTimeTableItem(
        @Parameter(description = "노선 ID", example = "A")
        @PathVariable routeID: String,
        @Parameter(description = "정류장 이름", example = "한양대학교")
        @PathVariable stopName: String,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 운행 시간표 항목 수정")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "통학버스 운행 시간표 항목을 수정합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"success\": true,\n  \"message\": \"SUCCESS_TO_UPDATE_SHUTTLE_TIMETABLE\"\n}")],
            )],
        ),
        ApiResponse(
            responseCode = "400",
            description = "필수 항목이 누락되었습니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"MISSING_REQUIRED_FIELD\",\n  \"path\": \"/api/commute/timetable/A/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                    ExampleObject(value = "{\n  \"message\": \"INVALID_DEPARTURE_TIME\",\n  \"path\": \"/api/commute/timetable/A/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                ]
            )],
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 노선 ID 또는 정류장 이름입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_ROUTE_NOT_FOUND\",\n  \"path\": \"/api/commute/timetable/A/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                    ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_STOP_NOT_FOUND\",\n  \"path\": \"/api/commute/timetable/A/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                ]
            )],
        ),
    ])
    @PatchMapping("/timetable/{routeID}/{stopName}")
    fun updateShuttleTimeTable(
        @Parameter(description = "노선 ID", example = "A")
        @PathVariable routeID: String,
        @Parameter(description = "정류장 이름", example = "한양대학교")
        @PathVariable stopName: String,
        @Parameter(description = "시간표 변경 정보")
        @RequestBody body: CommuteShuttleRouteDto.CommuteShuttleTimeTableListItem,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }

    @Operation(summary = "통학버스 운행 시간표 항목 삭제")
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            description = "통학버스 운행 시간표 항목을 삭제합니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.SuccessResponse::class),
                examples = [ExampleObject(value = "{\n  \"success\": true,\n  \"message\": \"SUCCESS_TO_DELETE_SHUTTLE_TIMETABLE\"\n}")],
            )],
        ),
        ApiResponse(
            responseCode = "404",
            description = "존재하지 않는 노선 ID 또는 정류장 이름입니다.",
            content = [Content(
                mediaType = "application/json",
                schema = Schema(implementation = Response.ErrorResponse::class),
                examples = [
                    ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_ROUTE_NOT_FOUND\",\n  \"path\": \"/api/commute/timetable/A/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                    ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_STOP_NOT_FOUND\",\n  \"path\": \"/api/commute/timetable/A/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                    ExampleObject(value = "{\n  \"message\": \"COMMUTE_SHUTTLE_TIMETABLE_NOT_FOUND\",\n  \"path\": \"/api/commute/timetable/A/한양대학교\",\n  \"timestamp\": \"2021-10-03T15:00:00.000\"\n}"),
                ]
            )],
        ),
    ])
    @DeleteMapping("/timetable/{routeID}/{stopName}")
    fun deleteShuttleTimeTable(
        @Parameter(description = "노선 ID", example = "A")
        @PathVariable routeID: String,
        @Parameter(description = "정류장 이름", example = "한양대학교")
        @PathVariable stopName: String,
    ): ResponseEntity<String> { return ResponseEntity.ok("NOT_IMPLEMENTED") }
}