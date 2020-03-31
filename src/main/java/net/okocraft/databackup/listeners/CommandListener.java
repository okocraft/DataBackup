package net.okocraft.databackup.listeners;

import net.okocraft.databackup.Messages;
import net.okocraft.databackup.commands.BackupCmd;
import net.okocraft.databackup.commands.CleanCmd;
import net.okocraft.databackup.commands.RollbackCmd;
import net.okocraft.databackup.commands.ShowCmd;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandListener implements CommandExecutor, TabCompleter {
    private final static CommandListener INSTANCE = new CommandListener();
    private final List<String> subCmd = Arrays.asList("backup", "clean", "rollback", "show", "help");

    private CommandListener() {

    }

    public static CommandListener get() {
        return INSTANCE;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (0 < args.length) {
            switch (args[0].toLowerCase()) {
                case "backup":
                    BackupCmd.run(sender, args);
                    return true;
                case "clean":
                    CleanCmd.run(sender);
                    return true;
                case "rollback":
                    RollbackCmd.run(sender, args);
                    return true;
                case "show":
                    ShowCmd.run(sender, args);
                    return true;
            }
        }
        Messages.get().sendHelp(sender);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], subCmd, result);
        }

        if (1 < args.length) {
            switch (args[0].toLowerCase()) {
                case "backup":
                    return BackupCmd.getTab(sender, args);
                case "rollback":
                    return RollbackCmd.getTab(sender, args);
                case "show":
                    return ShowCmd.getTab(sender, args);
            }
        }
        return result;
    }
}
