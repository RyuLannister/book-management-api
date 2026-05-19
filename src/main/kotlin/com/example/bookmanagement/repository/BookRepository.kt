package com.example.bookmanagement.repository

import com.example.bookmanagement.domain.Author
import com.example.bookmanagement.domain.Book
import com.example.bookmanagement.domain.PublicationStatus
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Result
import org.springframework.stereotype.Repository
import com.example.bookmanagement.domain.Tables.*

/**
 * 書籍リポジトリ
 * jOOQを使用してデータベースアクセスを行う
 */
@Repository
class BookRepository(private val dsl: DSLContext) {

    /**
     * 書籍を作成
     */
    fun create(book: Book): Book {
        return dsl.transactionResult { ctx ->
            // 書籍を挿入
            val bookRecord = ctx.newRecord(BOOKS).apply {
                this.title = book.title
                this.price = book.price
                this.publicationStatus = book.publicationStatus.name
            }.also { it.insert() }

            val bookId = bookRecord.id

            // 書籍と著者の関連付けを挿入
            book.authors.forEach { author ->
                ctx.newRecord(BOOK_AUTHORS).apply {
                    this.bookId = bookId
                    this.authorId = author.id
                }.insert()
            }

            findByIdInternal(ctx, bookId)!!
        }
    }

    /**
     * 書籍を更新
     */
    fun update(id: Long, book: Book): Book {
        return dsl.transactionResult { ctx ->
            // 書籍を更新
            ctx.update(BOOKS)
                .set(BOOKS.TITLE, book.title)
                .set(BOOKS.PRICE, book.price)
                .set(BOOKS.PUBLICATION_STATUS, book.publicationStatus.name)
                .where(BOOKS.ID.eq(id))
                .execute()

            // 既存の関連付けを削除
            ctx.deleteFrom(BOOK_AUTHORS)
                .where(BOOK_AUTHORS.BOOK_ID.eq(id))
                .execute()

            // 新しい関連付けを挿入
            book.authors.forEach { author ->
                ctx.newRecord(BOOK_AUTHORS).apply {
                    this.bookId = id
                    this.authorId = author.id
                }.insert()
            }

            findByIdInternal(ctx, id)!!
        }
    }

    /**
     * 全書籍を取得
     */
    fun findAll(): List<Book> {
        val booksResult: Result<Record> = dsl
            .selectFrom(BOOKS)
            .orderBy(BOOKS.ID)
            .fetch()

        return booksResult.map { mapBookRecord(it) }
    }

    /**
     * 書籍IDで取得
     */
    fun findById(id: Long): Book? {
        return findByIdInternal(dsl, id)
    }

    /**
     * 著者IDに紐づく書籍を取得
     */
    fun findByAuthorId(authorId: Long): List<Book> {
        val booksResult: Result<Record> = dsl
            .select(BOOKS.fields().toList())
            .from(BOOKS)
            .join(BOOK_AUTHORS).on(BOOK_AUTHORS.BOOK_ID.eq(BOOKS.ID))
            .where(BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
            .fetch()

        return booksResult.map { mapBookRecord(it) }
    }

    /**
     * 書籍を削除
     */
    fun delete(id: Long) {
        dsl.deleteFrom(BOOKS)
            .where(BOOKS.ID.eq(id))
            .execute()
    }

    private fun findByIdInternal(ctx: DSLContext, id: Long): Book? {
        val bookRecord: Record? = ctx
            .selectFrom(BOOKS)
            .where(BOOKS.ID.eq(id))
            .fetchOne()

        return bookRecord?.let { mapBookRecord(it) }
    }

    private fun mapBookRecord(record: Record): Book {
        val bookId = record.get(BOOKS.ID)
        val authors = findAuthorsByBookId(bookId)

        return Book(
            id = bookId,
            title = record.get(BOOKS.TITLE)!!,
            price = record.get(BOOKS.PRICE)!!,
            publicationStatus = PublicationStatus.valueOf(record.get(BOOKS.PUBLICATION_STATUS)!!),
            authors = authors,
            createdAt = record.get(BOOKS.CREATED_AT),
            updatedAt = record.get(BOOKS.UPDATED_AT)
        )
    }

    private fun findAuthorsByBookId(bookId: Long): List<Author> {
        val authorRecords: Result<Record> = dsl
            .select(AUTHORS.fields().toList())
            .from(AUTHORS)
            .join(BOOK_AUTHORS).on(BOOK_AUTHORS.AUTHOR_ID.eq(AUTHORS.ID))
            .where(BOOK_AUTHORS.BOOK_ID.eq(bookId))
            .fetch()

        return authorRecords.map { record ->
            Author(
                id = record.get(AUTHORS.ID),
                name = record.get(AUTHORS.NAME)!!,
                birthDate = record.get(AUTHORS.BIRTH_DATE)!!,
                createdAt = record.get(AUTHORS.CREATED_AT)
            )
        }
    }
}
