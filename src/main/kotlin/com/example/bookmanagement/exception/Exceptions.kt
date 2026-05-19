package com.example.bookmanagement.exception

/**
 * 書籍が見つからない場合にスローされる例外
 */
class BookNotFoundException(message: String) : RuntimeException(message)

/**
 * 著者が見つからない場合にスローされる例外
 */
class AuthorNotFoundException(message: String) : RuntimeException(message)

/**
 * 無効な操作を表す例外
 */
class InvalidOperationException(message: String) : RuntimeException(message)
