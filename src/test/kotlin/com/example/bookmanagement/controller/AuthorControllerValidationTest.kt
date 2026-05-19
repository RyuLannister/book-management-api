package com.example.bookmanagement.controller

import com.example.bookmanagement.dto.CreateAuthorRequest
import com.example.bookmanagement.service.AuthorService
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
import java.time.LocalDate

/**
 * 著者APIのバリデーション境界値テスト
 */
@WebMvcTest(AuthorController::class)
class AuthorControllerValidationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var authorService: AuthorService

    // ============== 名前境界値テスト ==============

    @Test
    fun `createAuthor should fail when name is blank`() {
        val request = CreateAuthorRequest(
            name = "",
            birthDate = LocalDate.of(1990, 1, 1)
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `createAuthor should fail when name exceeds 255 characters`() {
        val longName = "a".repeat(256)
        val request = CreateAuthorRequest(
            name = longName,
            birthDate = LocalDate.of(1990, 1, 1)
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `createAuthor should succeed when name is exactly 255 characters`() {
        val exactName = "a".repeat(255)
        val request = CreateAuthorRequest(
            name = exactName,
            birthDate = LocalDate.of(1990, 1, 1)
        )

        whenever(authorService.create(any())).thenThrow(RuntimeException())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }

    // ============== 生年月日境界値テスト ==============

    @Test
    fun `createAuthor should fail when birthDate is in the future`() {
        val futureDate = LocalDate.now().plusDays(1)
        val request = CreateAuthorRequest(
            name = "テスト著者",
            birthDate = futureDate
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `createAuthor should succeed when birthDate is today`() {
        val today = LocalDate.now()
        val request = CreateAuthorRequest(
            name = "テスト著者",
            birthDate = today
        )

        whenever(authorService.create(any())).thenThrow(RuntimeException())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }

    @Test
    fun `createAuthor should succeed when birthDate is in the past`() {
        val pastDate = LocalDate.of(1900, 1, 1)
        val request = CreateAuthorRequest(
            name = "テスト著者",
            birthDate = pastDate
        )

        whenever(authorService.create(any())).thenThrow(RuntimeException())

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }
}
