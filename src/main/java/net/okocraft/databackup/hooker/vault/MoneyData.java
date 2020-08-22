package net.okocraft.databackup.hooker.vault;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import net.milkbowl.vault.economy.Economy;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.data.BackupData;
import net.okocraft.databackup.user.UserList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class MoneyData implements BackupData {

    private final static String DATA_NAME = "money";
    private final static Economy ECONOMY = getEconomy();

    private final double money;

    private MoneyData(double money) {
        this.money = money;
    }

    @NotNull
    public static String getName() {
        return DATA_NAME;
    }

    @Contract("_ -> new")
    @NotNull
    public static MoneyData load(@NotNull BukkitYaml yaml) {
        return new MoneyData(yaml.getDouble(DATA_NAME));
    }

    @Contract("_ -> new")
    @NotNull
    public static MoneyData backup(@NotNull Player player) {
        return new MoneyData(ECONOMY.getBalance(player));
    }

    @NotNull
    private static Economy getEconomy() {
        RegisteredServiceProvider<Economy> rsp =
                Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp != null) {
            return rsp.getProvider();
        } else {
            throw new IllegalStateException("Could not get economy.");
        }
    }

    @Override
    public void save(@NotNull BukkitYaml yaml) {
        yaml.set(DATA_NAME, money);
    }

    @Override
    public void rollback(@NotNull Player player) {
        double now = ECONOMY.getBalance(player);
        ECONOMY.withdrawPlayer(player, now);
        ECONOMY.depositPlayer(player, money);
    }

    @Override
    public void show(@NotNull Player player, @NotNull UUID owner, @NotNull LocalDateTime backupTime) {
        Message.COMMAND_SHOW_MONEY
                .replaceDate(backupTime)
                .replaceMoney(money)
                .replacePlayer(UserList.getName(owner))
                .send(player);
    }
}
