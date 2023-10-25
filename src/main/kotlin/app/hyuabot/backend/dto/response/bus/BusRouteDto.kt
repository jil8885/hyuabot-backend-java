package app.hyuabot.backend.dto.response.bus

import io.swagger.v3.oas.annotations.media.Schema

object BusRouteDto {
    @Schema(description = "버스 노선 목록 조회 응답")
    data class BusRouteList (
        @Schema(description = "버스 노선 목록")
        val content: List<BusRouteListItem>,
    )

    @Schema(description = "버스 노선 목록 내 항목")
    data class BusRouteListItem (
        @Schema(description = "버스 노선 ID", example = "1")
        val id: Int,
        @Schema(description = "버스 운행 회사", example = "경원여객")
        val company: String,
        @Schema(description = "버스 노선 유형", example = "직행좌석형시내버스")
        val type: String,
        @Schema(description = "버스 노선 이름", example = "3102")
        val name: String,
    )

    @Schema(description = "버스 노선 항목 조회 응답")
    data class BusRouteItem (
        @Schema(description = "버스 노선 ID", example = "1")
        val id: Int,
        @Schema(description = "버스 노선 이름", example = "3102")
        val name: String? = null,
        @Schema(description = "버스 노선 유형", example = "직행좌석형시내버스")
        val type: String? = null,
        @Schema(description = "버스 노선 시점")
        val start: BusStartEndStop? = null,
        @Schema(description = "버스 노선 종점")
        val end: BusStartEndStop? = null,
        @Schema(description = "버스 첫차 및 막차 정보 (상행)")
        val up: BusFirstLast? = null,
        @Schema(description = "버스 첫차 및 막차 정보 (하행)")
        val down: BusFirstLast? = null,
        @Schema(description = "버스 운영 업체 정보")
        val company: BusCompany? = null,
    )

    @Schema(description = "버스 운영 업체 정보")
    data class BusCompany (
        @Schema(description = "버스 운영 업체 ID", example = "1")
        val id: Int? = null,
        @Schema(description = "버스 운영 업체 이름", example = "경원여객")
        val name: String? = null,
        @Schema(description = "버스 운영 업체 전화번호", example = "031-000-0000")
        val tel: String? = null,
    )

    @Schema(description = "버스 첫차 및 막차 정보")
    data class BusFirstLast (
        @Schema(description = "버스 첫차 시간", example = "05:00")
        val first: String? = null,
        @Schema(description = "버스 막차 시간", example = "23:00")
        val last: String? = null,
    )

    @Schema(description = "버스 시점 및 종점 정류장 정보")
    data class BusStartEndStop (
        @Schema(description = "버스 정류장 ID", example = "1")
        val id: Int? = null,
        @Schema(description = "버스 정류장 이름", example = "한양대학교")
        val name: String? = null,
    )

    @Schema(description = "버스 시간표 목록 조회 응답")
    data class BusTimetableList (
        @Schema(description = "버스 시간표 목록")
        val content: List<BusTimetableListItem>,
    )

    @Schema(description = "버스 시간표 목록 내 항목")
    data class BusTimetableListItem (
        @Schema(description = "버스 노선 ID", example = "1")
        val routeID: Int,
        @Schema(description = "버스 출발 정류장 ID", example = "1")
        val startStopID: Int,
        @Schema(description = "버스 출발 시간", example = "05:00")
        val departureTime: String,
        @Schema(description = "버스 출발 요일", allowableValues = ["weekdays", "saturday", "sunday"])
        val weekday: String,
    )
}