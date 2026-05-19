package com.example.bookmanagement.dto

import com.example.bookmanagement.domain.Author
import com.example.bookmanagement.domain.Book
import com.example.bookmanagement.domain.PublicationStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.LocalDateTime

// ============== 書籍リクエストDTO ==============

/**
 * 書籍作成リクエスト
 */
data class CreateBookRequest(
    @field:NotBlank(message = "タイトルは必須です")
    @field:Size(max = 255, message = "タイトルは255文字以内にしてください")
    val title: String,

    @field:Min(value = 0, message = "価格は0以上である必要があります")
    val price: Int,

    @field:NotEmpty(message = "最低1人の著者が必要です")
    @field:Valid
    val authors: List<AuthorIdRequest>,

    val publicationStatus: PublicationStatus = PublicationStatus.UNPUBLISHED
)

/**
 * 書籍更新リクエスト
 */
data class UpdateBookRequest(
    @field:NotBlank(message = "タイトルは必須です")
    @field:Size(max = 255, message = "タイトルは255文字以内にしてください")
    val title: String,

    @field:Min(value = 0, message = "価格は0以上である必要があります")
    val price: Int,

    @field:NotEmpty(message = "最低1人の著者が必要です")
    @field:Valid
    val authors: List<AuthorIdRequest>,

    val publicationStatus: PublicationStatus
)

// ============== 著者リクエストDTO ==============

/**
 * 著者作成リクエスト
 */
data class CreateAuthorRequest(
    @field:NotBlank(message = "名前は必須です")
    @field:Size(max = 255, message = "名前は255文字以内にしてください")
    val name: String,

    @field:NotNull(message = "生年月日は必須です")
    @field:PastOrPresent(message = "生年月日は現在日以前である必要があります")
    val birthDate: LocalDate
)

/**
 * 著者更新リクエスト
 */
data class UpdateAuthorRequest(
    @field:NotBlank(message = "名前は必須です")
    @field:Size(max = 255, message = "名前は255文字以内にしてください")
    val name: String,

    @field:NotNull(message = "生年月日は必須です")
    @field:PastOrPresent(message = "生年月日は現在日以前である必要があります")
    val birthDate: LocalDate
)

/**
 * 著者IDのみを持つ簡易リクエスト
 */
data class AuthorIdRequest(
    @field:NotNull(message = "著者IDは必須です")
    val id: Long
)

// ============== レスポンスDTO ==============

/**
 * 書籍レスポンス
 */
data class BookResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val publicationStatus: PublicationStatus,
    val authors: List<AuthorResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(book: Book): BookResponse {
            return BookResponse(
                id = book.id!!,
                title = book.title,
                price = book.price,
                publicationStatus = book.publicationStatus,
                authors = book.authors.map { AuthorResponse.from(it) },
                createdAt = book.createdAt!!,
                updatedAt = book.updatedAt!!
            )
        }
    }
}

/**
 * 著者レスポンス
 */
data class AuthorResponse(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(author: Author): AuthorResponse {
            return AuthorResponse(
                id = author.id!!,
                name = author.name,
                birthDate = author.birthDate,
                createdAt = author.createdAt!!
            )
        }
    }
}

/**
 * エラーレスポンス
 */
data class ErrorResponse(
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
