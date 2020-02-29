package net.okocraft.databackup.commands;

import com.github.siroshun09.sirolibrary.config.BukkitYaml;
import net.okocraft.databackup.Messages;
import net.okocraft.databackup.Permissions;
import net.okocraft.databackup.VaultHooker;
import net.okocraft.databackup.data.BackupApplier;
import net.okocraft.databackup.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class RollbackCmd {

    public static void run(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!CommandChecker.checkPermission(sender, Permissions.CMD_ROLLBACK)) return;

        if (!CommandChecker.checkArgsLength(sender, args, 4)) return;

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            Messages.get().sendPlayerNotFound(sender, args[2]);
            return;
        }

        Path filePath = PlayerData.getDataDir().resolve(target.getUniqueId().toString()).resolve(args[3]);
        if (!CommandChecker.existBackup(sender, filePath)) return;

        rollback(sender, target, new PlayerData(new BukkitYaml(filePath, true)), args[1]);
    }

    private static void rollback(@NotNull CommandSender sender, @NotNull Player target, @NotNull PlayerData data, @NotNull String type) {
        switch (type.toLowerCase()) {
            case "inventory":
                rollbackInventory(sender, target, data);
                return;
            case "enderchest":
                rollbackEnderChest(sender, target, data);
                return;
            case "money":
                rollbackMoney(sender, target, data);
                return;
            case "xp":
                rollbackXP(sender, target, data);
                return;
            default:
                Messages.get().sendInvalidDataType(sender, type);
        }
    }

    private static void rollbackInventory(@NotNull CommandSender sender, @NotNull Player target, @NotNull PlayerData data) {
        if (BackupApplier.applyInventory(target, data)) {
            Messages.get().sendSenderAppliedInventory(sender, target, data);
        } else {
            Messages.get().sendFailedToApplyBackup(sender, data);
        }
    }

    private static void rollbackEnderChest(@NotNull CommandSender sender, @NotNull Player target, @NotNull PlayerData data) {
        if (BackupApplier.applyEnderChest(target, data)) {
            Messages.get().sendSenderAppliedEnderChest(sender, target, data);
        } else {
            Messages.get().sendFailedToApplyBackup(sender, data);
        }
    }

    private static void rollbackMoney(@NotNull CommandSender sender, @NotNull Player target, @NotNull PlayerData data) {
        if (VaultHooker.get().isEnabledEconomy()) {
            BackupApplier.applyMoney(target, data);
            Messages.get().sendSenderAppliedMoney(sender, target, data);
        } else {
            Messages.get().sendEconomyDisabled(sender);
        }
    }

    private static void rollbackXP(@NotNull CommandSender sender, @NotNull Player target, @NotNull PlayerData data) {
        BackupApplier.applyXP(target, data);
        Messages.get().sendSenderAppliedXP(sender, target, data);
    }

    @NotNull
    public static List<String> getTab(@NotNull CommandSender sender, @NotNull String[] args) {
        List<String> result = new ArrayList<>();

        if (!sender.hasPermission(Permissions.CMD_ROLLBACK)) return result;

        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], PlayerData.getTypes(), new ArrayList<>());
        }

        if (args.length == 3) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                result.add(player.getName());
            }
            return StringUtil.copyPartialMatches(args[2], result, new ArrayList<>());
        }

        if (args.length == 4) {
            return StringUtil.copyPartialMatches(args[3], PlayerData.getBackupList(args[2]), new ArrayList<>());
        }

        return result;
    }
}
