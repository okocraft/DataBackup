package net.okocraft.databackup.data.impl;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import com.github.siroshun09.configapi.common.configurable.Configurable;
import com.github.siroshun09.configapi.common.configurable.FloatValue;
import com.github.siroshun09.mccommand.bukkit.sender.BukkitSender;
import net.okocraft.databackup.data.BackupData;
import net.okocraft.databackup.data.DataType;
import net.okocraft.databackup.lang.DefaultMessage;
import net.okocraft.databackup.lang.MessageProvider;
import net.okocraft.databackup.lang.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ExpData implements DataType<Float> {

    private static final FloatValue EXP = Configurable.create("exp", 0.0f);

    @Override
    public @NotNull String getName() {
        return EXP.getKey();
    }

    @Override
    public @NotNull Function<Player, BackupData<Float>> backup() {
        return player -> BackupData.create(player.getUniqueId(), player.getExp());
    }

    @Override
    public @NotNull BiConsumer<BackupData<Float>, Player> rollback() {
        return (expData, player) -> player.setExp(expData.get());
    }

    @Override
    public @NotNull BiConsumer<BackupData<Float>, BukkitSender> show() {
        return (expData, sender) ->
                MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_SHOW_EXP, sender)
                        .replace(Placeholders.EXP, expData.get())
                        .replace(Placeholders.DATE, BackupTimeValue.toLocalDateTime(expData.getBackupTime()))
                        .send(sender);
    }

    @Override
    public @NotNull Function<BukkitYaml, BackupData<Float>> load() {
        return yaml -> BackupData.create(
                UUIDValue.INSTANCE.getValue(yaml),
                BackupTimeValue.INSTANCE.getValue(yaml),
                EXP.getValue(yaml)
        );
    }

    @Override
    public @NotNull BiConsumer<BackupData<Float>, BukkitYaml> save() {
        return (expData, yaml) -> yaml.setValue(EXP, expData.get());
    }
}
