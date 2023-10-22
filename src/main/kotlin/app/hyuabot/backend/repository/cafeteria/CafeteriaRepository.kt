package app.hyuabot.backend.repository.cafeteria

import app.hyuabot.backend.domain.cafeteria.Cafeteria
import org.springframework.data.jpa.repository.JpaRepository

interface CafeteriaRepository : JpaRepository<Cafeteria, Int> {
    fun findByCampusID(campus: Int): List<Cafeteria>
}