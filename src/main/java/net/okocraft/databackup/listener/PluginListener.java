package net.okocraft.databackup.listener;

import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.data.DataType;
import net.okocraft.databackup.external.mcmmo.SkillXPData;
import net.okocraft.databackup.external.vault.MoneyData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class PluginListener implements Listener {

    private final DataBackup plugin;

    public PluginListener(@NotNull DataBackup plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEnable(@NotNull PluginEnableEvent e) {
        switch (e.getPlugin().getName().toLowerCase(Locale.ROOT)) {
            case "vault":
                plugin.hookVault();
                return;
            case "mcmmo":
                plugin.hookMcMMO();
                return;
            default:
        }
    }

    @EventHandler
    public void onDisable(@NotNull PluginDisableEvent e) {
        switch (e.getPlugin().getName().toLowerCase(Locale.ROOT)) {
            case "vault":
                for (DataType<?> type : plugin.getDataTypeRegistry().getRegisteredDataType()) {
                    if (type instanceof MoneyData) {
                        plugin.getDataTypeRegistry().unregisterDataType(type);
                    }
                }
                return;
            case "mcmmo":
                for (DataType<?> type : plugin.getDataTypeRegistry().getRegisteredDataType()) {
                    if (type instanceof SkillXPData) {
                        plugin.getDataTypeRegistry().unregisterDataType(type);
                    }
                }
                return;
            default:
        }
    }
}
