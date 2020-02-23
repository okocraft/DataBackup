package net.okocraft.databackup.tasks;

import com.github.siroshun09.sirolibrary.bukkitutils.BukkitUtil;
import net.okocraft.databackup.Configuration;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class PlayerBackupTask implements Runnable {

    @Override
    public void run() {
        BukkitUtil.runNextTick(DataBackup.get(), this::backupAllPlayers);
        DataBackup.get().getExecutor().schedule(this, Configuration.get().getBackupInterval(), TimeUnit.MINUTES);
    }

    private void backupAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(this::backup);
    }

    private void backup(Player player) {
        new PlayerData(player).save();
    }
}
