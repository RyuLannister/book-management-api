package com.example.bookmanagement.domain

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 書籍エンティティ
 *
 * @property id 書籍ID
 * @property title タイトル
 * @property price 価格（0以上）
 * @property publicationStatus 出版状況
 * @property authors 著者リスト（最低1人必要）
 * @property createdAt 作成日時
 * @property updatedAt 更新日時
 */
data class Book(
    val id: Long? = null,
    val title: String,
    val price: Int,
    val publicationStatus: PublicationStatus,
    val authors: List<Author> = emptyList(),
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    init {
        require(price >= 0) { "価格は0以上である必要があります" }
        require(authors.isNotEmpty()) { "最低1人の著者が必要です" }
    }

    /**
     * 出版済みステータスを未出版に変更する。
     * 出版済みの書籍は未出版に戻せない。
     */
    fun unpublish(): Book {
        check(publicationStatus != PublicationStatus.PUBLISHED) {
            "出版済みの書籍は未出版に変更できません"
        }
        return copy(publicationStatus = PublicationStatus.UNPUBLISHED)
    }
}

/**
 * 出版状況列挙型
 */
enum class PublicationStatus {
    /** 未出版 */
    UNPUBLISHED,
    /** 出版済み */
    PUBLISHED
}

/**
 * 著者エンティティ
 *
 * @property id 著者ID
 * @property name 名前
 * @property birthDate 生年月日（現在日以前）
 * @property createdAt 作成日時
 */
data class Author(
    val id: Long? = null,
    val name: String,
    val birthDate: LocalDate,
    val createdAt: LocalDateTime? = null
) {
    init {
        require(birthDate <= LocalDate.now()) { "生年月日は現在日以前である必要があります" }
    }
}
