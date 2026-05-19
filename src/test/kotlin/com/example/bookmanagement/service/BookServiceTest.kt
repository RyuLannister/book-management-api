package com.example.bookmanagement.service

import com.example.bookmanagement.domain.Author
import com.example.bookmanagement.domain.Book
import com.example.bookmanagement.domain.PublicationStatus
import com.example.bookmanagement.dto.CreateBookRequest
import com.example.bookmanagement.dto.AuthorIdRequest
import com.example.bookmanagement.exception.BookNotFoundException
import com.example.bookmanagement.exception.InvalidOperationException
import com.example.bookmanagement.repository.AuthorRepository
import com.example.bookmanagement.repository.BookRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * 書籍サービスのユニットテスト
 */
class BookServiceTest {

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var authorRepository: AuthorRepository

    private lateinit var bookService: BookService

    private val testAuthor = Author(
        id = 1L,
        name = "テスト著者",
        birthDate = LocalDate.of(1990, 1, 1),
        createdAt = LocalDateTime.now()
    )

    private val testBook = Book(
        id = 1L,
        title = "テスト書籍",
        price = 1500,
        publicationStatus = PublicationStatus.UNPUBLISHED,
        authors = listOf(testAuthor),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @BeforeEach
    fun setUp() {
        bookService = BookService(bookRepository, authorRepository)
    }

    @Test
    fun `全書籍を取得する`() {
        `when`(bookRepository.findAll()).thenReturn(listOf(testBook))

        val result = bookService.findAll()

        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals("テスト書籍", result[0].title)
        verify(bookRepository).findAll()
    }

    @Test
    fun `書籍IDで取得成功`() {
        `when`(bookRepository.findById(1L)).thenReturn(testBook)

        val result = bookService.findById(1L)

        assertNotNull(result)
        assertEquals("テスト書籍", result.title)
        verify(bookRepository).findById(1L)
    }

    @Test
    fun `存在しない書籍IDで取得すると例外をスロー`() {
        `when`(bookRepository.findById(999L)).thenReturn(null)

        assertThrows<BookNotFoundException> {
            bookService.findById(999L)
        }
    }

    @Test
    fun `書籍を作成成功`() {
        val request = CreateBookRequest(
            title = "新規書籍",
            price = 2000,
            authors = listOf(AuthorIdRequest(1L)),
            publicationStatus = PublicationStatus.UNPUBLISHED
        )

        `when`(authorRepository.existsByIds(listOf(1L))).thenReturn(true)
        `when`(authorRepository.findByIds(listOf(1L))).thenReturn(listOf(testAuthor))
        `when`(bookRepository.create(any(Book::class.java))).thenReturn(testBook.copy(title = "新規書籍"))

        val result = bookService.create(request)

        assertNotNull(result)
        assertEquals("新規書籍", result.title)
        verify(authorRepository).existsByIds(listOf(1L))
        verify(bookRepository).create(any(Book::class.java))
    }

    @Test
    fun `書籍作成時に存在しない著者がいると例外をスロー`() {
        val request = CreateBookRequest(
            title = "新規書籍",
            price = 2000,
            authors = listOf(AuthorIdRequest(999L)),
            publicationStatus = PublicationStatus.UNPUBLISHED
        )

        `when`(authorRepository.existsByIds(listOf(999L))).thenReturn(false)

        assertThrows<InvalidOperationException> {
            bookService.create(request)
        }
    }

    @Test
    fun `出版済みの書籍を未出版に変更すると例外をスロー`() {
        val publishedBook = testBook.copy(publicationStatus = PublicationStatus.PUBLISHED)
        `when`(bookRepository.findById(1L)).thenReturn(publishedBook)

        val request = com.example.bookmanagement.dto.UpdateBookRequest(
            title = "テスト書籍",
            price = 1500,
            authors = listOf(AuthorIdRequest(1L)),
            publicationStatus = PublicationStatus.UNPUBLISHED
        )

        assertThrows<InvalidOperationException> {
            bookService.update(1L, request)
        }
    }

    @Test
    fun `著者に紐づく書籍を取得`() {
        `when`(authorRepository.findById(1L)).thenReturn(testAuthor)
        `when`(bookRepository.findByAuthorId(1L)).thenReturn(listOf(testBook))

        val result = bookService.findByAuthorId(1L)

        assertNotNull(result)
        assertEquals(1, result.size)
        verify(authorRepository).findById(1L)
        verify(bookRepository).findByAuthorId(1L)
    }

    @Test
    fun `存在しない著者に紐づく書籍を取得しようとすると例外をスロー`() {
        `when`(authorRepository.findById(999L)).thenReturn(null)

        assertThrows<BookNotFoundException> {
            bookService.findByAuthorId(999L)
        }
    }

    @Test
    fun `書籍を削除成功`() {
        `when`(bookRepository.findById(1L)).thenReturn(testBook)
        doNothing().`when`(bookRepository).delete(1L)

        bookService.delete(1L)

        verify(bookRepository).delete(1L)
    }

    @Test
    fun `存在しない書籍を削除しようとすると例外をスロー`() {
        `when`(bookRepository.findById(999L)).thenReturn(null)

        assertThrows<BookNotFoundException> {
            bookService.delete(999L)
        }
    }
}
