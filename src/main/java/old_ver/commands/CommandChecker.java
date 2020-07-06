package old_ver.commands;

import old_ver.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

final class CommandChecker {

    static boolean checkPermission(@NotNull CommandSender sender, @NotNull Permission perm) {
        if (sender.hasPermission(perm)) {
            return true;
        } else {
            Messages.get().sendNoPermission(sender, perm);
            return false;
        }
    }

    static boolean checkArgsLength(@NotNull CommandSender sender, @NotNull String[] args, int necessary) {
        if (necessary <= args.length) {
            return true;
        } else {
            Messages.get().sendNotEnoughArgs(sender);
            return false;
        }
    }

    static boolean isPlayer(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return true;
        } else {
            Messages.get().sendOnlyPlayer(sender);
            return false;
        }
    }

    static boolean existBackup(@NotNull CommandSender sender, @NotNull Path path) {
        if (Files.exists(path)) {
            return true;
        } else {
            Messages.get().sendBackupNotFound(sender);
            return false;
        }
    }
}
