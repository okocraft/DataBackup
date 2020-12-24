package net.okocraft.databackup.command.subcommand;

import com.github.siroshun09.mccommand.bukkit.argument.parser.BukkitParser;
import com.github.siroshun09.mccommand.bukkit.sender.BukkitSender;
import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.argument.Argument;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.sender.Sender;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.data.DataType;
import net.okocraft.databackup.lang.DefaultMessage;
import net.okocraft.databackup.lang.MessageProvider;
import net.okocraft.databackup.lang.Placeholders;
import net.okocraft.databackup.storage.PlayerDataFile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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
            MessageProvider.sendNoPermission(sender, getPermission());
            return CommandResult.NO_PERMISSION;
        }

        List<Argument> args = context.getArguments();

        if (args.size() < 4) {
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_SHOW_USAGE, sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        if (args.get(1).get().equalsIgnoreCase("offline")) {
            if (args.size() < 5) {
                MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_SHOW_USAGE, sender);
                return CommandResult.INVALID_ARGUMENTS;
            }

            args = args.subList(1, args.size());
        }


        Argument secondArgument = args.get(1);
        Optional<DataType<?>> type = plugin.getDataTypeRegistry().get(secondArgument);

        if (type.isEmpty()) {
            MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_INVALID_DATA_TYPE, sender)
                    .replace(Placeholders.DATA_TYPE_NAME, secondArgument)
                    .send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Argument thirdArgument = args.get(2);
        OfflinePlayer target = BukkitParser.OFFLINE_PLAYER.parse(thirdArgument);

        if (target == null) {
            MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_PLAYER_NOT_FOUND, sender)
                    .replace(Placeholders.PLAYER_NAME, thirdArgument)
                    .send(sender);
            return CommandResult.STATE_ERROR;
        }

        Argument fourthArgument = args.get(3);
        Path filePath = plugin.getStorage().getPlayerDirectory(target).resolve(fourthArgument.get());

        if (!Files.exists(filePath)) {
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_BACKUP_NOT_FOUND, sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        PlayerDataFile dataFile = plugin.getStorage().loadPlayerDataFile(filePath);
        dataFile.show(type.get(), (BukkitSender) sender);

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
            var secondArgument = args.get(1).get();
            List<String> result =
                    plugin.getDataTypeRegistry().getRegisteredDataType()
                            .stream()
                            .map(DataType::getName)
                            .filter(str -> str.startsWith(secondArgument))
                            .sorted()
                            .collect(Collectors.toList());

            if (!offline) {
                result.add("offline");
            }

            return result;
        }

        if (args.size() == 3) {
            var thirdArgument = args.get(2).get();

            return offline ?
                    plugin.getStorage().getBackedUpPlayers()
                            .stream()
                            .map(Bukkit::getOfflinePlayer)
                            .map(OfflinePlayer::getName)
                            .filter(Objects::nonNull)
                            .filter(player -> player.startsWith(thirdArgument))
                            .sorted()
                            .collect(Collectors.toUnmodifiableList()) :
                    plugin.getServer().getOnlinePlayers().stream()
                            .map(HumanEntity::getName)
                            .filter(player -> player.startsWith(thirdArgument))
                            .sorted()
                            .collect(Collectors.toUnmodifiableList());
        }

        if (args.size() == 4) {
            Argument thirdArgument = args.get(2);
            OfflinePlayer target = BukkitParser.OFFLINE_PLAYER.parse(thirdArgument);

            if (target != null) {
                var fourthArgument = args.get(3).get();

                return plugin.getStorage().getPlayerDataYamlFiles(target.getUniqueId())
                        .map(Path::getFileName)
                        .map(Path::toString)
                        .filter(path -> path.startsWith(fourthArgument))
                        .sorted()
                        .collect(Collectors.toUnmodifiableList());
            }
        }

        return Collections.emptyList();
    }
}
