package old_ver.data;

import com.github.siroshun09.sirolibrary.economy.EconomyAPI;
import old_ver.Messages;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class BackupApplier {

    public static boolean applyInventory(@NotNull Player target, @NotNull PlayerData data) {
        try {
            target.getInventory().setContents(data.getInventory());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
        Messages.get().sendTargetAppliedInventory(target, data);
        return true;
    }

    public static boolean applyEnderChest(@NotNull Player target, @NotNull PlayerData data) {
        try {
            target.getEnderChest().setContents(data.getEnderChest());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
        Messages.get().sendTargetAppliedEnderChest(target, data);
        return true;
    }

    public static void applyMoney(@NotNull Player target, @NotNull PlayerData data) {
        EconomyAPI.setBalance(target, data.getMoney());
        Messages.get().sendTargetAppliedMoney(target, data);
    }

    public static void applyXP(@NotNull Player target, @NotNull PlayerData data) {
        target.setExp(data.getXp());
        Messages.get().sendTargetAppliedXP(target, data);
    }
}
