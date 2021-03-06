package net.okocraft.databackup.command;

import com.github.siroshun09.mccommand.bukkit.BukkitCommandFactory;
import com.github.siroshun09.mccommand.bukkit.paper.AsyncTabCompleteListener;
import com.github.siroshun09.mccommand.bukkit.paper.PaperChecker;
import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.Command;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.SubCommandHolder;
import com.github.siroshun09.mccommand.common.argument.Argument;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.filter.StringFilter;
import com.github.siroshun09.mccommand.common.sender.Sender;
import com.github.siroshun09.mcmessage.MessageReceiver;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.command.subcommand.BackupCommand;
import net.okocraft.databackup.command.subcommand.CleanCommand;
import net.okocraft.databackup.command.subcommand.RollbackCommand;
import net.okocraft.databackup.command.subcommand.SearchCommand;
import net.okocraft.databackup.command.subcommand.ShowCommand;
import net.okocraft.databackup.lang.DefaultMessage;
import net.okocraft.databackup.lang.MessageProvider;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DataBackupCommand extends AbstractCommand {

    private final SubCommandHolder subCommandHolder;

    public DataBackupCommand(@NotNull DataBackup plugin) {
        super("databackup", "databackup.command", Set.of("dbackup", "db"));

        subCommandHolder = SubCommandHolder.of(
                new BackupCommand(plugin),
                new CleanCommand(plugin),
                new RollbackCommand(plugin),
                new SearchCommand(plugin),
                new ShowCommand(plugin)
        );
    }

    @Override
    public @NotNull CommandResult onExecution(@NotNull CommandContext context) {
        Sender sender = context.getSender();

        if (!sender.hasPermission(getPermission())) {
            MessageProvider.sendNoPermission(sender, getPermission());
            return CommandResult.NO_PERMISSION;
        }

        if (context.getArguments().isEmpty()) {
            sendUsage(sender);
            return CommandResult.NO_ARGUMENT;
        }

        Argument firstArgument = context.getArguments().get(0);
        Optional<Command> subCommand = subCommandHolder.searchOptional(firstArgument);

        if (subCommand.isPresent()) {
            return subCommand.get().onExecution(context);
        } else {
            sendUsage(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        var args = context.getArguments();

        if (args.isEmpty() || !context.getSender().hasPermission(getPermission())) {
            return Collections.emptyList();
        }

        var firstArgument = context.getArguments().get(0);

        if (args.size() == 1) {
            var filter = StringFilter.startsWithIgnoreCase(firstArgument.get());

            return subCommandHolder.getSubCommands()
                    .stream()
                    .map(Command::getName)
                    .filter(filter)
                    .sorted()
                    .collect(Collectors.toList());
        } else {
            return subCommandHolder
                    .searchOptional(firstArgument)
                    .map(cmd -> cmd.onTabCompletion(context))
                    .orElse(Collections.emptyList());
        }
    }

    public void register(@NotNull PluginCommand command) {
        BukkitCommandFactory.register(command, this);

        if (PaperChecker.check()) {
            AsyncTabCompleteListener.register(command.getPlugin(), this);
        }
    }

    private void sendUsage(@NotNull MessageReceiver receiver) {
        send(DefaultMessage.COMMAND_USAGE, receiver);
        send(DefaultMessage.COMMAND_BACKUP_USAGE, receiver);
        send(DefaultMessage.COMMAND_CLEAN_USAGE, receiver);
        send(DefaultMessage.COMMAND_ROLLBACK_USAGE, receiver);
        send(DefaultMessage.COMMAND_SEARCH_USAGE, receiver);
        send(DefaultMessage.COMMAND_SHOW_USAGE, receiver);
    }

    private void send(@NotNull DefaultMessage defaultMessage, @NotNull MessageReceiver receiver) {
        MessageProvider.sendMessageWithPrefix(defaultMessage, receiver);
    }
}
