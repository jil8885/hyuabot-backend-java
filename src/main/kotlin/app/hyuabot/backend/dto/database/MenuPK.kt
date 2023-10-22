package app.hyuabot.backend.dto.database

import java.io.Serializable
import java.time.LocalDate

data class MenuPK (
    var cafeteriaID: Int = 0,
    var date: LocalDate = LocalDate.now(),
    var type: String = "",
    var menu: String = "",
    var price: String = "",
): Serializable
