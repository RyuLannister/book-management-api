-- 書籍管理システムのデータベーススキーマ
-- 著者テーブル
CREATE TABLE authors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_birth_date CHECK (birth_date <= CURRENT_DATE)
);

-- 書籍テーブル
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    publication_status VARCHAR(20) NOT NULL DEFAULT 'UNPUBLISHED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_price CHECK (price >= 0),
    CONSTRAINT chk_publication_status CHECK (publication_status IN ('UNPUBLISHED', 'PUBLISHED'))
);

-- 書籍と著者の多対多リレーションシップ
CREATE TABLE book_authors (
    book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    author_id BIGINT NOT NULL REFERENCES authors(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, author_id)
);

-- インデックス
CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_publication_status ON books(publication_status);
CREATE INDEX idx_authors_name ON authors(name);
CREATE INDEX idx_book_authors_book_id ON book_authors(book_id);
CREATE INDEX idx_book_authors_author_id ON book_authors(author_id);
