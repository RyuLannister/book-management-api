package com.example.bookmanagement.repository

import com.example.bookmanagement.domain.Author
import com.example.bookmanagement.domain.Tables
import com.example.bookmanagement.domain.tables.records.AuthorsRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

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
        val record = dsl.newRecord(Tables.AUTHORS).apply {
            this.setName(author.name)
            this.setBirthDate(author.birthDate)
        }.also { it.insert() }
        record.refresh() // DBのDEFAULT値を取得

        return author.copy(id = record.getId(), createdAt = record.getCreatedAt())
    }

    /**
     *  著者を更新
     */
    fun update(id: Long, author: Author): Author {
        dsl.update(Tables.AUTHORS)
            .set(Tables.AUTHORS.NAME, author.name)
            .set(Tables.AUTHORS.BIRTH_DATE, author.birthDate)
            .where(Tables.AUTHORS.ID.eq(id))
            .execute()

        return findById(id)!!
    }

    /**
     * 全 著者を取得
     */
    fun findAll(): List<Author> {
        return dsl.selectFrom(Tables.AUTHORS)
            .orderBy(Tables.AUTHORS.ID)
            .fetch()
            .map { mapAuthorRecord(it) }
    }

    /**
     * 著者IDで取得
     */
    fun findById(id: Long): Author? {
        return dsl.selectFrom(Tables.AUTHORS)
            .where(Tables.AUTHORS.ID.eq(id))
            .fetchOne()
            ?.let { mapAuthorRecord(it) }
    }

    /**
     * 複数の著者IDで取得
     */
    fun findByIds(ids: List<Long>): List<Author> {
        if (ids.isEmpty()) return emptyList()

        return dsl.selectFrom(Tables.AUTHORS)
            .where(Tables.AUTHORS.ID.`in`(ids))
            .fetch()
            .map { mapAuthorRecord(it) }
    }

    /**
     * 複数の 著者が存在するか確認
     */
    fun existsByIds(ids: List<Long>): Boolean {
        val count = dsl.selectCount()
            .from(Tables.AUTHORS)
            .where(Tables.AUTHORS.ID.`in`(ids))
            .fetchOne(0, Int::class.java)

        return count == ids.size
    }

    /**
     *  著者を削除
     */
    fun delete(id: Long) {
        dsl.deleteFrom(Tables.AUTHORS)
            .where(Tables.AUTHORS.ID.eq(id))
            .execute()
    }

    private fun mapAuthorRecord(record: AuthorsRecord): Author {
        return Author(
            id = record.getId(),
            name = record.getName()!!,
            birthDate = record.getBirthDate()!!,
            createdAt = record.getCreatedAt()
        )
    }
}
