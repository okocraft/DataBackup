package net.okocraft.databackup.command.sub;

import com.github.siroshun09.command.Command;
import com.github.siroshun09.command.CommandResult;
import com.github.siroshun09.command.argument.ArgumentList;
import com.github.siroshun09.command.bukkit.BukkitArgumentList;
import com.github.siroshun09.command.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BackupCommand implements Command {

    private final DataBackup plugin;

    public BackupCommand(@NotNull DataBackup plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getName() {
        return "backup";
    }

    @Override
    @NotNull
    public String getPermission() {
        return "databackup.command.backup";
    }

    @Override
    @NotNull
    @Unmodifiable
    public Set<String> getAliases() {
        return Set.of("b");
    }

    @Override
    @NotNull
    public String getUsage() {
        return Message.COMMAND_BACKUP_USAGE.getColorized();
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

        if (args.size() < 2) {
            sender.sendMessage(getUsage());
            return CommandResult.NO_ARGUMENT;
        }

        if (args.get(1).equalsIgnoreCase("all")) {
            return backupAll(sender);
        } else {
            return backupPlayer(sender, (BukkitArgumentList) args);
        }
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull Sender sender, @NotNull String label, @NotNull ArgumentList args) {
        if (!sender.hasPermission(getPermission()) || args.size() != 2) {
            return Collections.emptyList();
        }

        List<String> result = new LinkedList<>();

        result.add("all");
        plugin.getServer().getOnlinePlayers().stream().map(HumanEntity::getName).forEach(result::add);

        return StringUtil.copyPartialMatches(args.get(1), result, new LinkedList<>());
    }

    private CommandResult backupAll(@NotNull Sender sender) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!plugin.getStorage().backup(player)) {
                Message.COMMAND_BACKUP_FAILURE.send(sender);
                return CommandResult.STATE_ERROR;
            }
        }

        Message.COMMAND_BACKUP_ALL.send(sender);
        return CommandResult.SUCCESS;
    }

    private CommandResult backupPlayer(@NotNull Sender sender, @NotNull BukkitArgumentList args) {
        Player target = args.getPlayer(1);

        if (target == null) {
            Message.COMMAND_PLAYER_NOT_FOUND.replacePlayer(args.get(1)).send(sender);
            return CommandResult.STATE_ERROR;
        }

        if (plugin.getStorage().backup(target)) {
            Message.COMMAND_BACKUP_PLAYER.replacePlayer(target.getName()).send(sender);
            return CommandResult.SUCCESS;
        } else {
            Message.COMMAND_BACKUP_FAILURE.send(sender);
            return CommandResult.STATE_ERROR;
        }
    }
}
