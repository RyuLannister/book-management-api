package com.example.bookmanagement.exception

import com.example.bookmanagement.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * グローバル例外ハンドラー
 * アプリケーション全体で発生する例外を処理する
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * 書籍が見つからない場合の例外を処理
     */
    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookNotFoundException(ex: BookNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            error = "Not Found",
            message = ex.message ?: "書籍が見つかりません"
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    /**
     * 著者が見つからない場合の例外を処理
     */
    @ExceptionHandler(AuthorNotFoundException::class)
    fun handleAuthorNotFoundException(ex: AuthorNotFoundException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            error = "Not Found",
            message = ex.message ?: "著者が見つかりません"
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    /**
     * 無効な操作場合の例外を処理
     */
    @ExceptionHandler(InvalidOperationException::class)
    fun handleInvalidOperationException(ex: InvalidOperationException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            error = "Bad Request",
            message = ex.message ?: "無効な操作です"
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * バリデーションエラーの例外を処理
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        val errorResponse = ErrorResponse(
            error = "Validation Error",
            message = message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    /**
     * その他の例外を処理
     */
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        ex.printStackTrace() // DEBUG: 一時的にスタックトレースを出力
        val errorResponse = ErrorResponse(
            error = "Internal Server Error",
            message = ex.message ?: "予期しないエラーが発生しました"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}
