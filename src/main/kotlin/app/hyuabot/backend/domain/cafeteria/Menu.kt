package app.hyuabot.backend.domain.cafeteria

import app.hyuabot.backend.dto.database.MenuPK
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "menu")
@IdClass(MenuPK::class)
data class Menu (
    @Id
    @Column(name = "restaurant_id")
    val cafeteriaID: Int,
    @Id
    @Column(name = "feed_date")
    val date: LocalDate,
    @Id
    @Column(name = "time_type")
    val type: String,
    @Id
    @Column(name = "menu_food")
    val menu: String,
    @Column(name = "menu_price")
    var price: String,

    @ManyToOne
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurant_id", insertable = false, updatable = false)
    val cafeteria: Cafeteria? = null,
)