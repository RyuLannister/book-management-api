package com.example.bookmanagement.controller

import com.example.bookmanagement.dto.AuthorResponse
import com.example.bookmanagement.dto.CreateAuthorRequest
import com.example.bookmanagement.dto.UpdateAuthorRequest
import com.example.bookmanagement.service.AuthorService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 著者APIコントローラー
 * 著者のCRUD操作を提供するREST APIエンドポイント
 */
@RestController
@RequestMapping("/api/authors")
class AuthorController(private val authorService: AuthorService) {

    /**
     * 全著者を取得
     */
    @GetMapping
    fun getAllAuthors(): ResponseEntity<List<AuthorResponse>> {
        val authors = authorService.findAll()
        return ResponseEntity.ok(authors.map { AuthorResponse.from(it) })
    }

    /**
     * 著者IDで取得
     */
    @GetMapping("/{id}")
    fun getAuthorById(@PathVariable id: Long): ResponseEntity<AuthorResponse> {
        val author = authorService.findById(id)
        return ResponseEntity.ok(AuthorResponse.from(author))
    }

    /**
     * 著者を作成
     */
    @PostMapping
    fun createAuthor(@RequestBody @Valid request: CreateAuthorRequest): ResponseEntity<AuthorResponse> {
        val author = authorService.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthorResponse.from(author))
    }

    /**
     * 著者を更新
     */
    @PutMapping("/{id}")
    fun updateAuthor(
        @PathVariable id: Long,
        @RequestBody @Valid request: UpdateAuthorRequest
    ): ResponseEntity<AuthorResponse> {
        val author = authorService.update(id, request)
        return ResponseEntity.ok(AuthorResponse.from(author))
    }

    /**
     * 著者を削除
     */
    @DeleteMapping("/{id}")
    fun deleteAuthor(@PathVariable id: Long): ResponseEntity<Unit> {
        authorService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
