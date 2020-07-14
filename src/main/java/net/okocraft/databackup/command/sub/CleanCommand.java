package net.okocraft.databackup.command.sub;

import com.github.siroshun09.command.Command;
import com.github.siroshun09.command.CommandResult;
import com.github.siroshun09.command.argument.ArgumentList;
import com.github.siroshun09.command.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.task.FileCheckTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CleanCommand implements Command {

    private final DataBackup plugin;

    public CleanCommand(@NotNull DataBackup plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getName() {
        return "clean";
    }

    @Override
    @NotNull
    public String getPermission() {
        return "databackup.command.clean";
    }

    @Override
    @NotNull
    @Unmodifiable
    public Set<String> getAliases() {
        return Set.of("c");
    }

    @Override
    @NotNull
    public String getUsage() {
        return Message.COMMAND_CLEAN_USAGE.getString();
    }

    @Override
    @NotNull
    @Unmodifiable
    public Set<Command> getSubCommands() {
        return Collections.emptySet();
    }

    @Override
    @NotNull
    public CommandResult execute(@NotNull Sender sender, @NotNull String label, @NotNull ArgumentList args) {
        if (!sender.hasPermission(getPermission())) {
            Message.COMMAND_NO_PERMISSION.replacePermission(getPermission()).send(sender);
            return CommandResult.NO_PERMISSION;
        }

        plugin.getScheduler().execute(new FileCheckTask(plugin));
        Message.COMMAND_CLEAN_RUN.send(sender);
        return CommandResult.SUCCESS;
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull Sender sender, @NotNull String label, @NotNull ArgumentList args) {
        return Collections.emptyList();
    }
}
