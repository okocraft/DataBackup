package net.okocraft.databackup.storage;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import com.github.siroshun09.mccommand.bukkit.sender.BukkitSender;
import net.okocraft.databackup.data.BackupData;
import net.okocraft.databackup.data.DataType;
import net.okocraft.databackup.data.impl.BackupTimeValue;
import net.okocraft.databackup.data.impl.UUIDValue;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PlayerDataFile {

    private final UUID owner;
    private final BukkitYaml yaml;
    private final Map<DataType, BackupData> dataMap = new HashMap<>();

    private Player playerCache;
    private long backupTime;
    private boolean loaded;

    PlayerDataFile(@NotNull UUID owner, @NotNull BukkitYaml yaml, long backupTime) {
        this.owner = owner;
        this.yaml = yaml;
        this.backupTime = backupTime;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void backup(@NotNull Collection<DataType<?>> dataTypes) {
        var player = getPlayerOrThrow();

        for (DataType dataType : dataTypes) {
            var data = (BackupData) dataType.backup().apply(player);
            dataMap.put(dataType, data);
        }

        loaded = true;
        backupTime = Instant.now().toEpochMilli();
    }

    public void rollback(@NotNull DataType dataType) {
        var player = getPlayerOrThrow();

        var data = getDataOrLoad(dataType);
        dataType.rollback().accept(data, player);
    }

    public void show(@NotNull DataType dataType, @NotNull BukkitSender viewer) {
        var data = getDataOrLoad(dataType);
        dataType.show().accept(data, viewer);
    }

    public void loadAll(@NotNull Collection<DataType<?>> dataTypes) {
        for (DataType dataType : dataTypes) {
            var data = (BackupData) dataType.load().apply(yaml);
            dataMap.put(dataType, data);
        }
        loaded = true;
    }

    public void save() throws IOException {
        for (DataType dataType : dataMap.keySet()) {
            var data = dataMap.get(dataType);

            if (data != null) {
                dataType.save().accept(data, yaml);
            }
        }

        yaml.setValue(BackupTimeValue.INSTANCE, backupTime);
        yaml.setValue(UUIDValue.INSTANCE, owner);

        yaml.save();
    }

    public void save(@NotNull Logger logger) {
        try {
            save();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not save " + yaml.getPath().getFileName().toString(), e);
        }
    }

    public @NotNull Map<DataType, BackupData> getDataMap() {
        return dataMap;
    }

    public void setPlayerCache(@NotNull Player playerCache) {
        this.playerCache = playerCache;
    }

    public long getBackupTime() {
        return backupTime;
    }

    @NotNull
    private Player getPlayerOrThrow() {
        if (playerCache == null) {
            var player = Bukkit.getPlayer(owner);

            if (player != null) {
                playerCache = player;
            } else {
                throw new IllegalStateException("The player is offline. (uuid: " + owner.toString() + ")");
            }
        }

        return playerCache;
    }

    private @NotNull BackupData<?> getDataOrLoad(@NotNull DataType<?> dataType) {
        var data = dataMap.get(dataType);

        if (data == null) {
            data = dataType.load().apply(yaml);
            dataMap.put(dataType, data);
        }

        return data;
    }
}
