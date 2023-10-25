package app.hyuabot.backend.dto.response.commute

import io.swagger.v3.oas.annotations.media.Schema

object CommuteShuttleStopDto {
    @Schema(description = "통학버스 정류장 목록 조회 응답")
    data class CommuteShuttleStopList (
        @Schema(description = "통학버스 정류장 목록")
        val content: List<CommuteShuttleStopListItem>,
    )

    @Schema(description = "통학버스 정류장 목록 내 항목")
    data class CommuteShuttleStopListItem (
        @Schema(description = "통학버스 정류장 이름", example = "한양대학교")
        val name: String? = null,
        @Schema(description = "통학버스 정류장 부가 설명")
        val description: String? = null,
        @Schema(description = "통학버스 정류장 위치")
        val location: CommuteShuttleStopLocation? = null,
    )

    @Schema(description = "버스 정류장 위치")
    data class CommuteShuttleStopLocation (
        @Schema(description = "통학버스 정류장 위도", example = "37.555555")
        val latitude: Double? = null,
        @Schema(description = "통학버스 정류장 경도", example = "127.000000")
        val longitude: Double? = null,
    )
}