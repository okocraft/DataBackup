package old_ver.tasks;

import com.github.siroshun09.sirolibrary.bukkitutils.BukkitUtil;
import old_ver.Configuration;
import old_ver.DataBackup;
import old_ver.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PlayerBackupTask implements Runnable {

    private int count;
    private int completed;
    private long startTime;

    @Override
    public void run() {
        Set<Player> players = Set.copyOf(Bukkit.getOnlinePlayers());
        if (players.size() == 0) return;

        DataBackup.get().getLogger().info("Backup task is starting...");
        int delay = 1;
        completed = 1;
        startTime = System.currentTimeMillis();

        for (Player player : players) {
            BukkitUtil.runLater(DataBackup.get(), () -> backup(player), delay);
            delay++;
        }

        count = delay;
        DataBackup.get().getExecutor().schedule(new PlayerBackupTask(), Configuration.get().getBackupInterval(), TimeUnit.MINUTES);
    }

    private void backup(Player player) {
        new PlayerData(player).save();
        completed++;
        DataBackup.get().debug("Backed up data: " + player.getName());

        if (count == completed) {
            long took = System.currentTimeMillis() - startTime;
            DataBackup.get().getLogger().info("Backup task was completed. (" + took + "ms)");
        }
    }
}
