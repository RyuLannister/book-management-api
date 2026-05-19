package com.example.bookmanagement.repository

import com.example.bookmanagement.domain.Author
import com.example.bookmanagement.domain.Book
import com.example.bookmanagement.domain.PublicationStatus
import com.example.bookmanagement.domain.Tables
import com.example.bookmanagement.domain.tables.records.AuthorsRecord
import com.example.bookmanagement.domain.tables.records.BookAuthorsRecord
import com.example.bookmanagement.domain.tables.records.BooksRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

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
        return dsl.transactionResult { configuration ->
            val ctx = configuration.dsl()
            
            // 書籍を挿入
            val bookRecord = ctx.newRecord(Tables.BOOKS).apply {
                this.setTitle(book.title)
                this.setPrice(book.price)
                this.setPublicationStatus(book.publicationStatus.name)
            }.also { it.insert() }

            val bookId = bookRecord.getId()

            // 書籍と著者の関連付けを挿入
            book.authors.forEach { author ->
                ctx.newRecord(Tables.BOOK_AUTHORS).apply {
                    this.setBookId(bookId)
                    this.setAuthorId(author.id)
                }.insert()
            }

            findByIdInternal(ctx, bookId)!!
        }
    }

    /**
     * 書籍を更新
     */
    fun update(id: Long, book: Book): Book {
        return dsl.transactionResult { configuration ->
            val ctx = configuration.dsl()
            
            // 書籍を更新
            ctx.update(Tables.BOOKS)
                .set(Tables.BOOKS.TITLE, book.title)
                .set(Tables.BOOKS.PRICE, book.price)
                .set(Tables.BOOKS.PUBLICATION_STATUS, book.publicationStatus.name)
                .where(Tables.BOOKS.ID.eq(id))
                .execute()

            // 既存の関連付けを削除
            ctx.deleteFrom(Tables.BOOK_AUTHORS)
                .where(Tables.BOOK_AUTHORS.BOOK_ID.eq(id))
                .execute()

            // 新しい関連付けを挿入
            book.authors.forEach { author ->
                ctx.newRecord(Tables.BOOK_AUTHORS).apply {
                    this.setBookId(id)
                    this.setAuthorId(author.id)
                }.insert()
            }

            findByIdInternal(ctx, id)!!
        }
    }

    /**
     * 全書籍を取得
     */
    fun findAll(): List<Book> {
        val booksResult = dsl
            .selectFrom(Tables.BOOKS)
            .orderBy(Tables.BOOKS.ID)
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
        val booksResult = dsl
            .select(Tables.BOOKS.fields().toList())
            .from(Tables.BOOKS)
            .join(Tables.BOOK_AUTHORS).on(Tables.BOOK_AUTHORS.BOOK_ID.eq(Tables.BOOKS.ID))
            .where(Tables.BOOK_AUTHORS.AUTHOR_ID.eq(authorId))
            .fetch()

        return booksResult.map { mapBookRecord(it as BooksRecord) }
    }

    /**
     * 書籍を削除
     */
    fun delete(id: Long) {
        dsl.deleteFrom(Tables.BOOKS)
            .where(Tables.BOOKS.ID.eq(id))
            .execute()
    }

    private fun findByIdInternal(ctx: DSLContext, id: Long): Book? {
        val bookRecord = ctx
            .selectFrom(Tables.BOOKS)
            .where(Tables.BOOKS.ID.eq(id))
            .fetchOne()

        return bookRecord?.let { mapBookRecord(it) }
    }

    private fun mapBookRecord(record: BooksRecord): Book {
        val bookId = record.getId()
        val authors = findAuthorsByBookId(bookId)

        return Book(
            id = bookId,
            title = record.getTitle()!!,
            price = record.getPrice()!!,
            publicationStatus = PublicationStatus.valueOf(record.getPublicationStatus()!!),
            authors = authors,
            createdAt = record.getCreatedAt(),
            updatedAt = record.getUpdatedAt()
        )
    }

    private fun findAuthorsByBookId(bookId: Long): List<Author> {
        val authorRecords = dsl
            .select(Tables.AUTHORS.fields().toList())
            .from(Tables.AUTHORS)
            .join(Tables.BOOK_AUTHORS).on(Tables.BOOK_AUTHORS.AUTHOR_ID.eq(Tables.AUTHORS.ID))
            .where(Tables.BOOK_AUTHORS.BOOK_ID.eq(bookId))
            .fetch()

        return authorRecords.map { record ->
            Author(
                id = record.get(Tables.AUTHORS.ID),
                name = record.get(Tables.AUTHORS.NAME)!!,
                birthDate = record.get(Tables.AUTHORS.BIRTH_DATE)!!,
                createdAt = record.get(Tables.AUTHORS.CREATED_AT)
            )
        }
    }
}
