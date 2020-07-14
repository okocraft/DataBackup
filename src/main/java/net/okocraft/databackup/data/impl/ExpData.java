package net.okocraft.databackup.data.impl;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.data.BackupData;
import net.okocraft.databackup.user.UserList;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class ExpData implements BackupData {

    private final static String DATA_NAME = "xp";

    private final float exp;

    private ExpData(float exp) {
        this.exp = exp;
    }

    @NotNull
    public static String getName() {
        return DATA_NAME;
    }

    @Contract("_ -> new")
    @NotNull
    public static ExpData load(@NotNull BukkitYaml yaml) {
        return new ExpData((float) yaml.getDouble(DATA_NAME));
    }

    @Contract("_ -> new")
    @NotNull
    public static ExpData backup(@NotNull Player player) {
        return new ExpData(player.getExp());
    }

    @Override
    public void save(@NotNull BukkitYaml yaml) {
        yaml.set(DATA_NAME, exp);
    }

    @Override
    public void rollback(@NotNull Player player) {
        player.setExp(exp);
    }

    @Override
    public void show(@NotNull Player player, @NotNull UUID owner, @NotNull LocalDateTime backupTime) {
        Message.COMMAND_SHOW_EXP
                .replaceDate(backupTime)
                .replaceExp(exp)
                .replacePlayer(UserList.getName(owner))
                .send(player);
    }
}
