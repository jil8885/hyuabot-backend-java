package app.hyuabot.backend.service

import app.hyuabot.backend.domain.Campus
import app.hyuabot.backend.domain.library.ReadingRoom
import app.hyuabot.backend.repository.CampusRepository
import app.hyuabot.backend.repository.library.ReadingRoomRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class LibraryServiceTest {
    @Autowired private lateinit var libraryService: LibraryService
    @Autowired private lateinit var campusRepository: CampusRepository
    @Autowired private lateinit var readingRoomRepository: ReadingRoomRepository

    @BeforeEach
    fun init() {
        clean()
        campusRepository.saveAll(listOf(
            Campus(id = 999, name = "SEOUL"),
        ))
        readingRoomRepository.saveAll(listOf(
            ReadingRoom(id = 999, campusID = 999, name = "TEST", isActive = true, isReservable = true, total = 100, active = 100, occupied = 0),
            ReadingRoom(id = 998, campusID = 999, name = "TEST2", isActive = true, isReservable = true, total = 100, active = 100, occupied = 0),
            ReadingRoom(id = 997, campusID = 999, name = "TEST3", isActive = true, isReservable = true, total = 100, active = 100, occupied = 0),
            ReadingRoom(id = 996, campusID = 999, name = "TEST4", isActive = true, isReservable = true, total = 100, active = 100, occupied = 0),
        ))
    }

    @AfterEach
    fun clean() {
        readingRoomRepository.deleteAllByIdInBatch(listOf(996, 997, 998, 999))
        campusRepository.deleteAllByIdInBatch(listOf(999))
    }

    @Test
    @DisplayName("GET_READING_ROOM_LIST")
    fun getReadingRoomList() {
        val result = libraryService.getReadingRoomList()
        assertTrue(result.isNotEmpty())
    }

    @Test
    @DisplayName("GET_READING_ROOM")
    fun getReadingRoom() {
        val result = libraryService.getReadingRoom(999)
        assertTrue(result.name == "TEST")
        assertTrue(result.campus?.name == "SEOUL")
        assertTrue(result.available == 100)
        assertTrue(result.occupied == 0)
        assertTrue(result.active == 100)
    }

    @Test
    @DisplayName("GET_READING_ROOM_LIST_BY_CAMPUS")
    fun getReadingRoomListByCampus() {
        val result = libraryService.getReadingRoomListByCampus(999)
        result.forEach {
            assertTrue(it.campusID == 999)
        }
    }

    @Test
    @DisplayName("POST_READING_ROOM")
    fun postReadingRoom() {
        libraryService.postReadingRoom(995, 999, "TEST5", 100)
        val result = libraryService.getReadingRoom(995)
        assertTrue(result.name == "TEST5")
        assertTrue(result.campus?.name == "SEOUL")
        assertTrue(result.available == 100)
        assertTrue(result.occupied == 0)
        assertTrue(result.active == 100)
        readingRoomRepository.deleteById(995)
    }

    @Test
    @DisplayName("POST_READING_ROOM_DUPLICATED")
    fun postReadingRoomDuplicated() {
        assertTrue(assertThrows<Exception> {
            libraryService.postReadingRoom(999, 999, "TEST", 100)
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("DELETE_READING_ROOM")
    fun deleteReadingRoom() {
        libraryService.deleteReadingRoom(999)
        assertTrue(assertThrows<Exception> {
            libraryService.getReadingRoom(999)
        }.message == "SPECIFIED_ROOM_NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_READING_ROOM_NOT_FOUND")
    fun deleteReadingRoomNotFound() {
        assertTrue(assertThrows<NullPointerException> {
            libraryService.deleteReadingRoom(995)
        }.message == "SPECIFIED_ROOM_NOT_FOUND")
    }

    @Test
    @DisplayName("UPDATE_READING_ROOM")
    fun updateReadingRoom() {
        libraryService.updateReadingRoom(999, 999, "TEST5", 100)
        val result = libraryService.getReadingRoom(999)
        assertTrue(result.name == "TEST5")
        assertTrue(result.campus?.name == "SEOUL")
        assertTrue(result.available == 100)
        assertTrue(result.occupied == 0)
        assertTrue(result.active == 100)
    }

    @Test
    @DisplayName("UPDATE_READING_ROOM_NOT_FOUND")
    fun updateReadingRoomNotFound() {
        assertTrue(assertThrows<NullPointerException> {
            libraryService.updateReadingRoom(995, 999, "TEST5", 100)
        }.message == "SPECIFIED_ROOM_NOT_FOUND")
    }
}