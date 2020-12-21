package net.okocraft.databackup.external.vault;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import com.github.siroshun09.configapi.common.configurable.Configurable;
import com.github.siroshun09.configapi.common.configurable.DoubleValue;
import com.github.siroshun09.mccommand.bukkit.sender.BukkitSender;
import net.milkbowl.vault.economy.Economy;
import net.okocraft.databackup.data.BackupData;
import net.okocraft.databackup.data.impl.BackupTimeValue;
import net.okocraft.databackup.data.DataType;
import net.okocraft.databackup.data.impl.UUIDValue;
import net.okocraft.databackup.lang.DefaultMessage;
import net.okocraft.databackup.lang.MessageProvider;
import net.okocraft.databackup.lang.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class MoneyData implements DataType<Double> {

    private static final DoubleValue MONEY_DATA = Configurable.create("money", 0.0);

    private final Economy economy;

    public MoneyData(@NotNull Economy economy) {
        this.economy = economy;
    }

    @Override
    public @NotNull String getName() {
        return MONEY_DATA.getKey();
    }

    @Override
    public @NotNull Function<Player, BackupData<Double>> backup() {
        return player ->
                BackupData.create(
                        player.getUniqueId(),
                        economy.getBalance(player)
                );
    }

    @Override
    public @NotNull BiConsumer<BackupData<Double>, Player> rollback() {
        return (moneyData, player) -> {
            economy.withdrawPlayer(player, economy.getBalance(player));
            economy.depositPlayer(player, moneyData.get());
        };
    }

    @Override
    public @NotNull BiConsumer<BackupData<Double>, BukkitSender> show() {
        return (moneyData, sender) ->
                MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_SHOW_MONEY, sender)
                        .replace(Placeholders.MONEY, moneyData.get())
                        .replace(Placeholders.DATE, BackupTimeValue.toLocalDateTime(moneyData.getBackupTime()))
                        .send(sender);
    }

    @Override
    public @NotNull Function<BukkitYaml, BackupData<Double>> load() {
        return yaml ->
                BackupData.create(
                        UUIDValue.INSTANCE.getValue(yaml),
                        BackupTimeValue.INSTANCE.getValue(yaml),
                        MONEY_DATA.getValue(yaml)
                );
    }

    @Override
    public @NotNull BiConsumer<BackupData<Double>, BukkitYaml> save() {
        return (moneyData, yaml) -> yaml.setValue(MONEY_DATA, moneyData.get());
    }
}
