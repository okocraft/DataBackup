package net.okocraft.databackup;

import com.github.siroshun09.command.sender.Sender;
import com.github.siroshun09.configapi.common.FileConfiguration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public enum Message {
    PREFIX("prefix", "&8[&6DataBackup&8]&r "),

    COMMAND_USAGE("command.usage", "&b/databackup help"),
    COMMAND_BACKUP_USAGE("command.backup.usage", "&b/db backup <player|all>&8 - &7プレイヤーのデータをバックアップします"),
    COMMAND_BACKUP_ALL("command.backup.all", "&7ログイン中のプレイヤーのデータをバックアップしました。"),
    COMMAND_BACKUP_PLAYER("command.backup.player", "&b%player% &7のデータをバックアップしました。"),
    COMMAND_BACKUP_FAILURE("command.backup.failure", "&cバックアップに失敗しました。コンソールを確認してください。"),

    COMMAND_NO_PERMISSION("command.no-permission", "&c権限がありません: "),
    COMMAND_PLAYER_NOT_FOUND("command.player-not-found", "&cプレイヤー %player% は見つかりませんでした。"),

    INVENTORY_TITLE("gui-title.inventory", "&8%player% のインベントリ (%date%)"),
    ENDERCHEST_TITLE("gui-title.enderchest", "&8%player% のエンダーチェスト (%date%)");

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

    public String getString() {
        if (MESSAGE_CONFIG != null && MESSAGE_CONFIG.isLoaded()) {
            return MESSAGE_CONFIG.getString(key, def);
        } else {
            return def;
        }
    }

    public void send(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX.getString() + getEdited()));
    }

    public void send(@NotNull Sender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX.getString() + getEdited()));
    }

    public Message replacePermission(@NotNull String perm) {
        edited = getEdited().replace("%perm%", perm);

        return this;
    }

    public Message replacePlayer(@NotNull String name) {
        edited = getEdited().replace("%player%", name);

        return this;
    }

    private String getEdited() {
        if (edited == null) {
            edited = getString();
        }

        return edited;
    }
}
