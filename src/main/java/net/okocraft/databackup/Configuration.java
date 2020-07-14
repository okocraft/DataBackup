package net.okocraft.databackup;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Configuration extends BukkitConfig {

    public Configuration(@NotNull DataBackup plugin) {
        super(plugin, "config.yml", true);
    }

    public int getBackupInterval() {
        return getInt("backup.interval", 30);
    }

    public int getBackupPeriod() {
        return getInt("backup.period", 5);
    }

    @NotNull
    public Path getDestinationDir() {
        return Paths.get(getString("backup.destination-directory", "./plugins/databackup/backups"));
    }

    public boolean isDebugMode() {
        return getBoolean("debug", false);
    }

    public boolean isBroadcastMode() {
        return getBoolean("backup.broadcast");
    }
}
