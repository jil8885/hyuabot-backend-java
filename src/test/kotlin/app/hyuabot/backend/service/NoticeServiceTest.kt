package app.hyuabot.backend.service

import app.hyuabot.backend.domain.admin.Notice
import app.hyuabot.backend.domain.admin.NoticeCategory
import app.hyuabot.backend.domain.admin.User
import app.hyuabot.backend.dto.request.SignUpRequest
import app.hyuabot.backend.repository.admin.NoticeCategoryRepository
import app.hyuabot.backend.repository.admin.NoticeRepository
import app.hyuabot.backend.repository.admin.UserRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class NoticeServiceTest {
    @Autowired private lateinit var noticeService: NoticeService
    @Autowired private lateinit var noticeCategoryRepository: NoticeCategoryRepository
    @Autowired private lateinit var noticeRepository: NoticeRepository
    @Autowired private lateinit var userRepository: UserRepository

    @BeforeEach
    fun init() {
        clean()
        userRepository.saveAll(listOf(
            User.registerUser(SignUpRequest("TEST_USER", "", "", "", "")),
        ))
        noticeCategoryRepository.saveAll(listOf(
            NoticeCategory(id = 999, name = "TEST_CATEGORY_1"),
            NoticeCategory(id = 1000, name = "TEST_CATEGORY_2"),
            NoticeCategory(id = 1001, name = "TEST_CATEGORY_3"),
        ))
        noticeRepository.saveAll(listOf(
            Notice(
                id = 1,
                categoryID = 999,
                title = "TEST_TITLE_1",
                url = "TEST_URL_1",
                expiredAt = LocalDateTime.now().plusMonths(1),
                createdBy = "TEST_USER",
            ),
            Notice(
                id = 2,
                categoryID = 1000,
                title = "TEST_TITLE_2",
                url = "TEST_URL_2",
                expiredAt = LocalDateTime.now().plusMonths(1),
                createdBy = "TEST_USER",
            ),
            Notice(
                id = 3,
                categoryID = 1000,
                title = "TEST_TITLE_3",
                url = "TEST_URL_3",
                expiredAt = LocalDateTime.now().plusMonths(1),
                createdBy = "TEST_USER",
            ),
        ))
    }

    @AfterEach
    fun clean() {
        noticeRepository.deleteAllByIdInBatch(listOf(1, 2, 3))
        noticeCategoryRepository.deleteAllByIdInBatch(listOf(999, 1000, 1001))
        userRepository.deleteAllByIdInBatch(listOf("TEST_USER"))
    }

    @Test
    @DisplayName("GET_NOTICE_CATEGORY_LIST")
    fun getNoticeCategoryList() {
        assertTrue(noticeService.getNoticeCategoryList().isNotEmpty())
    }

    @Test
    @DisplayName("POST_NOTICE_CATEGORY_SUCCESS")
    fun postNoticeCategorySuccess() {
        noticeService.postNoticeCategory(4, "TEST_CATEGORY_4")
        assertTrue(noticeCategoryRepository.existsByName("TEST_CATEGORY_4"))
        noticeCategoryRepository.deleteById(4)
    }

    @Test
    @DisplayName("POST_NOTICE_CATEGORY_DUPLICATED")
    fun postNoticeCategoryDuplicated() {
        assertTrue(assertThrows<Exception> {
            noticeService.postNoticeCategory(4, "TEST_CATEGORY_1")
        }.message == "DUPLICATED")
    }

    @Test
    @DisplayName("DELETE_NOTICE_CATEGORY_SUCCESS")
    fun deleteNoticeCategorySuccess() {
        noticeRepository.deleteAllByCategoryID(1)
        noticeService.deleteNoticeCategory(1)
        assertTrue(noticeCategoryRepository.findById(1).isEmpty)
    }

    @Test
    @DisplayName("DELETE_NOTICE_CATEGORY_NOT_FOUND")
    fun deleteNoticeCategoryNotFound() {
        assertTrue(assertThrows<Exception> {
            noticeService.deleteNoticeCategory(4)
        }.message == "NOTICE_CATEGORY_NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_NOTICE_CATEGORY_NOTICES_EXIST")
    fun deleteNoticeCategoryNoticesExist() {
        assertTrue(assertThrows<Exception> {
            noticeService.deleteNoticeCategory(999)
        }.message == "NOTICES_EXIST_IN_CATEGORY")
    }

    @Test
    @DisplayName("GET_NOTICE_LIST")
    fun getNoticeList() {
        assertTrue(noticeService.getNoticeList().isNotEmpty())
    }

    @Test
    @DisplayName("GET_NOTICE_LIST_BY_CATEGORY")
    fun getNoticeListByCategory() {
        assertTrue(noticeService.getNoticeList(999).isNotEmpty())
    }

    @Test
    @DisplayName("POST_NOTICE_SUCCESS")
    fun postNoticeSuccess() {
        noticeService.postNotice(4, 999, "TEST_TITLE_4", "TEST_URL_4", LocalDateTime.now().plusMonths(1), "TEST_USER")
        assertTrue(noticeRepository.existsByTitle("TEST_TITLE_4"))
        noticeRepository.deleteById(4)
    }

    @Test
    @DisplayName("PATCH_NOTICE_SUCCESS")
    fun patchNoticeSuccess() {
        noticeService.patchNotice(1, 999, "TEST_TITLE_4", "TEST_URL_4", LocalDateTime.now().plusMonths(1))
        assertTrue(noticeRepository.existsByTitle("TEST_TITLE_4"))
    }

    @Test
    @DisplayName("PATCH_NOTICE_NOT_FOUND")
    fun patchNoticeNotFound() {
        assertTrue(assertThrows<Exception> {
            noticeService.patchNotice(4, 999, "TEST_TITLE_4", "TEST_URL_4", LocalDateTime.now().plusMonths(1))
        }.message == "NOTICE_NOT_FOUND")
    }

    @Test
    @DisplayName("DELETE_NOTICE_SUCCESS")
    fun deleteNoticeSuccess() {
        noticeService.deleteNotice(1)
        assertTrue(noticeRepository.findById(1).isEmpty)
    }

    @Test
    @DisplayName("DELETE_NOTICE_NOT_FOUND")
    fun deleteNoticeNotFound() {
        assertTrue(assertThrows<Exception> {
            noticeService.deleteNotice(4)
        }.message == "NOTICE_NOT_FOUND")
    }
}