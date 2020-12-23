package net.okocraft.databackup.lang;

import org.jetbrains.annotations.NotNull;

public enum DefaultMessage implements com.github.siroshun09.mcmessage.message.DefaultMessage {
    PREFIX("prefix", "&8[&6DataBackup&8]&r "),

    BACKUP_START("backup.start", "&7* プレイヤーデータのバックアップを開始しています..."),
    BACKUP_FINISH("backup.finish", "&7* バックアップが完了しました。"),

    COMMAND_USAGE("command.usage", "&b/databackup help"),
    COMMAND_BACKUP_USAGE("command.backup.usage", "&b/db backup <player|all>&8 - &7プレイヤーのデータをバックアップします"),
    COMMAND_BACKUP_ALL("command.backup.all", "&7全員のデータバックアップを開始しました。コンソールを確認してください。"),
    COMMAND_BACKUP_PLAYER("command.backup.player", "&b%player% &7のデータをバックアップしました。"),
    COMMAND_BACKUP_FAILURE("command.backup.failure", "&cバックアップに失敗しました。コンソールを確認してください。"),
    COMMAND_CLEAN_USAGE("command.clean.usage", "&b/db clean&8 - &7期限切れのバックアップファイルを削除します。"),
    COMMAND_CLEAN_RUN("command.clean.run", "&7チェックタスクを開始しました。コンソールを確認してください。"),
    COMMAND_ROLLBACK_USAGE("command.rollback.usage", "&b/db rollback <type> <target> <file>&8 - &7指定したファイルに基づきデータを戻します。"),
    COMMAND_ROLLBACK_SENDER("command.rollback.sender", "&b%player%&7 の &b%type%&7 のデータを &b%date%&7 時点に戻しました。"),
    COMMAND_ROLLBACK_TARGET("command.rollback.target", "&b%type%&7 のデータが &b%date%&7 時点に戻されました。"),
    COMMAND_ROLLBACK_FAILURE("command.rollback.failure", "&cロールバックに失敗しました。コンソールを確認してください。"),
    COMMAND_SEARCH_USAGE("command.search.usage", "&b/db search {offline} <target> <material> {page} - &7指定したアイテムを検索します。"),
    COMMAND_SEARCH_NOT_FOUND("command.search.not-found", "&c指定した Material のアイテムは見つかりませんでした。"),
    COMMAND_SEARCH_FAILURE("command.search.failure", "&cアイテムの検索に失敗しました。コンソールを確認してください。"),
    COMMAND_SHOW_USAGE("command.show.usage", "&b/db show {offline} <type> <target> <file>&8 - &7指定したデータの内訳を表示します。"),
    COMMAND_SHOW_EXP("command.show.exp", "&b%date%&7 時点の経験値: &b%exp%"),
    COMMAND_SHOW_MONEY("command.show.money", "&b%date%&7 時点の所持金: &b%money%円"),
    COMMAND_SHOW_MCMMO("command.show.mcmmo", "&b%date%&7 時点の &b%skill%&7 の経験値: &b%skill_xp%"),

    COMMAND_NO_PERMISSION("command.no-permission", "&c権限がありません: "),
    COMMAND_ONLY_PLAYER("command.only-player", "&cこのコマンドはプレイヤーのみ実行できます。"),
    COMMAND_INVALID_DATA_TYPE("command.invalid-data-type", "無効なデータタイプです: %type%"),
    COMMAND_PLAYER_NOT_FOUND("command.player-not-found", "&cプレイヤー %player% は見つかりませんでした。"),
    COMMAND_BACKUP_NOT_FOUND("command.backup-not-found", "&c指定したバックアップは存在しません。"),
    COMMAND_MATERIAL_NOT_FOUND("command.material-not-found", "&c指定した Material は存在しません。"),

    INVENTORY_TITLE("gui-title.inventory", "&8%player% のインベントリ (%date%)"),
    ENDERCHEST_TITLE("gui-title.enderchest", "&8%player% のエンダーチェスト (%date%)"),
    SEARCH_RESULT_TITLE("gui-title.search-result", "&8%material% での検索結果 (%page%ページ目)");

    private final String key;
    private final String def;

    DefaultMessage(String key, String def) {
        this.key = key;
        this.def = def;
    }


    @Override
    public @NotNull String getKey() {
        return key;
    }

    @Override
    public @NotNull String get() {
        return def;
    }
}
