package app.hyuabot.backend.repository.cafeteria

import app.hyuabot.backend.domain.cafeteria.Menu
import app.hyuabot.backend.dto.database.MenuPK
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface MenuRepository : JpaRepository<Menu, MenuPK>