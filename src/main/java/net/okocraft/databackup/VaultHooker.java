package net.okocraft.databackup;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class VaultHooker {

    private Economy economy;

    VaultHooker() {
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            economy = rsp.getProvider();
        }
    }

    public double getBalance(@NotNull Player player) {
        if (isEnabledEconomy()) {
            return economy.getBalance(player);
        }
        return 0;
    }

    public void setBalance(@NotNull Player player, double amount) {
        if (isEnabledEconomy()) {
            economy.depositPlayer(player, amount);
        }
    }

    public boolean isEnabledEconomy() {
        return economy != null;
    }
}
