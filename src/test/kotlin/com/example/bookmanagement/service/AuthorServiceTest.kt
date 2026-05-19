package com.example.bookmanagement.service

import com.example.bookmanagement.domain.Author
import com.example.bookmanagement.dto.CreateAuthorRequest
import com.example.bookmanagement.dto.UpdateAuthorRequest
import com.example.bookmanagement.exception.AuthorNotFoundException
import com.example.bookmanagement.repository.AuthorRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.time.LocalDate
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

/**
 * 著者サービスのユニットテスト
 */
class AuthorServiceTest {

    @Mock
    private lateinit var authorRepository: AuthorRepository

    private lateinit var authorService: AuthorService

    private lateinit var testAuthor: Author

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        authorService = AuthorService(authorRepository)
        
        testAuthor = Author(
            id = 1L,
            name = "テスト著者",
            birthDate = LocalDate.of(1980, 1, 1),
            createdAt = LocalDateTime.now()
        )
    }

    @Test
    fun `findAll should return all authors`() {
        // 準備
        val authors = listOf(testAuthor, testAuthor.copy(id = 2L, name = "著者2"))
        whenever(authorRepository.findAll()).thenReturn(authors)

        // 実行
        val result = authorService.findAll()

        // 検証
        assertEquals(2, result.size)
        assertEquals("テスト著者", result[0].name)
    }

    @Test
    fun `findById should return author when exists`() {
        // 準備
        whenever(authorRepository.findById(1L)).thenReturn(testAuthor)

        // 実行
        val result = authorService.findById(1L)

        // 検証
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("テスト著者", result.name)
    }

    @Test
    fun `findById should throw exception when not found`() {
        // 準備
        whenever(authorRepository.findById(999L)).thenReturn(null)

        // 実行 & 検証
        assertThrows<AuthorNotFoundException> {
            authorService.findById(999L)
        }
    }

    @Test
    fun `create should create and return author`() {
        // 準備
        val request = CreateAuthorRequest(
            name = "新規著者",
            birthDate = LocalDate.of(1990, 5, 15)
        )

        val createdAuthor = testAuthor.copy(name = "新規著者", birthDate = LocalDate.of(1990, 5, 15))
        whenever(authorRepository.create(any())).thenReturn(createdAuthor)

        // 実行
        val result = authorService.create(request)

        // 検証
        assertNotNull(result)
        assertEquals("新規著者", result.name)
        verify(authorRepository).create(any())
    }

    @Test
    fun `update should update and return author`() {
        // 準備
        val request = UpdateAuthorRequest(
            name = "更新済み著者",
            birthDate = LocalDate.of(1985, 3, 20)
        )

        val updatedAuthor = testAuthor.copy(name = "更新済み著者", birthDate = LocalDate.of(1985, 3, 20))
        whenever(authorRepository.findById(1L)).thenReturn(testAuthor)
        whenever(authorRepository.update(any(), any())).thenReturn(updatedAuthor)

        // 実行
        val result = authorService.update(1L, request)

        // 検証
        assertNotNull(result)
        assertEquals("更新済み著者", result.name)
        assertEquals(LocalDate.of(1985, 3, 20), result.birthDate)
    }

    @Test
    fun `update should throw exception when author not found`() {
        // 準備
        whenever(authorRepository.findById(999L)).thenReturn(null)

        val request = UpdateAuthorRequest(
            name = "更新済み著者",
            birthDate = LocalDate.of(1985, 3, 20)
        )

        // 実行 & 検証
        assertThrows<AuthorNotFoundException> {
            authorService.update(999L, request)
        }
    }

    @Test
    fun `delete should call repository delete`() {
        // 準備
        whenever(authorRepository.findById(1L)).thenReturn(testAuthor)

        // 実行
        authorService.delete(1L)

        // 検証
        verify(authorRepository).delete(1L)
    }

    @Test
    fun `delete should throw exception when author not found`() {
        // 準備
        whenever(authorRepository.findById(999L)).thenReturn(null)

        // 実行 & 検証
        assertThrows<AuthorNotFoundException> {
            authorService.delete(999L)
        }
    }
}
