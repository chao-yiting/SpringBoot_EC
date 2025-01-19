# ベースイメージ: Java 17を使用
FROM eclipse-temurin:17-jdk-focal

# 日本語対応のため、日本語ロケールとタイムゾーンを設定
ENV TZ=Asia/Tokyo
ENV LANG=ja_JP.UTF-8

# アプリケーションディレクトリの作成
WORKDIR /app

# Gradleビルドに必要なファイルをコピー
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# ソースコードをコピー
COPY src src

# Gradleラッパーに実行権限を付与
RUN chmod +x ./gradlew

# アプリケーションをビルド（テストはスキップ）
RUN ./gradlew build -x test

# ポート8080を公開
EXPOSE 8080

# アプリケーション起動
CMD ["java", "-jar", "build/libs/*.jar"]