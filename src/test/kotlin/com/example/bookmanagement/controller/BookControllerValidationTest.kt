package com.example.bookmanagement.controller

import com.example.bookmanagement.domain.PublicationStatus
import com.example.bookmanagement.dto.AuthorIdRequest
import com.example.bookmanagement.dto.CreateBookRequest
import com.example.bookmanagement.service.BookService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * 書籍APIのバリデーション境界値テスト
 */
@WebMvcTest(BookController::class)
class BookControllerValidationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var bookService: BookService

    // ============== タイトル境界値テスト ==============

    @Test
    fun `create should fail when title is blank`() {
        val request = CreateBookRequest(
            title = "",
            price = 1000,
            publicationStatus = PublicationStatus.UNPUBLISHED,
            authors = listOf(AuthorIdRequest(1L))
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `create should fail when title exceeds 255 characters`() {
        val longTitle = "a".repeat(256)
        val request = CreateBookRequest(
            title = longTitle,
            price = 1000,
            publicationStatus = PublicationStatus.UNPUBLISHED,
            authors = listOf(AuthorIdRequest(1L))
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `create should succeed when title is exactly 255 characters`() {
        val exactTitle = "a".repeat(255)
        val request = CreateBookRequest(
            title = exactTitle,
            price = 1000,
            publicationStatus = PublicationStatus.UNPUBLISHED,
            authors = listOf(AuthorIdRequest(1L))
        )

        // Serviceは例外を投げるが、バリデーションは通る
        whenever(bookService.create(any())).thenThrow(RuntimeException())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }

    // ============== 価格境界値テスト ==============

    @Test
    fun `create should fail when price is negative`() {
        val request = CreateBookRequest(
            title = "テスト書籍",
            price = -1,
            publicationStatus = PublicationStatus.UNPUBLISHED,
            authors = listOf(AuthorIdRequest(1L))
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `create should succeed when price is zero`() {
        val request = CreateBookRequest(
            title = "テスト書籍",
            price = 0,
            publicationStatus = PublicationStatus.UNPUBLISHED,
            authors = listOf(AuthorIdRequest(1L))
        )

        whenever(bookService.create(any())).thenThrow(RuntimeException())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }

    // ============== 著者境界値テスト ==============

    @Test
    fun `create should fail when authors is empty`() {
        val request = CreateBookRequest(
            title = "テスト書籍",
            price = 1000,
            publicationStatus = PublicationStatus.UNPUBLISHED,
            authors = emptyList()
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `create should succeed with multiple authors`() {
        val request = CreateBookRequest(
            title = "テスト書籍",
            price = 1000,
            publicationStatus = PublicationStatus.UNPUBLISHED,
            authors = listOf(AuthorIdRequest(1L), AuthorIdRequest(2L))
        )

        whenever(bookService.create(any())).thenThrow(RuntimeException())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }
}
