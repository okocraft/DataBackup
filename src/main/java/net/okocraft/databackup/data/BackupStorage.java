package net.okocraft.databackup.data;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.data.impl.EnderChestData;
import net.okocraft.databackup.data.impl.ExpData;
import net.okocraft.databackup.data.impl.InventoryData;
import net.okocraft.databackup.hooker.vault.MoneyData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BackupStorage {

    private final static String DATETIME_PATH = "datetime";

    private final Set<DataType> dataType = new HashSet<>();

    private final Path playerDir;

    public BackupStorage(@NotNull Path dirPath) {
        this.playerDir = dirPath.resolve("playerdata");

        registerDataType(EnderChestData.getName(), EnderChestData::load, EnderChestData::backup);
        registerDataType(ExpData.getName(), ExpData::load, ExpData::backup);
        registerDataType(InventoryData.getName(), InventoryData::load, InventoryData::backup);
        registerDataType(MoneyData.getName(), MoneyData::load, MoneyData::backup);
    }

    public boolean registerDataType(@NotNull DataType type) {
        return dataType.add(type);
    }

    public boolean registerDataType(@NotNull String name,
                                    @NotNull Function<BukkitYaml, BackupData> loadFunction,
                                    @NotNull Function<Player, BackupData> backupFunction) {
        return registerDataType(new DataType(name, loadFunction, backupFunction));
    }

    @NotNull
    public List<String> getDataList() {
        return dataType.stream().map(DataType::getName).collect(Collectors.toUnmodifiableList());
    }

    @NotNull
    public Optional<DataType> getDataType(@NotNull String name) {
        return dataType.stream().filter(d -> d.getName().equalsIgnoreCase(name)).findFirst();
    }

    public boolean backup(@NotNull Player player) {
        BukkitYaml yaml = new BukkitYaml(createFilePath(player));
        dataType.stream().map(d -> d.backup(player)).forEach(d -> d.save(yaml));
        yaml.set(DATETIME_PATH, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()));
        return yaml.save();
    }

    public boolean show(@NotNull Player player, @NotNull UUID owner, @NotNull DataType dataType, @NotNull String fileName) {
        Path filePath = playerDir.resolve(owner.toString()).resolve(fileName);

        if (Files.exists(filePath)) {
            BukkitYaml yaml = new BukkitYaml(filePath);

            LocalDateTime dateTime =
                    LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(yaml.getString(DATETIME_PATH)));

            dataType.load(yaml).show(player, owner, dateTime);
            return true;
        } else {
            Message.COMMAND_BACKUP_NOT_FOUND.send(player);
            return false;
        }
    }

    @NotNull
    public List<String> getFileList(@NotNull UUID uuid) {
        try {
            return Files.list(getPlayerDirectory(uuid))
                    .filter(Files::isReadable)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".yml"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toUnmodifiableList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private Path getPlayerDirectory(@NotNull UUID uuid) {
        return playerDir.resolve(uuid.toString());
    }

    @NotNull
    private Path createFilePath(@NotNull Player player) {
        return getPlayerDirectory(player.getUniqueId()).resolve(LocalDateTime.now().toString() + ".yml");
    }
}
