package net.okocraft.databackup.data;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import com.github.siroshun09.mccommand.bukkit.sender.BukkitSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface DataType<T> {

    @NotNull String getName();

    @NotNull Function<Player, BackupData<T>> backup();

    @NotNull BiConsumer<BackupData<T>, Player> rollback();

    @NotNull BiConsumer<BackupData<T>, BukkitSender> show();

    @NotNull Function<BukkitYaml, BackupData<T>> load();

    @NotNull BiConsumer<BackupData<T>, BukkitYaml> save();
}
