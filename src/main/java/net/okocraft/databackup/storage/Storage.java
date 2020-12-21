package net.okocraft.databackup.storage;

import com.github.siroshun09.configapi.bukkit.BukkitYamlFactory;
import net.okocraft.databackup.data.impl.BackupTimeValue;
import net.okocraft.databackup.data.impl.UUIDValue;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

public class Storage {

    private final Path rootDir;

    public Storage(@NotNull Path rootDir) {
        this.rootDir = rootDir;
    }

    public void setup() throws Throwable {
        if (Files.exists(rootDir)) {
            if (!Files.isDirectory(rootDir)) {
                throw new IllegalStateException(rootDir.toString() + " is not directory.");
            }
        } else {
            Files.createDirectories(rootDir);
        }
    }

    public Path getRootDirectory() {
        return rootDir;
    }

    public @NotNull Path getPlayerDirectory(@NotNull UUID uuid) {
        return rootDir.resolve(uuid.toString());
    }

    public @NotNull Path getPlayerDirectory(@NotNull OfflinePlayer player) {
        return getPlayerDirectory(player.getUniqueId());
    }

    public @NotNull PlayerDataFile createPlayerDataFile(@NotNull OfflinePlayer player) {
        return createPlayerDataFile(player.getUniqueId());
    }

    public @NotNull PlayerDataFile createPlayerDataFile(@NotNull UUID owner) {
        var filePath = getPlayerDirectory(owner).resolve(Instant.now().toEpochMilli() + ".yml");
        return new PlayerDataFile(owner, BukkitYamlFactory.loadUnsafe(filePath), 0);
    }

    public @NotNull PlayerDataFile loadPlayerDataFile(@NotNull Path path) {
        var yaml = BukkitYamlFactory.loadUnsafe(path);
        var owner = UUIDValue.INSTANCE.getValue(yaml);
        var backupTime = BackupTimeValue.INSTANCE.getValue(yaml);

        return new PlayerDataFile(owner, yaml, backupTime);
    }

    public @NotNull Stream<Path> getPlayerDataYamlFiles(@NotNull UUID uuid) {
        try {
            return Files.list(getPlayerDirectory(uuid))
                    .filter(Files::isRegularFile)
                    .filter(Files::isReadable)
                    .filter(s -> s.toString().endsWith(".yml"));
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
        }
    }
}
