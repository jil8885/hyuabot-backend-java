package app.hyuabot.backend.dto.response.commute

import io.swagger.v3.oas.annotations.media.Schema

object CommuteShuttleRouteDto {
    @Schema(description = "통학버스 노선 목록 조회 응답")
    data class CommuteShuttleRouteList (
        @Schema(description = "통학버스 노선 목록")
        val content: List<CommuteShuttleRouteListItem>,
    )

    @Schema(description = "버스 노선 목록 내 항목")
    data class CommuteShuttleRouteListItem (
        @Schema(description = "버스 노선 이름", example = "A")
        val id: String? = null,
        @Schema(description = "통학버스 노선 설명")
        val description: CommuteShuttleRouteDescription? = null,
    )

    @Schema(description = "버스 노선 항목 조회 응답")
    data class CommuteShuttleRouteItem (
        @Schema(description = "버스 노선 이름", example = "A")
        val id: String? = null,
        @Schema(description = "통학버스 노선 설명")
        val description: CommuteShuttleRouteDescription? = null,
    )

    @Schema(description = "통학버스 노선 설명")
    data class CommuteShuttleRouteDescription (
        @Schema(description = "한국어 설명")
        val korean: String? = null,
        @Schema(description = "영어 설명")
        val english: String? = null,
    )

    @Schema(description = "통학버스 시간표 목록 조회 응답")
    data class CommuteShuttleTimeTableList (
        @Schema(description = "통학버스 시간표 목록")
        val content: List<CommuteShuttleTimeTableListItem>,
    )

    @Schema(description = "통학버스 시간표 목록 내 항목")
    data class CommuteShuttleTimeTableListItem (
        @Schema(description = "통학버스 노선 ID", example = "A")
        val routeID: String? = null,
        @Schema(description = "통학버스 정류장 이름", example = "한양대학교")
        val stopName: String? = null,
        @Schema(description = "통학버스 시간표")
        val deparutreTime: String? = null,
    )
}