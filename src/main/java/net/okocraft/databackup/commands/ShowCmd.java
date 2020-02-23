package net.okocraft.databackup.commands;

import com.github.siroshun09.sirolibrary.config.BukkitYaml;
import net.okocraft.databackup.Messages;
import net.okocraft.databackup.Permissions;
import net.okocraft.databackup.UserList;
import net.okocraft.databackup.data.PlayerData;
import net.okocraft.databackup.gui.EnderChestGui;
import net.okocraft.databackup.gui.InventoryGui;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class ShowCmd {

    public static void run(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!CommandChecker.checkPermission(sender, Permissions.CMD_SHOW_BACKUP)) return;

        if (!CommandChecker.checkArgsLength(sender, args, 4)) return;

        if (args[1].equalsIgnoreCase("offline")) {
            showBackupOfOffline(sender, args);
        } else {
            showBackupOfOnline(sender, args);
        }
    }

    private static void showBackupOfOffline(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!CommandChecker.checkArgsLength(sender, args, 5)) return;

        Optional<UUID> uuid = UserList.get().getUUID(args[3]);

        if (uuid.isEmpty()) {
            Messages.get().sendPlayerNotFound(sender, args[3]);
            return;
        }

        Path filePath = PlayerData.getDataDir().resolve(uuid.get().toString()).resolve(args[4]);
        if (!CommandChecker.existBackup(sender, filePath)) return;

        showBackup(sender, args[2], new PlayerData(new BukkitYaml(filePath, true)));
    }

    private static void showBackupOfOnline(@NotNull CommandSender sender, @NotNull String[] args) {
        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            Messages.get().sendPlayerNotFound(sender, args[2]);
            return;
        }

        Path filePath = PlayerData.getDataDir().resolve(target.getUniqueId().toString()).resolve(args[3]);
        if (!CommandChecker.existBackup(sender, filePath)) return;

        showBackup(sender, args[1], new PlayerData(new BukkitYaml(filePath, true)));
    }

    private static void showBackup(@NotNull CommandSender sender, @NotNull String type, @NotNull PlayerData data) {
        switch (type.toLowerCase()) {
            case "inventory":
                if (CommandChecker.isPlayer(sender)) {
                    showInventoryBackup((Player) sender, data);
                } else {
                    Messages.get().sendOnlyPlayer(sender);
                }
                return;
            case "enderchest":
                if (CommandChecker.isPlayer(sender)) {
                    showEnderChestBackup((Player) sender, data);
                } else {
                    Messages.get().sendOnlyPlayer(sender);
                }
                return;
            case "money":
                showMoneyBackup(sender, data);
                return;
            case "xp":
                showXpBackup(sender, data);
                return;
            default:
                Messages.get().sendInvalidDataType(sender, type);
        }
    }

    private static void showInventoryBackup(@NotNull Player player, @NotNull PlayerData data) {
        new InventoryGui(data).openGui(player);
    }

    private static void showEnderChestBackup(@NotNull Player player, @NotNull PlayerData data) {
        new EnderChestGui(data).openGui(player);
    }

    private static void showMoneyBackup(@NotNull CommandSender sender, @NotNull PlayerData data) {
        Messages.get().sendMoneyBackup(sender, data);
    }

    private static void showXpBackup(@NotNull CommandSender sender, @NotNull PlayerData data) {
        Messages.get().sendXpBackup(sender, data);
    }

    @NotNull
    public static List<String> getTab(@NotNull CommandSender sender, @NotNull String[] args) {
        List<String> result = new ArrayList<>();

        if (!sender.hasPermission(Permissions.CMD_SHOW_BACKUP)) return result;

        if (args.length == 2) {
            result.addAll(PlayerData.getTypes());
            result.add("offline");
            return StringUtil.copyPartialMatches(args[1], result, new ArrayList<>());
        }

        if (args.length == 3) {
            if (args[1].equalsIgnoreCase("offline")) {
                return StringUtil.copyPartialMatches(args[2], PlayerData.getTypes(), new ArrayList<>());
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    result.add(player.getName());
                }
                return StringUtil.copyPartialMatches(args[2], result, new ArrayList<>());
            }
        }

        if (args.length == 4) {
            if (args[1].equalsIgnoreCase("offline")) {
                return StringUtil.copyPartialMatches(args[3], UserList.get().getUsers(), new ArrayList<>());
            } else {
                return StringUtil.copyPartialMatches(args[3], PlayerData.getBackupList(args[2]), new ArrayList<>());
            }
        }

        if (args.length == 5 && args[1].equalsIgnoreCase("offline")) {
            return StringUtil.copyPartialMatches(args[4], PlayerData.getBackupList(args[3]), new ArrayList<>());
        }

        return result;
    }
}
