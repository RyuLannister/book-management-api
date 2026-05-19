package com.example.bookmanagement.service

import com.example.bookmanagement.domain.Author
import com.example.bookmanagement.dto.CreateAuthorRequest
import com.example.bookmanagement.dto.UpdateAuthorRequest
import com.example.bookmanagement.exception.AuthorNotFoundException
import com.example.bookmanagement.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 著者サービス
 * 著者のビジネスロジックを処理する
 */
@Service
class AuthorService(
    private val authorRepository: AuthorRepository
) {

    /**
     * 全著者を取得
     */
    @Transactional(readOnly = true)
    fun findAll(): List<Author> {
        return authorRepository.findAll()
    }

    /**
     * 著者IDで取得
     */
    @Transactional(readOnly = true)
    fun findById(id: Long): Author {
        return authorRepository.findById(id)
            ?: throw AuthorNotFoundException("指定された著者が存在しません: id=$id")
    }

    /**
     * 著者を作成
     */
    @Transactional
    fun create(request: CreateAuthorRequest): Author {
        val author = Author(
            name = request.name,
            birthDate = request.birthDate
        )

        return authorRepository.create(author)
    }

    /**
     * 著者を更新
     */
    @Transactional
    fun update(id: Long, request: UpdateAuthorRequest): Author {
        if (authorRepository.findById(id) == null) {
            throw AuthorNotFoundException("指定された著者が存在しません: id=$id")
        }

        val updatedAuthor = Author(
            id = id,
            name = request.name,
            birthDate = request.birthDate
        )

        return authorRepository.update(id, updatedAuthor)
    }

    /**
     * 著者を削除
     */
    @Transactional
    fun delete(id: Long) {
        if (authorRepository.findById(id) == null) {
            throw AuthorNotFoundException("指定された著者が存在しません: id=$id")
        }
        authorRepository.delete(id)
    }
}
