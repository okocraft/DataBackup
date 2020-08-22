package net.okocraft.databackup.data;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BackupData {

    void save(@NotNull BukkitYaml yaml);

    void rollback(@NotNull Player player);

    void show(@NotNull Player player, @NotNull UUID owner, @NotNull LocalDateTime backupTime);
}
