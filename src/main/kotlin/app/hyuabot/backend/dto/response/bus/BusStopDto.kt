package app.hyuabot.backend.dto.response.bus

import io.swagger.v3.oas.annotations.media.Schema

object BusStopDto {
    @Schema(description = "버스 정류장 목록 조회 응답")
    data class BusStopList (
        @Schema(description = "버스 정류장 목록")
        val content: List<BusStopListItem>,
    )

    @Schema(description = "버스 정류장 목록 내 항목")
    data class BusStopListItem (
        @Schema(description = "버스 정류장 ID", example = "1")
        val id: Int,
        @Schema(description = "버스 정류장 이름", example = "한양대학교")
        val name: String,
    )

    @Schema(description = "버스 정류장 항목 조회 응답")
    data class BusStopItem (
        @Schema(description = "버스 정류장 ID", example = "1")
        val id: Int,
        @Schema(description = "버스 정류장 이름", example = "3102")
        val name: String? = null,
        @Schema(description = "버스 정류장 검색 ID", example = "17363")
        val mobileNumber: String? = null,
        @Schema(description = "버스 정류장 위치")
        val location: BusStopLocation? = null,
    )

    @Schema(description = "버스 정류장 위치")
    data class BusStopLocation (
        @Schema(description = "버스 정류장 위도", example = "37.555555")
        val latitude: Double? = null,
        @Schema(description = "버스 정류장 경도", example = "127.000000")
        val longitude: Double? = null,
        @Schema(description = "버스 정류장 지역", example = "서울")
        val region: String? = null,
    )

    @Schema(description = "버스 정류장 경유 노선 목록 조회 응답")
    data class BusStopRouteList (
        @Schema(description = "버스 정류장 경유 노선 목록")
        val content: List<BusStopRouteListItem>,
    )

    @Schema(description = "버스 정류장 경유 노선 목록 내 항목")
    data class BusStopRouteListItem (
        @Schema(description = "버스 정류장 경유 노선 ID", example = "1")
        val id: Int,
        @Schema(description = "버스 정류장 경유 노선 이름", example = "3102")
        val name: String,
        @Schema(description = "버스 노선의 경유 순서", example = "1")
        val seq: Int,
        @Schema(description = "버스 노선의 출발 정류장 ID", example = "1")
        val startStopID: Int,
    )
}