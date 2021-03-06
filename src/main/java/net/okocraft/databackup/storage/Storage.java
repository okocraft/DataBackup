package net.okocraft.databackup.storage;

import com.github.siroshun09.configapi.bukkit.BukkitYamlFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.okocraft.databackup.data.impl.BackupTimeValue;
import net.okocraft.databackup.data.impl.UUIDValue;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Storage {

    private static final DateTimeFormatter FILENAME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    private final Path rootDir;
    private final Cache<Path, PlayerDataFile> cache =
            CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    private final Set<UUID> backedUpPlayers = new HashSet<>();

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

        getPlayerDirectories();
    }

    public void clearCache(boolean reloadPlayers) {
        cache.invalidateAll();

        if (reloadPlayers) {
            getPlayerDirectories();
        }
    }

    public Path getRootDirectory() {
        return rootDir;
    }

    public @NotNull Set<UUID> getBackedUpPlayers() {
        return backedUpPlayers;
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
        var filePath = getPlayerDirectory(owner).resolve(createFileName());
        var parent = filePath.getParent();

        if (!Files.exists(parent)) {
            backedUpPlayers.add(owner);
        }

        var dataFile = new PlayerDataFile(owner, BukkitYamlFactory.loadUnsafe(filePath), 0);

        cache.put(filePath, dataFile);

        return dataFile;
    }

    public @NotNull PlayerDataFile loadPlayerDataFile(@NotNull Path path) {
        var dataFileFromCache = cache.getIfPresent(path);

        if (dataFileFromCache != null) {
            return dataFileFromCache;
        }

        var yaml = BukkitYamlFactory.loadUnsafe(path);
        var owner = UUIDValue.INSTANCE.getValue(yaml);
        var backupTime = BackupTimeValue.INSTANCE.getValue(yaml);

        var dataFile = new PlayerDataFile(owner, yaml, backupTime);

        cache.put(path, dataFile);

        return dataFile;
    }

    public @NotNull Stream<Path> getPlayerDataYamlFiles(@NotNull UUID uuid) {
        var dir = getPlayerDirectory(uuid);
        if (Files.isDirectory(dir)) {
            try {
                return Files.list(dir)
                        .filter(Files::isRegularFile)
                        .filter(Files::isReadable)
                        .filter(s -> s.toString().endsWith(".yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Stream.empty();
    }

    private void getPlayerDirectories() {
        try {
            Files.list(rootDir)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .map(this::parseUUID)
                    .filter(Objects::nonNull)
                    .forEach(backedUpPlayers::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private @Nullable UUID parseUUID(@NotNull String strUUID) {
        try {
            return UUID.fromString(strUUID);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private @NotNull String createFileName() {
        return FILENAME_FORMAT.format(LocalDateTime.now()) + ".yml";
    }

}
