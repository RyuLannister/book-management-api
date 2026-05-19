package com.example.bookmanagement.repository

import com.example.bookmanagement.domain.Author
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import com.example.bookmanagement.domain.Tables.*

/**
 * 著者リポジトリ
 * jOOQを使用してデータベースアクセスを行う
 */
@Repository
class AuthorRepository(private val dsl: DSLContext) {

    /**
     * 著者を作成
     */
    fun create(author: Author): Author {
        val record = dsl.newRecord(AUTHORS).apply {
            this.name = author.name
            this.birthDate = author.birthDate
        }.also { it.insert() }

        return author.copy(id = record.id, createdAt = record.createdAt)
    }

    /**
     *  著者を更新
     */
    fun update(id: Long, author: Author): Author {
        dsl.update(AUTHORS)
            .set(AUTHORS.NAME, author.name)
            .set(AUTHORS.BIRTH_DATE, author.birthDate)
            .where(AUTHORS.ID.eq(id))
            .execute()

        return findById(id)!!
    }

    /**
     * 全 著者を取得
     */
    fun findAll(): List<Author> {
        return dsl.selectFrom(AUTHORS)
            .orderBy(AUTHORS.ID)
            .fetch()
            .map { mapAuthorRecord(it) }
    }

    /**
     * 著者IDで取得
     */
    fun findById(id: Long): Author? {
        return dsl.selectFrom(AUTHORS)
            .where(AUTHORS.ID.eq(id))
            .fetchOne()
            ?.let { mapAuthorRecord(it) }
    }

    /**
     * 複数の著者IDで取得
     */
    fun findByIds(ids: List<Long>): List<Author> {
        if (ids.isEmpty()) return emptyList()

        return dsl.selectFrom(AUTHORS)
            .where(AUTHORS.ID.`in`(ids))
            .fetch()
            .map { mapAuthorRecord(it) }
    }

    /**
     * 複数の 著者が存在するか確認
     */
    fun existsByIds(ids: List<Long>): Boolean {
        val count = dsl.selectCount()
            .from(AUTHORS)
            .where(AUTHORS.ID.`in`(ids))
            .fetchOne(0, Int::class.java)

        return count == ids.size
    }

    /**
     *  著者を削除
     */
    fun delete(id: Long) {
        dsl.deleteFrom(AUTHORS)
            .where(AUTHORS.ID.eq(id))
            .execute()
    }

    private fun mapAuthorRecord(record: Record): Author {
        return Author(
            id = record.get(AUTHORS.ID),
            name = record.get(AUTHORS.NAME)!!,
            birthDate = record.get(AUTHORS.BIRTH_DATE)!!,
            createdAt = record.get(AUTHORS.CREATED_AT)
        )
    }
}
