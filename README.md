# 書籍管理システムのバックエンドAPI

クオカード デジタルイノベーションラボのコーディングテスト用プロジェクトです。

## 技術スタック

- **言語**: Kotlin
- **フレームワーク**: Spring Boot 3.2
- **データベースアクセス**: jOOQ
- **マイグレーションツール**: Flyway
- **データベース**: PostgreSQL
- **ビルドツール**: Gradle (Groovy DSL)
- **Javaバージョン**: 21

## プロジェクト構造

```
book-management-api/
├── src/main/kotlin/com/example/bookmanagement/
│   ├── controller/      # REST APIコントローラー
│   ├── service/         # ビジネスロジック
│   ├── repository/      # データベースアクセス（jOOQ）
│   ├── domain/          # エンティティ・列挙型
│   ├── dto/             # リクエスト・レスポンスDTO
│   ├── exception/       # 例外クラス
│   └── config/          # 設定クラス
├── src/main/resources/
│   ├── db/migration/    # FlywayマイグレーションSQL
│   └── application.yaml # アプリケーション設定
├── src/test/            # テストコード
├── build.gradle.kts     # Gradleビルド設定
└── docker-compose.yml   # PostgreSQL用Docker Compose
```

## 環境構築

### 前提条件

- JDK 21
- Docker / Docker Compose

### 1. PostgreSQLの起動

```bash
docker-compose up -d
```

### 2. jOOQコードの生成

※ 初回のビルド時にjOOQがデータベースに接続し、コードを一括生成します。

```bash
gradle generateJooq
```

### 3. アプリケーションの起動

```bash
gradle bootRun
```

またはJARファイルをビルドして実行：

```bash
gradle build
java -jar build/libs/book-management-api-0.0.1-SNAPSHOT.jar
```

## APIエンドポイント

### 書籍API

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| GET | `/api/books` | 全書籍を取得 |
| GET | `/api/books/{id}` | 書籍IDで取得 |
| GET | `/api/books/author/{authorId}` | 著者に紐づく書籍を取得 |
| POST | `/api/books` | 書籍を作成 |
| PUT | `/api/books/{id}` | 書籍を更新 |
| DELETE | `/api/books/{id}` | 書籍を削除 |

### 著者API

| メソッド | エンドポイント | 説明 |
|---------|---------------|------|
| GET | `/api/authors` | 全著者を取得 |
| GET | `/api/authors/{id}` | 著者IDで取得 |
| POST | `/api/authors` | 著者を作成 |
| PUT | `/api/authors/{id}` | 著者を更新 |
| DELETE | `/api/authors/{id}` | 著者を削除 |

## 書籍の仕様

- **タイトル**: 必須、255文字以内
- **価格**: 0以上の整数
- **著者**: 最低1人必要（複数可能）
- **出版状況**: `UNPUBLISHED`（未出版）または`PUBLISHED`（出版済み）
  - 出版済みの書籍は未出版に戻せない

## 著者の仕様

- **名前**: 必須、255文字以内
- **生年月日**: 現在日以前であること
- **書籍**: 複数の書籍を執筆可能

## テストの実行

```bash
./gradlew test
```

## 注意事項

- テスト件数: 31件（BookServiceTest 10件 + AuthorServiceTest 8件 + BookControllerValidationTest 7件 + AuthorControllerValidationTest 6件）
