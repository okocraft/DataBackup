package net.okocraft.databackup.task;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.data.BackupData;
import net.okocraft.databackup.data.BackupStorage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class BackupTask implements Runnable {

    private final DataBackup plugin;

    public BackupTask(@NotNull DataBackup plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Set<Player> players = Set.copyOf(Bukkit.getOnlinePlayers());

        if (players.isEmpty()) {
            return;
        }

        boolean broadcast = plugin.getConfiguration().isBroadcastMode();

        plugin.getLogger().info("Starting backup...");
        if (broadcast) {
            players.stream().filter(OfflinePlayer::isOnline).forEach(Message.BACKUP_START::send);
        }

        long start = System.currentTimeMillis();

        CompletableFuture.allOf(
                players.stream()
                        .map(p -> CompletableFuture.runAsync(() -> backup(p)))
                        .toArray(CompletableFuture[]::new)
        ).join();

        long finish = System.currentTimeMillis();

        plugin.getLogger().info("Backup task is complete (" + (finish - start) + "ms)");

        if (broadcast) {
            players.stream().filter(OfflinePlayer::isOnline).forEach(Message.BACKUP_FINISH::send);
        }
    }

    private void backup(@NotNull Player player) {
        BackupStorage storage = plugin.getStorage();

        BukkitYaml yaml = new BukkitYaml(storage.createFilePath(player));
        Set<BackupData> result = new HashSet<>();

        plugin.getServer().getScheduler().runTask(plugin, () -> getData(result, player));

        result.forEach(type -> type.save(yaml));

        if (yaml.save()) {
            plugin.debug(player.getName() + " was successfully backed up.");
        } else {
            plugin.getLogger().warning(player.getName() + " backup failed.");
        }
    }

    private void getData(@NotNull Set<BackupData> toCollect, @NotNull Player player) {
        plugin.getStorage().getDataList().stream().map(type -> type.backup(player)).forEach(toCollect::add);
    }
}
