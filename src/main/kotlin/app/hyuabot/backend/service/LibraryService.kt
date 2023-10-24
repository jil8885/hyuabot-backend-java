package app.hyuabot.backend.service

import app.hyuabot.backend.domain.library.ReadingRoom
import app.hyuabot.backend.repository.library.ReadingRoomRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class LibraryService(private val readingRoomRepository: ReadingRoomRepository) {
    @Transactional
    fun getReadingRoomList(): List<ReadingRoom> = readingRoomRepository.findAll()

    @Transactional
    fun getReadingRoom(roomID: Int): ReadingRoom = readingRoomRepository.findById(roomID).orElseThrow {
        Exception("SPECIFIED_ROOM_NOT_FOUND")
    }

    @Transactional
    fun getReadingRoomListByCampus(campus: Int): List<ReadingRoom> =
        readingRoomRepository.findByCampusID(campus)

    @Transactional
    fun postReadingRoom(roomID: Int, campus: Int, name: String, capacity: Int) {
        if (readingRoomRepository.existsById(roomID)) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            readingRoomRepository.save(
                ReadingRoom(
                    id = roomID,
                    campusID = campus,
                    name = name,
                    isActive = true,
                    isReservable = true,
                    total = capacity,
                    active = capacity,
                    occupied = 0,
                    available = capacity,
                    updatedAt = LocalDateTime.now(),
                )
            )
        }
    }

    @Transactional
    fun deleteReadingRoom(roomID: Int) {
        val item = readingRoomRepository.findById(roomID).orElseThrow {
            throw NullPointerException("SPECIFIED_ROOM_NOT_FOUND")
        }
        readingRoomRepository.delete(item)
    }

    @Transactional
    fun updateReadingRoom(roomID: Int, campus: Int?, name: String?, capacity: Int?) {
        readingRoomRepository.findById(roomID).orElseThrow {
            throw NullPointerException("SPECIFIED_ROOM_NOT_FOUND")
        }.let {
            it.campusID = campus ?: it.campusID
            it.name = name ?: it.name
            it.total = capacity ?: it.total
            it.active = capacity ?: it.active
            it.available = capacity ?: it.available
            it.updatedAt = LocalDateTime.now()
        }
    }
}