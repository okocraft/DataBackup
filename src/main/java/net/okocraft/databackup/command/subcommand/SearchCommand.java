package net.okocraft.databackup.command.subcommand;

import com.github.siroshun09.mccommand.bukkit.argument.parser.BukkitParser;
import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.argument.Argument;
import com.github.siroshun09.mccommand.common.argument.parser.BasicParser;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.user.UserList;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SearchCommand extends AbstractCommand {

    private final DataBackup plugin;

    public SearchCommand(@NotNull DataBackup plugin) {
        super("search", "databackup.command.search");

        this.plugin = plugin;
    }

    @Override
    public @NotNull CommandResult onExecution(@NotNull CommandContext context) {
        Sender sender = context.getSender();

        if (!sender.hasPermission(getPermission())) {
            Message.COMMAND_NO_PERMISSION.replacePermission(getPermission()).send(sender);
            return CommandResult.NO_PERMISSION;
        }

        Player player = plugin.getServer().getPlayer(sender.getUUID());

        if (player == null) {
            Message.COMMAND_ONLY_PLAYER.send(sender);
            return CommandResult.NOT_PLAYER;
        }

        List<Argument> args = context.getArguments();

        if (args.size() < 4) {
            Message.COMMAND_SEARCH_USAGE.send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        return search(player, args);
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        List<Argument> args = context.getArguments();

        if (args.isEmpty() || !context.getSender().hasPermission(getPermission())) {
            return Collections.emptyList();
        }

        if (args.size() == 2) {
            Argument secondArgument = args.get(1);

            return StringUtil.copyPartialMatches(
                    secondArgument.get(),
                    plugin.getServer().getOnlinePlayers().stream()
                            .map(HumanEntity::getName)
                            .collect(Collectors.toUnmodifiableList()),
                    new ArrayList<>()
            );
        }

        if (args.size() == 3) {
            Argument thirdArgument = args.get(2);

            return StringUtil.copyPartialMatches(
                    thirdArgument.get(),
                    Stream.of(Material.values()).map(Enum::toString).collect(Collectors.toList()),
                    new ArrayList<>()
            );
        }

        if (args.size() == 4) {
            Argument fourthArgument = args.get(3);

            return StringUtil.copyPartialMatches(
                    fourthArgument.get(),
                    List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"),
                    new ArrayList<>()
            );
        }

        return Collections.emptyList();
    }

    private CommandResult search(@NotNull Player player, @NotNull List<Argument> args) {
        Argument secondArgument = args.get(1);
        UUID target = UserList.PARSER.parse(secondArgument);

        if (target == null) {
            Message.COMMAND_PLAYER_NOT_FOUND.replacePlayer(secondArgument.get()).send(player);
            return CommandResult.STATE_ERROR;
        }

        Material material = BukkitParser.MATERIAL.parse(args.get(2));

        if (material == null) {
            Message.COMMAND_MATERIAL_NOT_FOUND.send(player);
            return CommandResult.INVALID_ARGUMENTS;
        }

        int fourthValue = BasicParser.INTEGER.parseOrDefault(args.get(3), 1);
        int page = Math.max(fourthValue, 1);

        if (plugin.getStorage().search(player, target, material, page)) {
            return CommandResult.SUCCESS;
        } else {
            return CommandResult.STATE_ERROR;
        }
    }
}
