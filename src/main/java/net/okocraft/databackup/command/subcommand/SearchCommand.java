package net.okocraft.databackup.command.subcommand;

import com.github.siroshun09.mccommand.bukkit.argument.parser.BukkitParser;
import com.github.siroshun09.mccommand.bukkit.sender.BukkitSender;
import com.github.siroshun09.mccommand.common.AbstractCommand;
import com.github.siroshun09.mccommand.common.CommandResult;
import com.github.siroshun09.mccommand.common.argument.Argument;
import com.github.siroshun09.mccommand.common.argument.parser.BasicParser;
import com.github.siroshun09.mccommand.common.context.CommandContext;
import com.github.siroshun09.mccommand.common.filter.StringFilter;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.data.BackupData;
import net.okocraft.databackup.data.ItemSearchable;
import net.okocraft.databackup.gui.DataBackupGui;
import net.okocraft.databackup.lang.DefaultMessage;
import net.okocraft.databackup.lang.MessageProvider;
import net.okocraft.databackup.lang.Placeholders;
import net.okocraft.databackup.storage.PlayerDataFile;
import net.okocraft.databackup.storage.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchCommand extends AbstractCommand {

    private static final List<String> PAGES =
            List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.toUnmodifiableList());

    private final DataBackup plugin;

    public SearchCommand(@NotNull DataBackup plugin) {
        super("search", "databackup.command.search");

        this.plugin = plugin;
    }

    @Override
    public @NotNull CommandResult onExecution(@NotNull CommandContext context) {
        BukkitSender sender = (BukkitSender) context.getSender();

        if (!sender.hasPermission(getPermission())) {
            MessageProvider.sendNoPermission(sender, getPermission());
            return CommandResult.NO_PERMISSION;
        }


        if (!(sender.getCommandSender() instanceof Player)) {
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_ONLY_PLAYER, sender);
            return CommandResult.NOT_PLAYER;
        }

        List<Argument> args = context.getArguments();

        if (args.size() < 3) {
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_SEARCH_USAGE, sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        if (args.get(1).get().equalsIgnoreCase("offline")) {
            if (args.size() < 4) {
                MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_SEARCH_USAGE, sender);
                return CommandResult.INVALID_ARGUMENTS;
            }

            args = args.subList(1, args.size());
        }

        return search(sender, args);
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
            var filter = StringFilter.startsWithIgnoreCase(secondArgument);

            var result = offline ?
                    plugin.getStorage().getBackedUpPlayers()
                            .stream()
                            .map(Bukkit::getOfflinePlayer)
                            .map(OfflinePlayer::getName)
                            .filter(Objects::nonNull)
                            .filter(filter)
                            .sorted()
                            .collect(Collectors.toUnmodifiableList()) :
                    plugin.getServer().getOnlinePlayers()
                            .stream()
                            .map(HumanEntity::getName)
                            .filter(filter)
                            .sorted()
                            .collect(Collectors.toList());

            if (!offline) {
                result.add("offline");
            }

            return result;
        }

        if (args.size() == 3) {
            var thirdArgument = args.get(2).get();
            var filter = StringFilter.startsWithIgnoreCase(thirdArgument);

            return Arrays.stream(Material.values())
                    .map(Enum::name)
                    .filter(filter)
                    .sorted()
                    .collect(Collectors.toUnmodifiableList());
        }

        if (args.size() == 4) {
            var fourthArgument = args.get(3).get();
            var filter = StringFilter.startsWithIgnoreCase(fourthArgument);

            return PAGES.stream()
                    .filter(filter)
                    .collect(Collectors.toUnmodifiableList());
        }

        return Collections.emptyList();
    }

    private CommandResult search(@NotNull BukkitSender sender, @NotNull List<Argument> args) {
        Argument secondArgument = args.get(1);
        OfflinePlayer target = BukkitParser.OFFLINE_PLAYER.parse(secondArgument);

        if (target == null) {
            MessageProvider.getBuilderWithPrefix(DefaultMessage.COMMAND_PLAYER_NOT_FOUND, sender)
                    .replace(Placeholders.PLAYER_NAME, secondArgument)
                    .send(sender);
            return CommandResult.STATE_ERROR;
        }

        Material material = BukkitParser.MATERIAL.parse(args.get(2));

        if (material == null) {
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_MATERIAL_NOT_FOUND, sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Storage storage = plugin.getStorage();

        List<ItemStack> result = new ArrayList<>();

        try {
            var dataTypes = plugin.getDataTypeRegistry().getRegisteredDataType();
            var dataSet =
                    storage.getPlayerDataYamlFiles(target.getUniqueId())
                            .map(storage::loadPlayerDataFile)
                            .collect(Collectors.toSet());

            for (PlayerDataFile dataFile : dataSet) {
                if (!dataFile.isLoaded()) {
                    dataFile.loadAll(dataTypes);
                }

                for (BackupData<?> data : dataFile.getDataMap().values()) {
                    if (data instanceof ItemSearchable) {
                        result.addAll(((ItemSearchable) data).search(material));
                    }
                }
            }
        } catch (Throwable e) {
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_SEARCH_FAILURE, sender);
            return CommandResult.EXCEPTION_OCCURRED;
        }

        if (result.isEmpty()) {
            MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_SEARCH_NOT_FOUND, sender);
            return CommandResult.STATE_ERROR;
        }

        int page;

        if (args.size() < 4) {
            page = 1;
        } else {
            int fourthValue = BasicParser.INTEGER.parseOrDefault(args.get(3), 1);
            page = Math.max(fourthValue, 1);
        }

        if ((result.size() / 54) < page - 1) {
            page = 1;
        }

        int fromIndex = 54 * (page - 1);
        int endIndex = Math.min(result.size(), 54 * page);

        DataBackupGui.openSearchResultGui((Player) sender.getCommandSender(), result.subList(fromIndex, endIndex), material, page);

        return CommandResult.SUCCESS;
    }
}
