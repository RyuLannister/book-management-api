package com.example.bookmanagement.service

import com.example.bookmanagement.domain.Author
import com.example.bookmanagement.domain.Book
import com.example.bookmanagement.domain.PublicationStatus
import com.example.bookmanagement.dto.CreateBookRequest
import com.example.bookmanagement.dto.UpdateBookRequest
import com.example.bookmanagement.exception.BookNotFoundException
import com.example.bookmanagement.exception.InvalidOperationException
import com.example.bookmanagement.repository.AuthorRepository
import com.example.bookmanagement.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 書籍サービス
 * 書籍のビジネスロジックを処理する
 */
@Service
class BookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
) {

    /**
     * 全書籍を取得
     */
    @Transactional(readOnly = true)
    fun findAll(): List<Book> {
        return bookRepository.findAll()
    }

    /**
     * 書籍IDで取得
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): Book {
        return bookRepository.findById(id)
            ?: throw BookNotFoundException("指定された書籍が存在しません: id=$id")
    }

    /**
     * 著者IDに紐づく書籍を取得
     */
    @Transactional(readOnly = true)
    fun findByAuthorId(authorId: Long): List<Book> {
        // 著者が存在するか確認
        authorRepository.findById(authorId)
            ?: throw BookNotFoundException("指定された著者が存在しません: id=$authorId")

        return bookRepository.findByAuthorId(authorId)
    }

    /**
     * 書籍を作成
     */
    @Transactional
    fun create(request: CreateBookRequest): Book {
        // すべての著者が存在することを確認
        val authorIds = request.authors.map { it.id }
        validateAuthorsExist(authorIds)

        val book = Book(
            title = request.title,
            price = request.price,
            publicationStatus = request.publicationStatus,
            authors = authorRepository.findByIds(authorIds)
        )

        return bookRepository.create(book)
    }

    /**
     * 書籍を更新
     */
    @Transactional
    fun update(id: Long, request: UpdateBookRequest): Book {
        val existingBook = bookRepository.findById(id)
            ?: throw BookNotFoundException("指定された書籍が存在しません: id=$id")

        // 出版済みステータスを未出版に変更しようとしている場合はエラー
        if (existingBook.publicationStatus == PublicationStatus.PUBLISHED &&
            request.publicationStatus == PublicationStatus.UNPUBLISHED) {
            throw InvalidOperationException("出版済みの書籍は未出版に変更できません")
        }

        // すべての著者が存在することを確認
        val authorIds = request.authors.map { it.id }
        validateAuthorsExist(authorIds)

        val updatedBook = existingBook.copy(
            title = request.title,
            price = request.price,
            publicationStatus = request.publicationStatus,
            authors = authorRepository.findByIds(authorIds)
        )

        return bookRepository.update(id, updatedBook)
    }

    /**
     * 書籍を削除
     */
    @Transactional
    fun delete(id: Long) {
        if (bookRepository.findById(id) == null) {
            throw BookNotFoundException("指定された書籍が存在しません: id=$id")
        }
        bookRepository.delete(id)
    }

    private fun validateAuthorsExist(authorIds: List<Long>) {
        if (!authorRepository.existsByIds(authorIds)) {
            throw InvalidOperationException("指定された著者が一部存在しません: ids=$authorIds")
        }
    }
}
