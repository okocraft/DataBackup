package net.okocraft.databackup;

import com.github.siroshun09.configapi.common.FileConfiguration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public enum Message {
    ;

    private static FileConfiguration MESSAGE_CONFIG;

    private final String key;
    private final String def;

    Message(@NotNull String key, @NotNull String def) {
        this.key = key;
        this.def = def;
    }

    public static void setMessageConfig(FileConfiguration config) {
        MESSAGE_CONFIG = config;
    }

    public String getMessage() {
        if (MESSAGE_CONFIG != null && MESSAGE_CONFIG.isLoaded()) {
            return MESSAGE_CONFIG.getString(key, def);
        } else {
            return def;
        }
    }

    public void send(@NotNull CommandSender sender) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getMessage()));
    }
}
