package old_ver.gui;

import old_ver.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EnderChestGui {

    private final PlayerData data;

    public EnderChestGui(@NotNull PlayerData data) {
        this.data = data;
    }

    public void openGui(@NotNull Player player) {
        String name = Objects.requireNonNullElse(Bukkit.getOfflinePlayer(data.getUuid()).getName(), "UNKNOWN");
        Inventory inv = DataBackupGui.createEnderChestGui(
                name + " のエンダーチェスト (" + data.getFormattedDateTime() + ")").getInventory();
        inv.setContents(data.getEnderChest());
        player.openInventory(inv);
    }
}
