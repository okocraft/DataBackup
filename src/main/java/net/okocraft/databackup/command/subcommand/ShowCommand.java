package net.okocraft.databackup.command.subcommand;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.argument.Argument;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.data.DataType;
import net.okocraft.databackup.user.UserList;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ShowCommand extends AbstractCommand {

    private final DataBackup plugin;

    public ShowCommand(@NotNull DataBackup plugin) {
        super("show", "databackup.command.show", Set.of("s"));

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
            Message.COMMAND_SHOW_USAGE.send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        if (args.get(1).get().equalsIgnoreCase("offline")) {
            if (args.size() < 5) {
                Message.COMMAND_SHOW_USAGE.send(player);
                return CommandResult.INVALID_ARGUMENTS;
            }

            args = args.subList(1, args.size());
        }


        Argument secondArgument = args.get(1);
        Optional<DataType> type = plugin.getStorage().getDataType(secondArgument.get());

        if (type.isEmpty()) {
            Message.COMMAND_INVALID_DATA_TYPE.replaceType(secondArgument.get()).send(player);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Argument thirdArgument = args.get(2);
        Optional<UUID> target = UserList.getUUID(thirdArgument.get());

        if (target.isEmpty()) {
            Message.COMMAND_PLAYER_NOT_FOUND.replacePlayer(thirdArgument.get()).send(player);
            return CommandResult.STATE_ERROR;
        }

        Argument fourthArgument = args.get(3);
        Path filePath = plugin.getStorage().createFilePath(target.get(), fourthArgument.get());

        if (!Files.exists(filePath)) {
            Message.COMMAND_BACKUP_NOT_FOUND.send(player);
            return CommandResult.INVALID_ARGUMENTS;
        }

        BukkitYaml yaml = new BukkitYaml(filePath);
        LocalDateTime dateTime = plugin.getStorage().getDateTime(yaml);

        type.get().load(yaml).show(player, target.get(), dateTime);

        return CommandResult.SUCCESS;
    }

    @Override
    public @NotNull List<String> onTabCompletion(@NotNull CommandContext context) {
        List<Argument> args = context.getArguments();

        if (args.size() < 2 || !context.getSender().hasPermission(getPermission())) {
            return Collections.emptyList();
        }

        boolean offline = false;

        if (args.get(1).get().equalsIgnoreCase("offline")) {
            args = args.subList(1, args.size());
            offline = true;
        }

        if (args.size() == 2) {
            Argument secondArgument = args.get(1);
            List<String> result = offline ? plugin.getStorage().getDataListAsString() : new ArrayList<>(plugin.getStorage().getDataListAsString());

            if (!offline) {
                result.add("offline");
            }

            return StringUtil.copyPartialMatches(secondArgument.get(), result, new ArrayList<>());
        }

        if (args.size() == 3) {
            Argument thirdArgument = args.get(2);

            return StringUtil.copyPartialMatches(
                    thirdArgument.get(),
                    plugin.getServer().getOnlinePlayers().stream()
                            .map(HumanEntity::getName)
                            .collect(Collectors.toUnmodifiableList()),
                    new ArrayList<>()
            );
        }

        if (args.size() == 4) {
            Argument thirdArgument = args.get(2);
            Optional<UUID> uuid = UserList.getUUID(thirdArgument.get());

            if (uuid.isPresent()) {
                Argument fourthArgument = args.get(3);

                return StringUtil.copyPartialMatches(
                        fourthArgument.get(),
                        plugin.getStorage().getFileListAsString(uuid.get()),
                        new ArrayList<>()
                );
            }
        }

        return Collections.emptyList();
    }
}
