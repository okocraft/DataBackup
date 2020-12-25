package net.okocraft.databackup.command.subcommand;

import com.github.siroshun09.mccommand.bukkit.argument.parser.BukkitParser;
import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.argument.Argument;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.filter.StringFilter;
import com.github.siroshun09.mccommand.common.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.lang.DefaultMessage;
import net.okocraft.databackup.lang.MessageProvider;
import net.okocraft.databackup.lang.Placeholders;
import net.okocraft.databackup.storage.PlayerDataFile;
import net.okocraft.databackup.task.BackupTask;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class BackupCommand extends AbstractCommand {

    private static final String ALL = "all";

    private final DataBackup plugin;

    public BackupCommand(@NotNull DataBackup plugin) {
        super("backup", "databackup.command.backup", Set.of("b"));

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

        if (args.size() < 2) {
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_BACKUP_USAGE, sender);
            return CommandResult.NO_ARGUMENT;
        }

        Argument secondArgument = args.get(1);

        if (secondArgument.get().equalsIgnoreCase(ALL)) {
            plugin.getScheduler().execute(new BackupTask(plugin));
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_BACKUP_ALL, sender);
            return CommandResult.SUCCESS;
        } else {
            return backupPlayer(sender, secondArgument);
        }
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        List<Argument> args = context.getArguments();

        if (args.size() != 2 || !context.getSender().hasPermission(getPermission())) {
            return Collections.emptyList();
        }

        var secondArgument = args.get(1).get();
        var filter = StringFilter.startsWithIgnoreCase(secondArgument);

        var result =
                plugin.getServer().getOnlinePlayers()
                        .stream()
                        .map(HumanEntity::getName)
                        .filter(filter)
                        .sorted()
                        .collect(Collectors.toList());

        result.add("all");

        return result;
    }

    @NotNull
    private CommandResult backupPlayer(@NotNull Sender sender, @NotNull Argument argument) {
        Optional<Player> playerOptional = BukkitParser.PLAYER.parseOptional(argument);

        if (playerOptional.isEmpty()) {
            MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_PLAYER_NOT_FOUND, sender)
                    .replace(Placeholders.PLAYER_NAME, argument)
                    .send(sender);
            return CommandResult.STATE_ERROR;
        }

        Player target = playerOptional.get();
        PlayerDataFile dataFile = plugin.getStorage().createPlayerDataFile(target);

        dataFile.backup(plugin.getDataTypeRegistry().getRegisteredDataType());

        try {
            dataFile.save();
            MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_BACKUP_PLAYER, sender)
                    .replace(Placeholders.PLAYER, target)
                    .send(sender);
            return CommandResult.SUCCESS;
        } catch (Throwable e) {
            plugin.getLogger().log(Level.WARNING, "Could not back up player (" + target.getName() + ")", e);
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_BACKUP_FAILURE, sender);
            return CommandResult.STATE_ERROR;
        }
    }
}
