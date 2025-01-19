# お取り寄せECサイト

## 概要
このプロジェクトは、ECサイトの練習として開発されました。ユーザーは商品の閲覧、カート管理、注文処理までの一連の機能を利用できます。

## 主な機能
- ユーザー管理機能（登録・ログイン・権限管理）
- 商品カタログ表示（カテゴリー別表示対応）★
- カート機能（商品追加・数量変更・削除）
- 注文機能
  - 配送先住所管理
  - 都道府県別送料・配送予定日計算
  - 消費税計算
- 決済機能（Stripe決済システム連携）
- 在庫管理機能
- 管理者機能
  - 商品管理（登録・編集・削除）
  - 注文管理
  - ユーザー管理

## 使用技術
### バックエンド
- Java 17
- Spring Boot 3.1.3
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- MySQL
- Lombok

### フロントエンド
- Thymeleaf
- HTML/CSS
- JavaScript

### 開発環境
- Gradle
- Git/GitHub
- Spring Boot DevTools

### 決済システム
- Stripe API (v24.0.0)

## テーブル設計
主要なテーブル：
- users（ユーザー情報）
- products（商品情報）
- categories（商品カテゴリー）
- orders（注文情報）
- carts（カート情報）
- payments（支払い情報）

その他、配送料金や消費税などのマスターテーブルも実装

## プロジェクト構成
src/main/java

├── controller/    # リクエスト制御

├── entity/       # エンティティ

├── repository/   # データアクセス

├── request/      # リクエストデータ処理

└── service/      # ビジネスロジック

## セットアップ手順
1. リポジトリのクローン
```bash
git clone [リポジトリURL]

2. データベースのセットアップ
- MySQLをインストール
- 提供されているSQLスクリプトを実行してテーブルを作成

3. application.propertiesの設定
- データベース接続情報
- Stripe APIキーを適切に設定

4.アプリケーションの起動
./gradlew bootRun

##動作環境
- Java 17以上
- MySQL 8.0以上

##今後の改善点
- 商品検索機能の拡充（カテゴリーでの検索対応）
- 商品レビュー機能の追加
- テスト実装の追加
- レスポンシブデザインの改善

##作成者
CHAO YI-TING