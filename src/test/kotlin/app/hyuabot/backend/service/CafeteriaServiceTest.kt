package app.hyuabot.backend.service

import app.hyuabot.backend.domain.Campus
import app.hyuabot.backend.domain.cafeteria.Cafeteria
import app.hyuabot.backend.domain.cafeteria.Menu
import app.hyuabot.backend.dto.database.MenuPK
import app.hyuabot.backend.repository.CampusRepository
import app.hyuabot.backend.repository.cafeteria.CafeteriaRepository
import app.hyuabot.backend.repository.cafeteria.MenuRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@SpringBootTest
@ActiveProfiles("test")
class CafeteriaServiceTest {
    @Autowired private lateinit var cafeteriaService: CafeteriaService
    @Autowired private lateinit var campusRepository: CampusRepository
    @Autowired private lateinit var cafeteriaRepository: CafeteriaRepository
    @Autowired private lateinit var menuRepository: MenuRepository

    @BeforeEach
    fun init() {
        clean()
        campusRepository.saveAll(listOf(
            Campus(999, "TEST_CAMPUS_1"),
            Campus(1000, "TEST_CAMPUS_2"),
        ))
        cafeteriaRepository.saveAll(listOf(
            Cafeteria(999, "TEST_CAFETERIA_1", 999, 0.0, 0.0),
            Cafeteria(1000, "TEST_CAFETERIA_2", 999, 0.0, 0.0),
            Cafeteria(1001, "TEST_CAFETERIA_3", 999, 0.0, 0.0),
            Cafeteria(1002, "TEST_CAFETERIA_4", 1000, 0.0, 0.0),
            Cafeteria(1003, "TEST_CAFETERIA_5", 1000, 0.0, 0.0),
        ))
        menuRepository.saveAll(listOf(
            Menu(999, LocalDate.parse("2023-12-31"), "TYPE_1", "TEST_MENU_1", "TEST_CONTENT_1"),
            Menu(999, LocalDate.parse("2023-12-31"), "TYPE_2", "TEST_MENU_2", "TEST_CONTENT_2"),
            Menu(999, LocalDate.parse("2023-12-31"), "TYPE_3", "TEST_MENU_3", "TEST_CONTENT_3"),
            Menu(1000, LocalDate.parse("2024-01-01"), "TYPE_4", "TEST_MENU_4", "TEST_CONTENT_4"),
        ))
    }

    @AfterEach
    fun clean() {
        menuRepository.deleteAllInBatch(listOf(
            Menu(999, LocalDate.parse("2023-12-31"), "TYPE_1", "TEST_MENU_1", "TEST_CONTENT_1"),
            Menu(999, LocalDate.parse("2023-12-31"), "TYPE_2", "TEST_MENU_2", "TEST_CONTENT_2"),
            Menu(999, LocalDate.parse("2023-12-31"), "TYPE_3", "TEST_MENU_3", "TEST_CONTENT_3"),
            Menu(1000, LocalDate.parse("2024-01-01"), "TYPE_4", "TEST_MENU_4", "TEST_CONTENT_4"),
        ))
        cafeteriaRepository.deleteAllByIdInBatch(listOf(999, 1000, 1001, 1002, 1003))
        campusRepository.deleteAllByIdInBatch(listOf(999, 1000))
    }

