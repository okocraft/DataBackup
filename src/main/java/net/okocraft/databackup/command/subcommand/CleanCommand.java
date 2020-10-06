package net.okocraft.databackup.command.subcommand;

import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.task.FileCheckTask;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CleanCommand extends AbstractCommand {

    private final DataBackup plugin;

    public CleanCommand(@NotNull DataBackup plugin) {
        super("clean", "databackup.command.clean", Set.of("c"));

        this.plugin = plugin;
    }

    @Override
    public @NotNull CommandResult onExecution(@NotNull CommandContext context) {
        Sender sender = context.getSender();

        if (!sender.hasPermission(getPermission())) {
            Message.COMMAND_NO_PERMISSION.replacePermission(getPermission()).send(sender);
            return CommandResult.NO_PERMISSION;
        }

        plugin.getScheduler().execute(new FileCheckTask(plugin));
        Message.COMMAND_CLEAN_RUN.send(sender);

        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        return Collections.emptyList();
    }
}
