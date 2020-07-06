package old_ver.commands;

import old_ver.DataBackup;
import old_ver.Messages;
import old_ver.Permissions;
import old_ver.tasks.BackupCheckingTask;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class CleanCmd {

    public static void run(@NotNull CommandSender sender) {
        if (!CommandChecker.checkPermission(sender, Permissions.CMD_CLEAN)) return;

        DataBackup.get().getExecutor().submit(new BackupCheckingTask());
        Messages.get().sendCleanBackup(sender);
    }
}
