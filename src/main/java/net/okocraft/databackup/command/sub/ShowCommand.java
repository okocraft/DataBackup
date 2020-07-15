package net.okocraft.databackup.command.sub;

import com.github.siroshun09.command.Command;
import com.github.siroshun09.command.CommandResult;
import com.github.siroshun09.command.argument.ArgumentList;
import com.github.siroshun09.command.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.data.DataType;
import net.okocraft.databackup.user.UserList;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShowCommand implements Command {

    private final DataBackup plugin;

    public ShowCommand(@NotNull DataBackup plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getName() {
        return "show";
    }

    @Override
    @NotNull
    public String getPermission() {
        return "databackup.command.show";
    }

    @Override
    @NotNull
    @Unmodifiable
    public Set<String> getAliases() {
        return Set.of("s");
    }

    @Override
    @NotNull
    public String getUsage() {
        return Message.COMMAND_SHOW_USAGE.getColorized();
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

        Player player = plugin.getServer().getPlayer(sender.getUUID());

        if (player == null) {
            Message.COMMAND_ONLY_PLAYER.send(sender);
            return CommandResult.NOT_PLAYER;
        }

        if (args.size() < 4) {
            sender.sendMessage(getUsage());
            return CommandResult.NO_ARGUMENT;
        }

        if (args.get(1).equalsIgnoreCase("offline")) {
            return show(player, args.subList(1, args.size()));
        } else {
            return show(player, args);
        }
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull Sender sender, @NotNull String label, @NotNull ArgumentList args) {
        if (!sender.hasPermission(getPermission())) {
            return Collections.emptyList();
        }

        if (1 < args.size() && args.get(1).equalsIgnoreCase("offline")) {
            return tabComplete(args.subList(1, args.size()), true);
        } else {
            return tabComplete(args, false);
        }
    }

    @NotNull
    private List<String> tabComplete(@NotNull ArgumentList args, boolean offline) {
        if (args.size() == 2) {
            List<String> result = new LinkedList<>(plugin.getStorage().getDataListAsString());

            if (!offline) {
                result.add("offline");
            }

            return StringUtil.copyPartialMatches(args.get(1), result, new LinkedList<>());
        }

        if (args.size() == 3) {
            List<String> result = List.copyOf(offline ?
                    UserList.getUsers() :
                    plugin.getServer().getOnlinePlayers().stream()
                            .map(HumanEntity::getName)
                            .collect(Collectors.toList()));

            return StringUtil.copyPartialMatches(args.get(2), result, new LinkedList<>());
        }

        if (args.size() == 4) {
            Optional<UUID> uuid = UserList.getUUID(args.get(2));
            if (uuid.isPresent()) {
                return StringUtil.copyPartialMatches(args.get(3), plugin.getStorage().getFileList(uuid.get()), new LinkedList<>());
            }
        }

        return Collections.emptyList();
    }

    private CommandResult show(@NotNull Player player, @NotNull ArgumentList args) {
        if (args.size() < 4) {
            player.sendMessage(getUsage());
            return CommandResult.NO_ARGUMENT;
        }

        Optional<UUID> target = UserList.getUUID(args.get(2));
        if (target.isEmpty()) {
            Message.COMMAND_PLAYER_NOT_FOUND.replacePlayer(args.get(2)).send(player);
            return CommandResult.STATE_ERROR;
        }

        Optional<DataType> dataType = plugin.getStorage().getDataType(args.get(1));
        if (dataType.isEmpty()) {
            Message.COMMAND_INVALID_DATA_TYPE.replaceType(args.get(1));
            return CommandResult.INVALID_ARGUMENTS;
        }

        if (plugin.getStorage().show(player, target.get(), dataType.get(), args.get(3))) {
            return CommandResult.SUCCESS;
        } else {
            return CommandResult.INVALID_ARGUMENTS;
        }
    }
}