    @Test
    @DisplayName("GET_CAFETERIA_LIST")
    fun getCafeteriaList() {
        assertTrue(cafeteriaService.getCafeteriaList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_CAFETERIA_BY_CAMPUS")
    fun getCafeteriaByCampus() {
        cafeteriaService.getCafeteriaByCampus(999).forEach {
            assertTrue(it.campusID == 999)
        }
    }

    @Test
    @DisplayName("GET_CAFETERIA")
    fun getCafeteria() {
        assertTrue(cafeteriaService.getCafeteria(999).isPresent)
    }

    @Test
    @DisplayName("GET_MENU_LIST")
    fun getMenuList() {
        assertTrue(cafeteriaService.getMenuList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_MENU_BY_CAFETERIA")
    fun getMenuByCafeteria() {
        cafeteriaService.getMenuByCafeteria(999).forEach {
            assertTrue(it.cafeteriaID == 999)
        }
    }

    @Test
    @DisplayName("GET_MENU_BY_CAFETERIA_AND_DATE")
    fun getMenuByCafeteriaAndDate() {
        cafeteriaService.getMenuByCafeteriaAndDate(999, LocalDate.parse("2023-12-31")).forEach {
            assertTrue(it.cafeteriaID == 999)
            assertTrue(it.date == LocalDate.parse("2023-12-31"))
        }
    }

    @Test
    @DisplayName("GET_MENU_BY_DATE")
    fun getMenuByDate() {
        cafeteriaService.getMenuByDate(LocalDate.parse("2023-12-31")).forEach {
            assertTrue(it.date == LocalDate.parse("2023-12-31"))
        }
    }

    @Test
    @DisplayName("GET_MENU_BY_CAFETERIA_AND_DATE_AND_TYPE")
    fun getMenuByCafeteriaAndDateAndType() {
        cafeteriaService.getMenuByCafeteriaAndDateAndType(999, LocalDate.parse("2023-12-31"), "TYPE_1").forEach {
            assertTrue(it.cafeteriaID == 999)
            assertTrue(it.date == LocalDate.parse("2023-12-31"))
            assertTrue(it.type == "TYPE_1")
        }
    }

    @Test
    @DisplayName("POST_CAFETERIA_SUCCESS")
    fun postCafeteriaSuccess() {
        cafeteriaService.postCafeteria(1004, 999, "TEST_CAFETERIA_6", 0.0, 0.0)
        assertTrue(cafeteriaService.getCafeteria(1004).isPresent)
        cafeteriaRepository.deleteById(1004)
    }

    @Test
    @DisplayName("POST_CAFETERIA_DUPLICATED")
    fun postCafeteriaDuplicated() {
        try {
            cafeteriaService.postCafeteria(999, 999, "TEST_CAFETERIA_6", 0.0, 0.0)
        } catch (e: Exception) {
            assertTrue(e.message == "DUPLICATED")
        }
    }

    @Test
    @DisplayName("PATCH_CAFETERIA_SUCCESS")
    fun patchCafeteriaSuccess() {
        cafeteriaService.patchCafeteria(999, 1000, "TEST_CAFETERIA_6", 0.0, 0.0)
        assertTrue(cafeteriaService.getCafeteria(999).isPresent)
        assertTrue(cafeteriaService.getCafeteria(999).get().campusID == 1000)
        assertTrue(cafeteriaService.getCafeteria(999).get().name == "TEST_CAFETERIA_6")
    }

    @Test
    @DisplayName("PATCH_CAFETERIA_NOT_FOUND")
    fun patchCafeteriaNotFound() {
        assertTrue(assertThrows<Exception> {
            cafeteriaService.patchCafeteria(1004, 1000, "TEST_CAFETERIA_6", 0.0, 0.0)
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_CAFETERIA_SUCCESS")
    fun deleteCafeteriaSuccess() {
        menuRepository.deleteAllByIdInBatch(listOf(
            MenuPK(999, LocalDate.parse("2023-12-31"), "TYPE_1", "TEST_MENU_1"),
            MenuPK(999, LocalDate.parse("2023-12-31"), "TYPE_2", "TEST_MENU_2"),
            MenuPK(999, LocalDate.parse("2023-12-31"), "TYPE_3", "TEST_MENU_3"),
        ))
        cafeteriaService.deleteCafeteria(999)
        assertTrue(cafeteriaService.getCafeteria(999).isEmpty)
    }

    @Test
    @DisplayName("DELETE_CAFETERIA_MENUS_EXIST")
    fun deleteCafeteriaMenusExist() {

        println(assertThrows<Exception> {
            cafeteriaService.deleteCafeteria(999)
        }.message)
        assertTrue(assertThrows<Exception> {
            cafeteriaService.deleteCafeteria(999)
        }.message == "MENUS_EXIST_IN_CAFETERIA")
    }

    @Test
    @DisplayName("DELETE_CAFETERIA_NOT_FOUND")
    fun deleteCafeteriaNotFound() {
        assertTrue(assertThrows<Exception> {
            cafeteriaService.deleteCafeteria(1004)
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("POST_MENU_SUCCESS")
    fun postMenuSuccess() {
        cafeteriaService.postMenu(1000, LocalDate.parse("2024-01-01"), "TYPE_5", "TEST_MENU_5", "TEST_CONTENT_5")
        assertTrue(cafeteriaService.getMenuByCafeteriaAndDateAndType(1000, LocalDate.parse("2024-01-01"), "TYPE_5").isNotEmpty())
        menuRepository.deleteById(MenuPK(1000, LocalDate.parse("2024-01-01"), "TYPE_5", "TEST_MENU_5"))
    }

    @Test
    @DisplayName("POST_MENU_DUPLICATED")
    fun postMenuDuplicated() {
        assertTrue(assertThrows<Exception> {
            cafeteriaService.postMenu(1000, LocalDate.parse("2024-01-01"), "TYPE_4", "TEST_MENU_4", "TEST_CONTENT_4")
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("PATCH_MENU_SUCCESS")
    fun patchMenuSuccess() {
        cafeteriaService.patchMenu(1000, LocalDate.parse("2024-01-01"), "TYPE_4", "TEST_MENU_4", "TEST_CONTENT_5")
        assertTrue(cafeteriaService.getMenuByCafeteriaAndDateAndType(1000, LocalDate.parse("2024-01-01"), "TYPE_4").isNotEmpty())
        assertTrue(cafeteriaService.getMenuByCafeteriaAndDateAndType(1000, LocalDate.parse("2024-01-01"), "TYPE_4").first().menu == "TEST_MENU_4")
        assertTrue(cafeteriaService.getMenuByCafeteriaAndDateAndType(1000, LocalDate.parse("2024-01-01"), "TYPE_4").first().price == "TEST_CONTENT_5")
    }

    @Test
    @DisplayName("PATCH_MENU_NOT_FOUND")
    fun patchMenuNotFound() {
        assertTrue(assertThrows<Exception> {
            cafeteriaService.patchMenu(1000, LocalDate.parse("2024-01-01"), "TYPE_5", "TEST_MENU_5", "TEST_CONTENT_5")
        }.message == "NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_MENU_SUCCESS")
    fun deleteMenuSuccess() {
        cafeteriaService.deleteMenu(1000, LocalDate.parse("2024-01-01"), "TYPE_4", "TEST_MENU_4")
        assertTrue(cafeteriaService.getMenuByCafeteriaAndDateAndType(1000, LocalDate.parse("2024-01-01"), "TYPE_4").isEmpty())
    }

    @Test
    @DisplayName("DELETE_MENU_NOT_FOUND")
    fun deleteMenuNotFound() {
        assertTrue(assertThrows<Exception> {
            cafeteriaService.deleteMenu(1000, LocalDate.parse("2024-01-01"), "TYPE_5", "TEST_MENU_5")
        }.message == "NOT_FOUND")
    }
}