# ts-videos

TS抜きした録画データを処理、管理するためのアプリケーション

- core: 共通コンポーネント
- manager: 処理済みの録画データを管理するためのツール
- processor: 録画データを処理して管理ツールで管理できる状態にするためのアプリケーション

## ローカルでのバックエンド動作確認

`docker-compose.test.yml` で MariaDB と Samba (NAS) を起動すると、`backend/core/src/main/resources/application-core.yaml` のデフォルト値のまま環境変数なしで `manager:api` をローカル起動できます。

```bash
docker compose -f docker-compose.test.yml up -d
cd backend && ./gradlew manager:api:bootRun
```
