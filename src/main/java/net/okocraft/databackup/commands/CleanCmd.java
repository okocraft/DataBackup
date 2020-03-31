package net.okocraft.databackup.commands;

import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.Messages;
import net.okocraft.databackup.Permissions;
import net.okocraft.databackup.tasks.BackupCheckingTask;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class CleanCmd {

    public static void run(@NotNull CommandSender sender) {
        if (!CommandChecker.checkPermission(sender, Permissions.CMD_CLEAN)) return;

        DataBackup.get().getExecutor().submit(new BackupCheckingTask());
        Messages.get().sendCleanBackup(sender);
    }
}
