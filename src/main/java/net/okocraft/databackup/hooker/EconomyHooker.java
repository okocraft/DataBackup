package net.okocraft.databackup.hooker;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class EconomyHooker {

    public static double getBalance(@NotNull OfflinePlayer player) {
        if (isEconomyEnabled()) {
            return VaultHooker.getBalance(player);
        }
        return 0;
    }

    public static void setBalance(@NotNull OfflinePlayer player, double amount) {
        if (isEconomyEnabled()) {
            VaultHooker.setBalance(player, amount);
        }
    }

    public static boolean isEconomyEnabled() {
        Plugin vault = Bukkit.getPluginManager().getPlugin("Vault");
        return vault != null && vault.isEnabled();
    }
}
