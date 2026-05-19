package com.example.bookmanagement.controller

import com.example.bookmanagement.dto.BookResponse
import com.example.bookmanagement.dto.CreateBookRequest
import com.example.bookmanagement.dto.ErrorResponse
import com.example.bookmanagement.dto.UpdateBookRequest
import com.example.bookmanagement.service.BookService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 書籍APIコントローラー
 * 書籍のCRUD操作を提供するREST APIエンドポイント
 */
@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    /**
     * 全書籍を取得
     */
    @GetMapping
    fun getAllBooks(): ResponseEntity<List<BookResponse>> {
        val books = bookService.findAll()
        return ResponseEntity.ok(books.map { BookResponse.from(it) })
    }

    /**
     * 書籍IDで取得
     */
    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Long): ResponseEntity<BookResponse> {
        val book = bookService.findById(id)
        return ResponseEntity.ok(BookResponse.from(book))
    }

    /**
     *  著者に紐づく書籍を取得
     */
    @GetMapping("/author/{authorId}")
    fun getBooksByAuthorId(@PathVariable authorId: Long): ResponseEntity<List<BookResponse>> {
        val books = bookService.findByAuthorId(authorId)
        return ResponseEntity.ok(books.map { BookResponse.from(it) })
    }

    /**
     * 書籍を作成
     */
    @PostMapping
    fun createBook(@RequestBody @Valid request: CreateBookRequest): ResponseEntity<BookResponse> {
        val book = bookService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(BookResponse.from(book))
    }

    /**
     * 書籍を更新
     */
    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: Long,
        @RequestBody @Valid request: UpdateBookRequest
    ): ResponseEntity<BookResponse> {
        val book = bookService.update(id, request)
        return ResponseEntity.ok(BookResponse.from(book))
    }

    /**
     * 書籍を削除
     */
    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: Long): ResponseEntity<Unit> {
        bookService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
