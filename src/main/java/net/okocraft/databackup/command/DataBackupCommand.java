package net.okocraft.databackup.command;

import com.github.siroshun09.asynctabcompleter.PaperChecker;
import com.github.siroshun09.command.Command;
import com.github.siroshun09.command.CommandResult;
import com.github.siroshun09.command.argument.ArgumentList;
import com.github.siroshun09.command.bukkit.BukkitCommand;
import com.github.siroshun09.command.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.command.sub.BackupCommand;
import net.okocraft.databackup.command.sub.CleanCommand;
import net.okocraft.databackup.command.sub.RollbackCommand;
import net.okocraft.databackup.command.sub.ShowCommand;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class DataBackupCommand extends BukkitCommand {

    private final Set<Command> subCommands;

    public DataBackupCommand(@NotNull DataBackup plugin) {
        subCommands = Set.of(
                new BackupCommand(plugin),
                new CleanCommand(plugin),
                new RollbackCommand(plugin),
                new ShowCommand(plugin)
        );

        if (PaperChecker.isPaper()) {
            plugin.getServer().getPluginManager().registerEvents(new AsyncTabCompletionListener(this), plugin);
        }
    }

    @Override
    @NotNull
    public String getName() {
        return "databackup";
    }

    @Override
    @NotNull
    public String getPermission() {
        return "databackup.command";
    }

    @Override
    @NotNull
    @Unmodifiable
    public Set<String> getAliases() {
        return Set.of("dbackup", "db");
    }

    @Override
    @NotNull
    public String getUsage() {
        return Message.COMMAND_USAGE.getColorized();
    }

    @Override
    @NotNull
    @Unmodifiable
    public Set<Command> getSubCommands() {
        return subCommands;
    }

    @Override
    @NotNull
    public CommandResult execute(@NotNull Sender sender, @NotNull String label, @NotNull ArgumentList args) {
        if (!sender.hasPermission(getPermission())) {
            Message.COMMAND_NO_PERMISSION.replacePermission(getPermission()).send(sender);
            return CommandResult.NO_PERMISSION;
        }

        if (args.size() < 1) {
            Message.COMMAND_USAGE.send(sender);
            return CommandResult.NO_ARGUMENT;
        }

        Optional<Command> cmd = getCommand(args.get(0));
        if (cmd.isPresent()) {
            return cmd.get().execute(sender, label, args);
        } else {
            subCommands.forEach(c -> sender.sendMessage(c.getUsage()));
            return CommandResult.INVALID_ARGUMENTS;
        }
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull Sender sender, @NotNull String label, @NotNull ArgumentList args) {
        if (!sender.hasPermission(getPermission())) {
            return Collections.emptyList();
        }

        if (args.size() == 1) {
            return StringUtil.copyPartialMatches(
                    args.get(0),
                    subCommands.stream().map(Command::getName).collect(Collectors.toList()),
                    new LinkedList<>());
        }

        Optional<Command> cmd = getCommand(args.get(0));
        return cmd.map(c -> c.tabComplete(sender, label, args)).orElse(Collections.emptyList());
    }

    private Optional<Command> getCommand(@NotNull String str) {
        Optional<Command> cmd = subCommands.stream().filter(c -> c.getName().equalsIgnoreCase(str)).findFirst();
        if (cmd.isPresent()) {
            return cmd;
        }

        return subCommands.stream()
                .filter(c -> c.getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(str)))
                .findFirst();
    }
}
