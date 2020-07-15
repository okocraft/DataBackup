package net.okocraft.databackup;

import com.github.siroshun09.command.sender.Sender;
import com.github.siroshun09.configapi.common.FileConfiguration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum Message {
    PREFIX("prefix", "&8[&6DataBackup&8]&r "),

    BACKUP_START("backup.start", "&7プレイヤーデータのバックアップを開始しています..."),
    BACKUP_FINISH("backup.finish", "&7バックアップが完了しました。"),

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
    COMMAND_SHOW_USAGE("command.show.usage", "&b/db show {offline} <type> <target> <file>&8 - &7指定したデータの内訳を表示します。"),
    COMMAND_SHOW_EXP("command.show.exp", "&b%date%&7 時点の経験値: &b%amount%"),
    COMMAND_SHOW_MONEY("command.show.money", "&b%date%&7 時点の所持金: &b%amount%円"),
    COMMAND_SHOW_MCMMO("command.show.mcmmo", "&b%date%&7 時点の &b%skill%&7 の経験値: &b%amount%"),

    COMMAND_NO_PERMISSION("command.no-permission", "&c権限がありません: "),
    COMMAND_ONLY_PLAYER("command.only-player", "&cこのコマンドはプレイヤーのみ実行できます。"),
    COMMAND_INVALID_DATA_TYPE("command.invalid-data-type", "無効なデータタイプです: %type%"),
    COMMAND_PLAYER_NOT_FOUND("command.player-not-found", "&cプレイヤー %player% は見つかりませんでした。"),
    COMMAND_BACKUP_NOT_FOUND("command.backup-not-found", "&c指定したバックアップは存在しません。"),

    INVENTORY_TITLE("gui-title.inventory", "&8%player% のインベントリ (%date%)"),
    ENDERCHEST_TITLE("gui-title.enderchest", "&8%player% のエンダーチェスト (%date%)");

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss");
    private static FileConfiguration MESSAGE_CONFIG;

    private final String key;
    private final String def;

    private String edited = null;

    Message(@NotNull String key, @NotNull String def) {
        this.key = key;
        this.def = def;
    }

    public static void setMessageConfig(FileConfiguration config) {
        MESSAGE_CONFIG = config;
    }

    @NotNull
    public static String colorize(@NotNull String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    @NotNull
    public String getString() {
        if (MESSAGE_CONFIG != null && MESSAGE_CONFIG.isLoaded()) {
            return MESSAGE_CONFIG.getString(key, def);
        } else {
            return def;
        }
    }

    @NotNull
    public String getColorized() {
        return colorize(getEdited());
    }

    public void send(@NotNull CommandSender sender) {
        sender.sendMessage(colorize(PREFIX.getString() + getEdited()));
    }

    public void send(@NotNull Sender sender) {
        sender.sendMessage(colorize(PREFIX.getString() + getEdited()));
    }

    public Message replaceDate(@NotNull LocalDateTime dateTime) {
        edited = getEdited().replace("%date%", FORMATTER.format(dateTime));

        return this;
    }

    public Message replaceExp(float exp) {
        edited = getEdited().replace("%amount%", String.valueOf(exp));

        return this;
    }

    public Message replaceMoney(double money) {
        edited = getEdited().replace("%amount%", String.valueOf(money));

        return this;
    }

    public Message replacePermission(@NotNull String perm) {
        edited = getEdited().replace("%perm%", perm);

        return this;
    }

    public Message replacePlayer(@NotNull String name) {
        edited = getEdited().replace("%player%", name);

        return this;
    }

    public Message replaceSkill(@NotNull String skillName) {
        edited = getEdited().replace("%skill%", skillName);

        return this;
    }

    public Message replaceSkillXp(int xp) {
        edited = getEdited().replace("%amount%", String.valueOf(xp));

        return this;
    }

    public Message replaceType(@NotNull String type) {
        edited = getEdited().replace("%type%", type);

        return this;
    }

    @Override
    public String toString() {
        return getEdited();
    }

    private String getEdited() {
        if (edited == null) {
            edited = getString();
        }

        return edited;
    }
}
