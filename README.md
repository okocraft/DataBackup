![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/okocraft/DataBackup)
![GitHub Workflow Status](https://img.shields.io/github/workflow/status/okocraft/DataBackup/Java%20CI)
![GitHub](https://img.shields.io/github/license/okocraft/DataBackup)

# DataBackup

プレイヤーデータをバックアップする Spigot プラグインです。

## Requirements

- Java 11+
- Spigot 1.15+
- Vault (所持金をバックアップする場合)
- [SiroLibrary](https://github.com/SiroPlugins/SiroLibrary) 1.8.0+
- [UserAPI](https://github.com/okocraft/UserAPI) 1.0+

## Usage

サーバーディレクトリの `/plugins/` に配置し、サーバーを再起動する。

`/plugins/DataBackup/config.yml` にてバックアップ間隔と保存期間を設定できる。

### Commands

コマンド: `/databackup` (略: `/db`)

タブ補完が使用可能です。

```
/db backup <target>: プレイヤーのバックアップを取ります。
/db clean: 期限切れのバックアップファイルを削除します (起動毎に自動実行)
/db rollback <type> <target> <file>: 指定したデータを戻します。
/db show {offline} <type> <target> <file>: 指定したデータの内訳を表示します。
※ offline と指定しない限り、オンラインプレイヤーとして検索されます。
```

バックアップするデータタイプ: 持ち物 `inventory`, エンダーチェスト `enderchest`, 所持金 `money` ,経験値 `xp` 

## License

このプロジェクトは GPL-3.0 のもとで公開しています。詳しくは [ライセンスファイル](LICENSE) をお読みください。

This project is licensed under the permissive GPL-3.0 license. Please see [LICENSE](LICENSE) for more info.

Copyright © 2019-2020, Siroshun09