package app.hyuabot.backend.service

import app.hyuabot.backend.domain.admin.Notice
import app.hyuabot.backend.domain.admin.NoticeCategory
import app.hyuabot.backend.repository.admin.NoticeCategoryRepository
import app.hyuabot.backend.repository.admin.NoticeRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class NoticeService(
    private val noticeRepository: NoticeRepository,
    private val noticeCategoryRepository: NoticeCategoryRepository,
) {
    @Transactional
    fun getNoticeCategoryList(): List<NoticeCategory> = noticeCategoryRepository.findAll()

    @Transactional
    fun postNoticeCategory(categoryID: Int, name: String) {
        if (noticeCategoryRepository.existsByName(name)) {
            throw Exception(
                "DUPLICATED"
            )
        } else {
            noticeCategoryRepository.save(
                NoticeCategory(id = categoryID, name = name)
            )
        }
    }

    @Transactional
    fun deleteNoticeCategory(categoryID: Int) {
        if (noticeRepository.getNoticesByCategoryID(categoryID).isNotEmpty()) {
            throw DataIntegrityViolationException("NOTICES_EXIST_IN_CATEGORY")
        } else if (noticeRepository.existsById(categoryID)) {
            noticeCategoryRepository.deleteById(categoryID)
        } else {
            throw Exception("NOTICE_CATEGORY_NOT_FOUND")
        }
    }

    @Transactional
    fun getNoticeList(): List<Notice> = noticeRepository.findAll()

    @Transactional
    fun getNoticeList(categoryId: Int): List<Notice> = noticeRepository.getNoticesByCategoryID(categoryId)

    @Transactional
    fun postNotice(noticeID: Int, categoryId: Int, title: String, url: String, expiredAt: LocalDateTime, createdBy: String) {
        noticeRepository.save(
            Notice(
                id = noticeID,
                categoryID = categoryId,
                title = title,
                url = url,
                expiredAt = expiredAt,
                createdBy = createdBy,
            )
        )
    }

    @Transactional
    fun patchNotice(noticeID: Int, categoryID: Int, title: String, url: String, expiredAt: LocalDateTime) {
        if (noticeRepository.existsById(noticeID)) {
            noticeRepository.findById(noticeID).map {
                it.categoryID = categoryID
                it.title = title
                it.url = url
                it.expiredAt = expiredAt
                noticeRepository.save(it)
            }
        } else {
            throw Exception("NOTICE_NOT_FOUND")
        }
    }

    @Transactional
    fun deleteNotice(noticeID: Int) {
        if (noticeRepository.existsById(noticeID)) {
            noticeRepository.deleteById(noticeID)
        } else {
            throw Exception("NOTICE_NOT_FOUND")
        }
    }
}