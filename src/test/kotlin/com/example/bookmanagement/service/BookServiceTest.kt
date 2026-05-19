package com.example.bookmanagement.service

import com.example.bookmanagement.domain.Author
import com.example.bookmanagement.domain.Book
import com.example.bookmanagement.domain.PublicationStatus
import com.example.bookmanagement.dto.AuthorIdRequest
import com.example.bookmanagement.dto.CreateBookRequest
import com.example.bookmanagement.dto.UpdateBookRequest
import com.example.bookmanagement.exception.BookNotFoundException
import com.example.bookmanagement.exception.InvalidOperationException
import com.example.bookmanagement.repository.AuthorRepository
import com.example.bookmanagement.repository.BookRepository
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
 * 書籍サービスのユニットテスト
 */
class BookServiceTest {

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var authorRepository: AuthorRepository

    private lateinit var bookService: BookService

    private lateinit var testAuthor: Author
    private lateinit var testBook: Book

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        bookService = BookService(bookRepository, authorRepository)
        
        testAuthor = Author(
            id = 1L,
            name = "テスト著者",
            birthDate = LocalDate.of(1980, 1, 1),
            createdAt = LocalDateTime.now()
        )
        
        testBook = Book(
            id = 1L,
            title = "テスト書籍",
            price = 2000,
            publicationStatus = PublicationStatus.UNPUBLISHED,
            authors = listOf(testAuthor),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
    }

    @Test
    fun `create should create and return book with authors`() {
        // 準備
        val request = CreateBookRequest(
            title = "テスト書籍",
            price = 2000,
            publicationStatus = PublicationStatus.UNPUBLISHED,
            authors = listOf(AuthorIdRequest(1L))
        )

        whenever(authorRepository.existsByIds(listOf(1L))).thenReturn(true)
        whenever(authorRepository.findByIds(listOf(1L))).thenReturn(listOf(testAuthor))
        whenever(bookRepository.create(any())).thenReturn(testBook)

        // 実行
        val result = bookService.create(request)

        // 検証
        assertNotNull(result)
        assertEquals("テスト書籍", result.title)
        assertEquals(2000, result.price)
        assertEquals(PublicationStatus.UNPUBLISHED, result.publicationStatus)
        verify(bookRepository).create(any())
    }

    @Test
    fun `findById should return book when exists`() {
        // 準備
        whenever(bookRepository.findById(1L)).thenReturn(testBook)

        // 実行
        val result = bookService.findById(1L)

        // 検証
        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("テスト書籍", result.title)
    }

    @Test
    fun `findById should throw exception when not found`() {
        // 準備
        whenever(bookRepository.findById(999L)).thenReturn(null)

        // 実行 & 検証
        assertThrows<BookNotFoundException> {
            bookService.findById(999L)
        }
    }

    @Test
    fun `findAll should return all books`() {
        // 準備
        val books = listOf(testBook, testBook.copy(id = 2L, title = "書籍2", price = 3000))
        whenever(bookRepository.findAll()).thenReturn(books)

        // 実行
        val result = bookService.findAll()

        // 検証
        assertEquals(2, result.size)
    }

    @Test
    fun `findByAuthorId should return books for author`() {
        // 準備
        whenever(authorRepository.findById(1L)).thenReturn(testAuthor)
        whenever(bookRepository.findByAuthorId(1L)).thenReturn(listOf(testBook))

        // 実行
        val result = bookService.findByAuthorId(1L)

        // 検証
        assertEquals(1, result.size)
        assertEquals("テスト書籍", result[0].title)
    }

    @Test
    fun `findByAuthorId should throw exception when author not found`() {
        // 準備
        whenever(authorRepository.findById(999L)).thenReturn(null)

        // 実行 & 検証
        assertThrows<BookNotFoundException> {
            bookService.findByAuthorId(999L)
        }
    }

    @Test
    fun `update should update and return book`() {
        // 準備
        val request = UpdateBookRequest(
            title = "更新書籍",
            price = 3000,
            publicationStatus = PublicationStatus.PUBLISHED,
            authors = listOf(AuthorIdRequest(1L))
        )

        val updatedBook = testBook.copy(title = "更新書籍", price = 3000, publicationStatus = PublicationStatus.PUBLISHED)

        whenever(bookRepository.findById(1L)).thenReturn(testBook)
        whenever(authorRepository.existsByIds(listOf(1L))).thenReturn(true)
        whenever(authorRepository.findByIds(listOf(1L))).thenReturn(listOf(testAuthor))
        whenever(bookRepository.update(any(), any())).thenReturn(updatedBook)

        // 実行
        val result = bookService.update(1L, request)

        // 検証
        assertNotNull(result)
        assertEquals("更新書籍", result.title)
        assertEquals(3000, result.price)
    }

    @Test
    fun `update should throw exception when book not found`() {
        // 準備
        whenever(bookRepository.findById(999L)).thenReturn(null)

        val request = UpdateBookRequest(
            title = "更新書籍",
            price = 3000,
            publicationStatus = PublicationStatus.PUBLISHED,
            authors = listOf(AuthorIdRequest(1L))
        )

        // 実行 & 検証
        assertThrows<BookNotFoundException> {
            bookService.update(999L, request)
        }
    }

    @Test
    fun `update should throw exception when changing published to unpublished`() {
        // 準備
        val publishedBook = testBook.copy(publicationStatus = PublicationStatus.PUBLISHED)
        whenever(bookRepository.findById(1L)).thenReturn(publishedBook)

        val request = UpdateBookRequest(
            title = "更新書籍",
            price = 3000,
            publicationStatus = PublicationStatus.UNPUBLISHED,
            authors = listOf(AuthorIdRequest(1L))
        )

        // 実行 & 検証
        assertThrows<InvalidOperationException> {
            bookService.update(1L, request)
        }
    }

    @Test
    fun `delete should call repository delete`() {
        // 準備
        whenever(bookRepository.findById(1L)).thenReturn(testBook)

        // 実行
        bookService.delete(1L)

        // 検証
        verify(bookRepository).delete(1L)
    }
}
