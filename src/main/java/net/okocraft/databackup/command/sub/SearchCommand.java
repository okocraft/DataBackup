package net.okocraft.databackup.command.sub;

import com.github.siroshun09.command.Command;
import com.github.siroshun09.command.CommandResult;
import com.github.siroshun09.command.argument.ArgumentList;
import com.github.siroshun09.command.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.user.UserList;
import org.bukkit.Material;
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
import java.util.stream.Stream;

public class SearchCommand implements Command {

    private final DataBackup plugin;

    public SearchCommand(@NotNull DataBackup plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getName() {
        return "search";
    }

    @Override
    @NotNull
    public String getPermission() {
        return "databackup.command.search";
    }

    @Override
    @NotNull
    @Unmodifiable
    public Set<String> getAliases() {
        return Collections.emptySet();
    }

    @Override
    @NotNull
    public String getUsage() {
        return Message.COMMAND_SEARCH_USAGE.getColorized();
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

        if (args.size() < 3) {
            sender.sendMessage(getUsage());
            return CommandResult.NO_ARGUMENT;
        }

        return search(player, args);
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull Sender sender, @NotNull String label, @NotNull ArgumentList args) {
        if (!sender.hasPermission(getPermission())) {
            return Collections.emptyList();
        }

        if (args.size() == 2) {
            List<String> players =
                    plugin.getServer().getOnlinePlayers().stream()
                            .map(HumanEntity::getName)
                            .collect(Collectors.toList());

            return StringUtil.copyPartialMatches(args.get(1), players, new LinkedList<>());
        }

        if (args.size() == 3) {
            List<String> materials = Stream.of(Material.values()).map(Enum::toString).collect(Collectors.toList());
            return StringUtil.copyPartialMatches(args.get(2), materials, new LinkedList<>());
        }

        return Collections.emptyList();
    }

    private CommandResult search(@NotNull Player player, @NotNull ArgumentList args) {
        Optional<UUID> target = UserList.getUUID(args.get(2));

        if (target.isEmpty()) {
            Message.COMMAND_PLAYER_NOT_FOUND.replacePlayer(args.get(2)).send(player);
            return CommandResult.STATE_ERROR;
        }

        Material material;

        try {
            material = Material.valueOf(args.get(3));
        } catch (IllegalArgumentException e) {
            Message.COMMAND_MATERIAL_NOT_FOUND.send(player);
            return CommandResult.INVALID_ARGUMENTS;
        }

        int page = Math.max(args.getInt(3), 1);

        if (plugin.getStorage().search(player, target.get(), material, page)) {
            return CommandResult.SUCCESS;
        } else {
            return CommandResult.STATE_ERROR;
        }
    }
}
