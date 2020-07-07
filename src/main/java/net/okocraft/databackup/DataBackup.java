package net.okocraft.databackup;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public final class DataBackup extends JavaPlugin {

    private Configuration config;
    private Path playerDir;
    private ScheduledExecutorService scheduler;

    @Override
    public void onLoad() {
        config = new Configuration(this);
        playerDir = config.getDestinationDir().resolve("playerdata");
        debug("config.yml loaded.");
        Message.setMessageConfig(new BukkitConfig(this, "message.yml", true));
        debug("message.yml loaded.");

        getLogger().info("DataBackup v" + getDescription().getVersion() + " has been loaded!");
    }

    @Override
    public void onEnable() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void onDisable() {
        config = null;
        Message.setMessageConfig(null);
    }

    @NotNull
    public Configuration getConfiguration() {
        return config;
    }

    @NotNull
    public Path getPlayerDataDir() {
        return playerDir;
    }

    @NotNull
    public Path getNewBackupFile(@NotNull Player player) {
        return playerDir.resolve(player.getUniqueId().toString()).resolve(LocalDateTime.now().toString() + ".yml");
    }

    @NotNull
    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public void debug(@NotNull String log) {
        if (config.isDebugMode()) {
            getLogger().info("Debug: " + log);
        }
    }
}
