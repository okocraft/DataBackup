package net.okocraft.databackup.command;

import com.github.siroshun09.asynctabcompleter.AsyncTabCompleter;
import com.github.siroshun09.command.bukkit.BukkitArgumentList;
import com.github.siroshun09.command.bukkit.BukkitSender;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AsyncTabCompletionListener extends AsyncTabCompleter {

    private final DataBackupCommand command;

    public AsyncTabCompletionListener(@NotNull DataBackupCommand command) {
        super(command.getName(), List.copyOf(command.getAliases()));
        this.command = command;
    }

    @Override
    @NotNull
    public List<String> getCompletions(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return command.tabComplete(new BukkitSender(sender), alias, new BukkitArgumentList(args));
    }
}
