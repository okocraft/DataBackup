package net.okocraft.databackup.task;

import com.github.siroshun09.mccommand.bukkit.sender.BukkitSender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Setting;
import net.okocraft.databackup.data.DataType;
import net.okocraft.databackup.lang.DefaultMessage;
import net.okocraft.databackup.lang.MessageProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class BackupTask implements Runnable {

    private static final long MILLI_SECONDS_TO_WAIT = 10000;

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

        boolean broadcast = Setting.BACKUP_BROADCAST.getValue(plugin.getConfiguration());

        plugin.getLogger().info("Starting backup...");

        if (broadcast) {
            players.stream()
                    .filter(OfflinePlayer::isOnline)
                    .map(BukkitSender::new)
                    .forEach(p -> MessageProvider.sendMessage(DefaultMessage.BACKUP_START, p));
        }

        long start = System.currentTimeMillis();

        var dataTypes = plugin.getDataTypeRegistry().getRegisteredDataType();
        players.forEach(p -> backup(p, dataTypes));

        long finish = System.currentTimeMillis();

        plugin.getLogger().info("Backup task is completed. (" + (finish - start) + "ms)");

        if (broadcast) {
            players.stream()
                    .filter(OfflinePlayer::isOnline)
                    .map(BukkitSender::new)
                    .forEach(p -> MessageProvider.sendMessage(DefaultMessage.BACKUP_FINISH, p));
        }
    }

    private void backup(@NotNull Player player, @NotNull Collection<DataType<?>> dataTypes) {
        var dataFile = plugin.getStorage().createPlayerDataFile(player);
        dataFile.setPlayerCache(player);

        AtomicBoolean waiting = new AtomicBoolean(false);
        AtomicBoolean backedUp = new AtomicBoolean(false);

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            dataFile.backup(dataTypes);
            backedUp.set(true);

            if (waiting.get()) {
                synchronized (waiting) {
                    waiting.notifyAll();
                }
            }
        });

        if (!backedUp.get()) {
            try {
                waiting.set(true);
                synchronized (waiting) {
                    waiting.wait(MILLI_SECONDS_TO_WAIT);
                }
            } catch (InterruptedException e) {
                plugin.getLogger().log(Level.SEVERE, "Could not get player data: " + player.getName(), e);
            }

            waiting.set(false);
        }


        dataFile.save(plugin.getLogger());
    }
}
