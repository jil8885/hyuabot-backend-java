package app.hyuabot.backend.dto.request.shuttle

import app.hyuabot.backend.dto.database.ShuttleRouteStopPK
import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.time.Duration

data class PostRouteStop (
    val routeName: String,
    val stopName: String,
    var seq: Int,
    var cumulativeTime: String,
)