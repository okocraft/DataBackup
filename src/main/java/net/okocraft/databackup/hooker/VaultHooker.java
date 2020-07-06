package net.okocraft.databackup.hooker;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public final class VaultHooker {

    private static Economy ECONOMY;

    static double getBalance(@NotNull OfflinePlayer player) {
        return getEconomy().getBalance(player);
    }

    static void setBalance(@NotNull OfflinePlayer player, double amount) {
        getEconomy().depositPlayer(player, amount);
    }

    @NotNull
    static Economy getEconomy() {
        if (ECONOMY == null) {
            setEconomy();
        }
        return ECONOMY;
    }

    static void setEconomy() {
        RegisteredServiceProvider<Economy> rsp =
                Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp != null) {
            ECONOMY = rsp.getProvider();
        } else {
            throw new IllegalStateException("Could not get economy.");
        }
    }
}
