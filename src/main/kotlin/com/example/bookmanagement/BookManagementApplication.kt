package com.example.bookmanagement

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * 書籍管理APIアプリケーション
 *
 * 書籍と著者の情報を管理するREST APIを提供する。
 * 技術スタック：Kotlin + Spring Boot + jOOQ + PostgreSQL
 */
@SpringBootApplication
class BookManagementApplication

fun main(args: Array<String>) {
    runApplication<BookManagementApplication>(*args)
}
