package app.hyuabot.backend.service

import app.hyuabot.backend.domain.cafeteria.Cafeteria
import app.hyuabot.backend.domain.cafeteria.Menu
import app.hyuabot.backend.dto.database.MenuPK
import app.hyuabot.backend.repository.cafeteria.CafeteriaRepository
import app.hyuabot.backend.repository.cafeteria.MenuRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate


@Service
@Transactional(readOnly = true)
class CafeteriaService(
    private val cafeteriaRepository: CafeteriaRepository,
    private val menuRepository: MenuRepository,
) {
    @Transactional
    fun getCafeteriaList() = cafeteriaRepository.findAll()

    @Transactional
    fun getCafeteriaByCampus(campus: Int) = cafeteriaRepository.findByCampusID(campus)

    @Transactional
    fun getCafeteria(cafeteriaID: Int) = cafeteriaRepository.findById(cafeteriaID)

    @Transactional
    fun getMenuList() = menuRepository.findAll()

    @Transactional
    fun getMenuByCafeteria(cafeteriaID: Int) = menuRepository.findByCafeteriaID(cafeteriaID)

    @Transactional
    fun getMenuByCafeteriaAndDate(cafeteriaID: Int, date: LocalDate) = menuRepository.findByCafeteriaIDAndDate(cafeteriaID, date)

    @Transactional
    fun getMenuByDate(date: LocalDate) = menuRepository.findByDate(date)

    @Transactional
    fun getMenuByCafeteriaAndDateAndType(cafeteriaID: Int, date: LocalDate, type: String) = menuRepository.findByCafeteriaIDAndDateAndType(cafeteriaID, date, type)

    @Transactional
    fun postCafeteria(
        cafeteriaID: Int, campusID: Int, name: String, latitude: Double, longitude: Double) {
        if (cafeteriaRepository.existsById(cafeteriaID)) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            cafeteriaRepository.save(
                Cafeteria(
                    id = cafeteriaID,
                    campusID = campusID,
                    name = name,
                    latitude = latitude,
                    longitude = longitude,
                )
            )
        }
    }

    @Transactional
    fun patchCafeteria(
        cafeteriaID: Int, campusID: Int?, name: String?, latitude: Double?, longitude: Double?) {
        if (cafeteriaRepository.existsById(cafeteriaID)) {
            cafeteriaRepository.findById(cafeteriaID).map {
                it.campusID = campusID ?: it.campusID
                it.name = name ?: it.name
                it.latitude = latitude ?: it.latitude
                it.longitude = longitude ?: it.longitude
                cafeteriaRepository.save(it)
            }
        } else {
            throw Exception(
                "NOT_FOUND"
            )
        }
    }

    @Transactional
    fun deleteCafeteria(cafeteriaID: Int) {
        if (cafeteriaRepository.existsById(cafeteriaID)) {
            cafeteriaRepository.deleteById(cafeteriaID)
        } else if (menuRepository.findByCafeteriaID(cafeteriaID).isNotEmpty()) {
            throw Exception(
                "MENUS_EXIST_IN_CAFETERIA"
            )
        } else {
            throw Exception(
                "NOT_FOUND"
            )
        }
    }

    @Transactional
    fun postMenu(
        cafeteriaID: Int, date: LocalDate, type: String, menu: String, price: String) {
        if (menuRepository.existsById(MenuPK(cafeteriaID = cafeteriaID, date = date, type = type, menu = menu))) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            menuRepository.save(
                Menu(
                    cafeteriaID = cafeteriaID,
                    date = date,
                    type = type,
                    menu = menu,
                    price = price,
                )
            )
        }
    }

    @Transactional
    fun patchMenu(
        cafeteriaID: Int, date: LocalDate, type: String, menu: String, price: String?) {
        if (menuRepository.existsById(MenuPK(cafeteriaID = cafeteriaID, date = date, type = type, menu = menu))) {
            menuRepository.findById(MenuPK(cafeteriaID = cafeteriaID, date = date, type = type, menu = menu)).map {
                it.price = price ?: it.price
                menuRepository.save(it)
            }
        } else {
            throw Exception(
                "NOT_FOUND"
            )
        }
    }

    @Transactional
    fun deleteMenu(cafeteriaID: Int, date: LocalDate, type: String, menu: String) {
        if (menuRepository.existsById(MenuPK(cafeteriaID = cafeteriaID, date = date, type = type, menu = menu))) {
            menuRepository.deleteById(MenuPK(cafeteriaID = cafeteriaID, date = date, type = type, menu = menu))
        } else {
            throw Exception(
                "NOT_FOUND"
            )
        }
    }

    @Transactional
    fun deleteMenuByCafeteria(cafeteriaID: Int) {
        if (menuRepository.findByCafeteriaID(cafeteriaID).isNotEmpty()) {
            menuRepository.deleteAllByCafeteriaID(cafeteriaID)
        } else {
            throw Exception(
                "NOT_FOUND"
            )
        }
    }
}