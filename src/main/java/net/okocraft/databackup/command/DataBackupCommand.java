package net.okocraft.databackup.command;

import com.github.siroshun09.command.Command;
import com.github.siroshun09.command.CommandResult;
import com.github.siroshun09.command.argument.ArgumentList;
import com.github.siroshun09.command.sender.Sender;
import net.okocraft.databackup.Message;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Set;

public class DataBackupCommand implements Command {

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
        return Message.COMMAND_USAGE.getMessage();
    }

    @Override
    @NotNull
    @Unmodifiable
    public Set<Command> getSubCommands() {
        return Set.of();
    }

    @Override
    @NotNull
    public CommandResult execute(@NotNull Sender sender, @NotNull String label, @NotNull ArgumentList args) {
        return null;
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull Sender sender, @NotNull String label, @NotNull ArgumentList args) {
        return null;
    }
}
