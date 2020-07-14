package net.okocraft.databackup.data;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class DataType {

    private final String name;
    private final Function<BukkitYaml, BackupData> loadFunction;
    private final Function<Player, BackupData> backupFunction;

    public DataType(@NotNull String name,
                    @NotNull Function<BukkitYaml, BackupData> loadFunction,
                    @NotNull Function<Player, BackupData> backupFunction) {
        this.name = name;
        this.loadFunction = loadFunction;
        this.backupFunction = backupFunction;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public BackupData load(@NotNull BukkitYaml yaml) {
        return loadFunction.apply(yaml);
    }

    @NotNull
    public BackupData backup(@NotNull Player player) {
        return backupFunction.apply(player);
    }
}
