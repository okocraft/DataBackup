package old_ver.commands;

import old_ver.Messages;
import old_ver.Permissions;
import old_ver.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class BackupCmd {

    public static void run(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!CommandChecker.checkPermission(sender, Permissions.CMD_BACKUP)) return;

        if (!CommandChecker.checkArgsLength(sender, args, 2)) return;

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            Messages.get().sendPlayerNotFound(sender, args[1]);
            return;
        }

        PlayerData data = new PlayerData(target);
        data.save();

        Messages.get().sendForcedBackup(sender, target);
    }

    @NotNull
    public static List<String> getTab(@NotNull CommandSender sender, @NotNull String[] args) {
        List<String> result = new ArrayList<>();

        if (!sender.hasPermission(Permissions.CMD_BACKUP)) return result;

        if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                result.add(player.getName());
            }

            return StringUtil.copyPartialMatches(args[1], result, new ArrayList<>());
        }

        return result;
    }
}
