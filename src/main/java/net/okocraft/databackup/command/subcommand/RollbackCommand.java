package net.okocraft.databackup.command.subcommand;

import com.github.siroshun09.mccommand.bukkit.argument.parser.BukkitParser;
import com.github.siroshun09.mccommand.bukkit.sender.BukkitSender;
import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.argument.Argument;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.filter.StringFilter;
import com.github.siroshun09.mccommand.common.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.data.DataType;
import net.okocraft.databackup.data.impl.BackupTimeValue;
import net.okocraft.databackup.lang.DefaultMessage;
import net.okocraft.databackup.lang.MessageProvider;
import net.okocraft.databackup.lang.Placeholders;
import net.okocraft.databackup.storage.PlayerDataFile;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RollbackCommand extends AbstractCommand {

    private final DataBackup plugin;

    public RollbackCommand(@NotNull DataBackup plugin) {
        super("rollback", "databackup.command.rollback", Set.of("r", "rb"));

        this.plugin = plugin;
    }

    @Override
    public @NotNull CommandResult onExecution(@NotNull CommandContext context) {
        Sender sender = context.getSender();

        if (!sender.hasPermission(getPermission())) {
            MessageProvider.sendNoPermission(sender, getPermission());
            return CommandResult.NO_PERMISSION;
        }

        List<Argument> args = context.getArguments();

        if (args.size() < 4) {
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_ROLLBACK_USAGE, sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Argument secondArgument = args.get(1);
        Optional<DataType<?>> type = plugin.getDataTypeRegistry().get(secondArgument);

        if (type.isEmpty()) {
            MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_INVALID_DATA_TYPE, sender)
                    .replace(Placeholders.DATA_TYPE_NAME, secondArgument)
                    .send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Argument thirdArgument = args.get(2);
        Player target = BukkitParser.PLAYER.parse(thirdArgument);

        if (target == null) {
            MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_PLAYER_NOT_FOUND, sender)
                    .replace(Placeholders.PLAYER_NAME, thirdArgument)
                    .send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Argument fourthArgument = args.get(3);
        Path filePath = plugin.getStorage().getPlayerDirectory(target).resolve(fourthArgument.get());

        if (!Files.exists(filePath)) {
            MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_PLAYER_NOT_FOUND, sender)
                    .replace(Placeholders.PLAYER_NAME, thirdArgument)
                    .send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        PlayerDataFile dataFile = plugin.getStorage().loadPlayerDataFile(filePath);

        try {
            dataFile.rollback(type.get());
        } catch (Throwable e) {
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_ROLLBACK_FAILURE, sender);
            return CommandResult.EXCEPTION_OCCURRED;
        }

        LocalDateTime dateTime = BackupTimeValue.toLocalDateTime(dataFile.getBackupTime());

        MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_ROLLBACK_SENDER, sender)
                .replace(Placeholders.PLAYER, target)
                .replace(Placeholders.DATA_TYPE, type.get())
                .replace(Placeholders.DATE, dateTime)
                .send(sender);

        if (!target.getUniqueId().equals(sender.getUUID())) {
            MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_ROLLBACK_TARGET, sender)
                    .replace(Placeholders.DATA_TYPE, type.get())
                    .replace(Placeholders.DATE, dateTime)
                    .send(new BukkitSender(target));
        }

        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        List<Argument> args = context.getArguments();

        if (args.isEmpty() || !context.getSender().hasPermission(getPermission())) {
            return Collections.emptyList();
        }

        if (args.size() == 2) {
            var secondArgument = args.get(1).get();
            var filter = StringFilter.startsWithIgnoreCase(secondArgument);

            return plugin.getDataTypeRegistry().getRegisteredDataType()
                    .stream()
                    .map(DataType::getName)
                    .filter(filter)
                    .sorted()
                    .collect(Collectors.toList());
        }

        if (args.size() == 3) {
            var thirdArgument = args.get(2).get();
            var filter = StringFilter.startsWithIgnoreCase(thirdArgument);

            return plugin.getServer().getOnlinePlayers()
                    .stream()
                    .map(HumanEntity::getName)
                    .filter(filter)
                    .sorted()
                    .collect(Collectors.toUnmodifiableList());
        }

        if (args.size() == 4) {
            var thirdArgument = args.get(2);
            var target = BukkitParser.PLAYER.parse(thirdArgument);

            if (target != null) {
                var fourthArgument = args.get(3).get();
                var filter = StringFilter.startsWithIgnoreCase(fourthArgument);

                return plugin.getStorage().getPlayerDataYamlFiles(target.getUniqueId())
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .filter(filter)
                        .sorted()
                        .collect(Collectors.toUnmodifiableList());
            }
        }

        return Collections.emptyList();
    }
}
