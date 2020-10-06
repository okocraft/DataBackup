package net.okocraft.databackup.command.subcommand;

import com.github.siroshun09.mccommand.bukkit.argument.parser.BukkitParser;
import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.argument.Argument;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.task.BackupTask;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            Message.COMMAND_NO_PERMISSION.replacePermission(getPermission()).send(sender);
            return CommandResult.NO_PERMISSION;
        }

        List<Argument> args = context.getArguments();

        if (args.size() < 2) {
            Message.COMMAND_BACKUP_USAGE.send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Argument secondArgument = args.get(1);

        if (secondArgument.get().equalsIgnoreCase(ALL)) {
            plugin.getScheduler().execute(new BackupTask(plugin));
            Message.COMMAND_BACKUP_ALL.send(sender);
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

        List<String> result = new LinkedList<>();

        result.add("all");
        plugin.getServer().getOnlinePlayers().stream().map(HumanEntity::getName).forEach(result::add);

        Argument secondArgument = args.get(1);

        return StringUtil.copyPartialMatches(secondArgument.get(), result, new ArrayList<>());
    }

    @NotNull
    private CommandResult backupPlayer(@NotNull Sender sender, @NotNull Argument argument) {
        Optional<Player> playerOptional = BukkitParser.PLAYER.parseOptional(argument);

        if (playerOptional.isEmpty()) {
            Message.COMMAND_PLAYER_NOT_FOUND.replacePlayer(argument.get()).send(sender);
            return CommandResult.STATE_ERROR;
        }

        Player target = playerOptional.get();
        boolean success = plugin.getStorage().backup(target);

        if (success) {
            Message.COMMAND_BACKUP_PLAYER.replacePlayer(target.getName()).send(sender);
            return CommandResult.SUCCESS;
        } else {
            Message.COMMAND_BACKUP_FAILURE.send(sender);
            return CommandResult.STATE_ERROR;
        }
    }
}
