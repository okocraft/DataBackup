package old_ver.gui;

import old_ver.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InventoryGui {
    private final PlayerData data;

    public InventoryGui(@NotNull PlayerData data) {
        this.data = data;
    }

    public void openGui(@NotNull Player player) {
        String name = Objects.requireNonNullElse(Bukkit.getOfflinePlayer(data.getUuid()).getName(), "UNKNOWN");
        Inventory inv = DataBackupGui.createInventoryGui(
                name + " のインベントリ (" + data.getFormattedDateTime() + ")").getInventory();
        inv.setContents(data.getInventory());
        player.openInventory(inv);
    }
}
