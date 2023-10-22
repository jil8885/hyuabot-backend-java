package app.hyuabot.backend.repository.cafeteria

import app.hyuabot.backend.domain.cafeteria.Menu
import app.hyuabot.backend.dto.database.MenuPK
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface MenuRepository : JpaRepository<Menu, MenuPK> {
    fun findByCafeteriaID(cafeteriaID: Int): List<Menu>
    fun findByCafeteriaIDAndDate(cafeteriaID: Int, date: LocalDate): List<Menu>
    fun findByDate(date: LocalDate): List<Menu>
    fun findByCafeteriaIDAndDateAndType(cafeteriaID: Int, date: LocalDate, type: String): List<Menu>
    fun deleteAllByCafeteriaID(cafeteriaID: Int)
}