package net.okocraft.databackup.command.sub;

import com.github.siroshun09.command.Command;
import com.github.siroshun09.command.CommandResult;
import com.github.siroshun09.command.argument.ArgumentList;
import com.github.siroshun09.command.bukkit.BukkitArgumentList;
import com.github.siroshun09.command.sender.Sender;
import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Message;
import net.okocraft.databackup.data.DataType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RollbackCommand implements Command {

    private final DataBackup plugin;

    public RollbackCommand(@NotNull DataBackup plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getName() {
        return "rollback";
    }

    @Override
    @NotNull
    public String getPermission() {
        return "databackup.command.rollback";
    }

    @Override
    @NotNull
    @Unmodifiable
    public Set<String> getAliases() {
        return Set.of("r", "rb");
    }

    @Override
    @NotNull
    public String getUsage() {
        return Message.COMMAND_ROLLBACK_USAGE.getColorized();
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

        if (args.size() < 4) {
            sender.sendMessage(getUsage());
            return CommandResult.NO_ARGUMENT;
        }

        Optional<DataType> type = plugin.getStorage().getDataType(args.get(1));

        if (type.isEmpty()) {
            Message.COMMAND_INVALID_DATA_TYPE.replaceType(args.get(1)).send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Player target = ((BukkitArgumentList) args).getPlayer(2);
        if (target == null) {
            Message.COMMAND_PLAYER_NOT_FOUND.replacePlayer(args.get(2)).send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        Path filePath = plugin.getStorage().createFilePath(target, args.get(3));
        if (!Files.exists(filePath)) {
            Message.COMMAND_BACKUP_NOT_FOUND.send(sender);
            return CommandResult.INVALID_ARGUMENTS;
        }

        BukkitYaml yaml = new BukkitYaml(filePath);

        type.get().load(yaml).rollback(target);
        LocalDateTime dateTime = plugin.getStorage().getDateTime(yaml);

        Message.COMMAND_ROLLBACK_SENDER
                .replacePlayer(target.getName())
                .replaceType(type.get().getName())
                .replaceDate(dateTime)
                .send(sender);

        if (!target.getUniqueId().equals(sender.getUUID())) {
            Message.COMMAND_ROLLBACK_TARGET
                    .replaceType(type.get().getName())
                    .replaceDate(dateTime)
                    .send(target);
        }

        return CommandResult.SUCCESS;
    }

    @Override
    @NotNull
    public List<String> tabComplete(@NotNull Sender sender, @NotNull String label, @NotNull ArgumentList args) {
        if (!sender.hasPermission(getPermission())) {
            return Collections.emptyList();
        }

        if (args.size() == 1) {
            return StringUtil.copyPartialMatches(args.get(0), plugin.getStorage().getDataListAsString(), new LinkedList<>());
        }

        if (args.size() == 2) {
            return StringUtil.copyPartialMatches(args.get(1),
                    plugin.getServer().getOnlinePlayers().stream()
                            .map(HumanEntity::getName).collect(Collectors.toUnmodifiableList()),
                    new LinkedList<>());
        }

        if (args.size() == 3) {
            Player target = ((BukkitArgumentList) args).getPlayer(2);

            if (target != null) {
                return StringUtil.copyPartialMatches(args.get(2), plugin.getStorage().getFileListAsString(target.getUniqueId()), new LinkedList<>());
            }
        }

        return Collections.emptyList();
    }
}
