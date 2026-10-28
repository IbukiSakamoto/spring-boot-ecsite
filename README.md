# Spring Boot ECサイト (学習用デモ)

Java 21、Spring Boot 3、Thymeleaf、MySQL を使用した学習用ECサイトの最小構成アプリケーションです。

---

## 1. 使用技術
- **言語**: Java 21 (OpenJDK 21)
- **フレームワーク**: Spring Boot 3.3.4
  - Spring Web (MVC)
  - Spring Data JPA (Hibernate)
  - Thymeleaf (テンプレートエンジン)
- **データベース**: MySQL 8.4
- **ビルドツール**: Apache Maven 3.9+

---

## 2. 実装機能一覧
1. **商品一覧表示** (`/`, `/products`)
   - 登録されている全商品の一覧をカード形式で表示
   - 在庫状況バッジの表示 (在庫あり、残りわずか、在庫切れ)
2. **商品検索**
   - 商品名および説明文のキーワードあいまい検索
3. **価格順ソート**
   - 「標準」「価格が安い順」「価格が高い順」の並び替え
4. **ショッピングカート** (`/cart`)
   - セッションスコープで商品を一時保持
   - カート内商品の数量変更、削除、カートを空にする機能
   - 小計・合計金額の自動計算
5. **注文処理 & 在庫確認** (`/order/checkout`)
   - 注文確定時に最新の在庫数をチェック
   - 在庫不足の場合はエラーメッセージを表示して購入を中断
   - 注文完了時に在庫を自動減算し、注文履歴・購入明細を永続化
6. **注文完了画面** (`/order/complete/{id}`)
   - 注文番号、注文日時、注文者名、購入明細、合計金額を表示
7. **初期商品データ自動投入**
   - 初回起動時、データベースが空の場合にサンプル商品（PC、ヘッドホン等）を自動登録

---

## 3. 設定値一覧 (環境変数・接続情報)

本リポジトリではセキュリティのため、データベースのパスワードなどの機密情報はコードおよび設定ファイルに直接記載していません。実行時に環境変数または設定で渡す必要があります。

| 設定項目 | プロパティ / 環境変数 | デフォルト値 / 推奨値 | 説明 |
| :--- | :--- | :--- | :--- |
| **DB接続URL** | `spring.datasource.url` | `jdbc:mysql://localhost:3306/ecdb?...` | MySQLの接続先URL (UTF-8, Asia/Tokyo) |
| **DBユーザー名** | `spring.datasource.username` | `ecuser` | データベースの接続ユーザー名 |
| **DBパスワード** | `DB_PASSWORD` (環境変数) | （指定必須） | MySQLユーザー作成時に設定したパスワード |
| **ポート番号** | `server.port` | `8080` | Spring Boot の待ち受けポート番号 |

---

## 4. セットアップと起動手順

### 前提条件
- OpenJDK 21
- Apache Maven 3.9+
- MySQL Server 8.0+

### 1) データベースの準備
MySQLに管理者ユーザー（root等）で接続し、データベースと専用ユーザーを作成します。

```sql
-- データベースの作成
CREATE DATABASE IF NOT EXISTS ecdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ユーザー作成（<任意のパスワード> 部分をご自身で設定してください）
CREATE USER IF NOT EXISTS 'ecuser'@'localhost' IDENTIFIED BY '<任意のパスワード>';

-- 権限付与
GRANT ALL PRIVILEGES ON ecdb.* TO 'ecuser'@'localhost';
FLUSH PRIVILEGES;
```

### 2) ビルド
プロジェクトルートで以下のコマンドを実行します。

```bash
mvn clean package
```

### 3) アプリケーションの起動
先ほど設定したMySQLパスワードを環境変数 `DB_PASSWORD` に指定して起動します。

```bash
# jarから起動する場合
DB_PASSWORD="<設定したパスワード>" java -jar target/ecsite-0.0.1-SNAPSHOT.jar

# または Maven プラグインから起動する場合
DB_PASSWORD="<設定したパスワード>" mvn spring-boot:run
```

起動後、ブラウザで以下のURLにアクセスしてください。
- **URL**: `http://localhost:8080/`
