package net.okocraft.databackup.command.subcommand;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import com.github.siroshun09.mccommand.bukkit.argument.parser.BukkitParser;
import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.argument.Argument;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.data.DataType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
            Message.COMMAND_NO_PERMISSION.replacePermission(getPermission()).send(sender);
            return CommandResult.NO_PERMISSION;
        }

        List<Argument> args = context.getArguments();

        if (args.size() < 4) {
            Message.COMMAND_ROLLBACK_USAGE.send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Argument secondArgument = args.get(1);
        Optional<DataType> type = plugin.getStorage().getDataType(secondArgument.get());

        if (type.isEmpty()) {
            Message.COMMAND_INVALID_DATA_TYPE.replaceType(secondArgument.get()).send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Argument thirdArgument = args.get(2);
        Player target = BukkitParser.PLAYER.parse(thirdArgument);

        if (target == null) {
            Message.COMMAND_PLAYER_NOT_FOUND.replacePlayer(thirdArgument.get()).send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Argument fourthArgument = args.get(3);
        Path filePath = plugin.getStorage().createFilePath(target, fourthArgument.get());

        if (!Files.exists(filePath)) {
            Message.COMMAND_BACKUP_NOT_FOUND.send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        BukkitYaml yaml = new BukkitYaml(filePath);
        type.get().load(yaml).rollback(target);
        LocalDateTime dateTime = plugin.getStorage().getDateTime(yaml);

        Message.COMMAND_ROLLBACK_SENDER
                .replacePlayer(target.getName())
                .replaceType(type.get().getName())
                .replaceDate(dateTime)
                .send(sender);

        if (!target.getUniqueId().equals(sender.getUUID())) {
            Message.COMMAND_ROLLBACK_TARGET
                    .replaceType(type.get().getName())
                    .replaceDate(dateTime)
                    .send(target);
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
            Argument secondArgument = args.get(1);

            return StringUtil.copyPartialMatches(
                    secondArgument.get(),
                    plugin.getStorage().getDataListAsString(),
                    new ArrayList<>()
            );
        }

        if (args.size() == 3) {
            Argument thirdArgument = args.get(2);

            return StringUtil.copyPartialMatches(
                    thirdArgument.get(),
                    plugin.getServer().getOnlinePlayers().stream()
                            .map(HumanEntity::getName)
                            .collect(Collectors.toUnmodifiableList()),
                    new ArrayList<>()
            );
        }

        if (args.size() == 4) {
            Argument thirdArgument = args.get(2);
            Player target = BukkitParser.PLAYER.parse(thirdArgument);

            if (target != null) {
                Argument fourthArgument = args.get(3);

                return StringUtil.copyPartialMatches(
                        fourthArgument.get(),
                        plugin.getStorage().getFileListAsString(target.getUniqueId()),
                        new ArrayList<>()
                );
            }
        }

        return Collections.emptyList();
    }
}
