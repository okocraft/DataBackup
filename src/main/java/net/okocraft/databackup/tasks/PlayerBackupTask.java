package net.okocraft.databackup.tasks;

import com.github.siroshun09.sirolibrary.bukkitutils.BukkitUtil;
import net.okocraft.databackup.Configuration;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class PlayerBackupTask implements Runnable {

    private int count;
    private int completed;

    @Override
    public void run() {
        DataBackup.get().getLogger().info("Backup task starting...");
        int delay = 1;
        completed = 1;

        for (Player player : Bukkit.getOnlinePlayers()) {
            BukkitUtil.runLater(DataBackup.get(), () -> backup(player), delay);
            delay++;
        }

        count = delay;
        DataBackup.get().getExecutor().schedule(this, Configuration.get().getBackupInterval(), TimeUnit.MINUTES);
    }

    private void backup(Player player) {
        new PlayerData(player).save();
        completed++;

        if (count == completed) {
            DataBackup.get().getLogger().info("Backup task completed.");
        }
    }
}
