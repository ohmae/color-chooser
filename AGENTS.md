# AGENTS.md

このファイルはリポジトリ全体で作業するエージェント向けのガイドです。

## プロジェクト概要

Android 向けの色選択ライブラリです。View 版と Jetpack Compose 版を Maven Central に公開しています。

| ディレクトリ | 役割 |
| --- | --- |
| `chooser/` | View 版。公開パッケージは `net.mm2d.color.chooser` |
| `chooser-compose/` | Compose 版。公開パッケージは `net.mm2d.color.chooser.compose` |
| `sample/` | View 版の動作確認用アプリ |
| `sample-compose/` | Compose 版の動作確認用アプリ |
| `build-logic/` | Android・Kotlin・公開・API 検証などの共通 Gradle convention plugin |
| `gradle/libs.versions.toml` | 依存ライブラリとプラグインのバージョンカタログ |
| `docs/dokka/` | Dokka の生成先 |
| `readme/` | README 用画像 |

利用方法は `README.md`、実際の呼び出し方は各サンプルの `src/main/kotlin/` を参照してください。

## 作業方針

- ユーザーへの説明・確認・完了報告は、指定がなければ日本語で行ってください。
- 作業前に `git status --short` と対象コードを確認し、既存の未コミット変更を保持してください。
- 変更は依頼の範囲に絞り、無関係なリファクタリングや一括整形を混ぜないでください。
- View 版と Compose 版は独立したライブラリです。共通仕様に関わる変更では双方への影響を確認し、
  一方だけを変更する場合は理由を説明してください。
- 公開 API、既定値、色の表現、アルファ値の扱い、状態復元、結果通知の互換性に注意してください。
- 公開 API や利用方法を変更する場合は、関連する KDoc・README・サンプルも確認してください。

## ビルド設定と実装規約

- リポジトリルートから Gradle Wrapper (`./gradlew`) を使用してください。
- Android SDK の参照先は環境変数またはローカルの `local.properties` で設定してください。
  個人のパスや認証情報をコミットしないでください。
- SDK・JVM・ライブラリ公開バージョンの定義元は
  `build-logic/src/main/kotlin/net/mm2d/build/Projects.kt` です。
  現在は JVM toolchain 21、Java/Kotlin の出力ターゲット 11 です。両者を混同しないでください。
- 共通ビルド設定は `build-logic/`、依存関係はバージョンカタログと各モジュールの
  `build.gradle.kts` に従って変更してください。
- Kotlin と Gradle Kotlin DSL の整形は `.editorconfig` と既存コードに合わせてください。
  基本はスペース 4 個、LF、UTF-8、最大行長 120 です。
- ライブラリのリソース名には `mm2d_cc_` プレフィックスを使用してください。
  公開リソースは各ライブラリの `src/main/res/values/public.xml` も確認してください。
- View 版では既存の ViewBinding と色通知の仕組み、Compose 版では既存の状態管理と
  コールバックの構成を確認してから変更してください。

## 検証

変更に関係する検証を選んで実行してください。文書だけの変更は内容・参照先・差分の確認で十分です。

| 対象 | コマンド |
| --- | --- |
| Kotlin のスタイル検査 | `./gradlew ktlint` |
| View 版とサンプルのビルド | `./gradlew :chooser:assembleDebug :sample:assembleDebug` |
| Compose 版とサンプルのビルド | `./gradlew :chooser-compose:assembleDebug :sample-compose:assembleDebug` |
| View 版の静的解析 | `./gradlew :chooser:lintDebug :sample:lintDebug` |
| Compose 版の静的解析 | `./gradlew :chooser-compose:lintDebug :sample-compose:lintDebug` |
| ライブラリの JVM テスト | `./gradlew :chooser:testDebugUnitTest :chooser-compose:testDebugUnitTest` |
| 公開 API の検査 | `./gradlew :chooser:apiCheck :chooser-compose:apiCheck` |
| 依存関係の検査 | `./gradlew dependencyGuard` |
| View 版サンプルのインストール | `./gradlew :sample:installDebug` |
| Compose 版サンプルのインストール | `./gradlew :sample-compose:installDebug` |

- `ktlint` と `ktlintFormat` は `isIgnoreExitValue = true` の設定です。
  `BUILD SUCCESSFUL` だけで合格と判断せず、出力された違反も確認してください。
- `./gradlew ktlintFormat` は広い範囲を書き換えます。実行後は対象外の変更が混ざっていないか確認してください。
- 現時点ではテストソースがありません。テストタスクが `NO-SOURCE` の場合、テスト実施済みとは扱わず、
  ロジックの変更では必要な回帰テストを検討してください。
- UI を変更した場合は、対象のサンプルで操作を確認してください。変更に応じてタブ切替、
  RGB/HSV/パレット間の同期、アルファ値、確定・キャンセル、再生成時の状態を確認してください。
- 実機・エミュレーターやビルド環境の制約で検証できなかった項目は、完了報告で明示してください。

## API・依存関係のベースライン

- 公開 API の基準は `chooser/api/chooser.api` と `chooser-compose/api/chooser-compose.api` です。
  `apiCheck` は独自の `BinaryCompatibilityValidator.kt` で登録され、`check` に接続されています。
- 意図した API 変更では対象モジュールの `apiDump` を実行し、生成された差分を確認してください。
  検査を通すためだけにベースラインを更新しないでください。
- 依存関係の基準は各ライブラリの `dependencies/releaseRuntimeClasspath.txt` です。
  意図した依存関係の更新後に `./dependency-guard-baseline.sh` を使い、推移的依存も含めて差分を確認してください。
- `./version-catalog-update.sh` は更新候補を作成する対話式スクリプトです。
  既存の `gradle/libs.versions.updates.toml` を削除するため、必要な作業内容がないか確認してください。

## 生成物と完了報告

- `build/`、`.gradle/`、生成された ViewBinding などを直接編集しないでください。
- `docs/dokka/` は生成物です。API 文書の修正は元の KDoc に行い、再生成が必要な作業で更新してください。
- 通常の修正で公開バージョンの変更や Maven Central への公開を行わないでください。
  リリース作業が依頼された場合は、その依頼の範囲に従ってください。
- 完了時は変更点、実行した検証と結果、未検証の項目を簡潔に報告してください。
