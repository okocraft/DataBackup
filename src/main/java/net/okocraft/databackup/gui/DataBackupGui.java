package net.okocraft.databackup.gui;

import net.okocraft.databackup.Message;
import net.okocraft.databackup.data.PlayerData;
import net.okocraft.databackup.util.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class DataBackupGui implements InventoryHolder {

    private final Inventory inv;

    private DataBackupGui(int size, @NotNull String title) {
        inv = Bukkit.createInventory(this, size, title);
    }

    public static void openInventoryGui(@NotNull Player player, @NotNull PlayerData data) {
        String title = Message.INVENTORY_TITLE.getMessage()
                .replace("%player%", data.getName().orElse("UNKNOWN"))
                .replace("%date%", Formatter.datetime(data.getDateTime()));

        Inventory inv = new DataBackupGui(45, title).getInventory();
        inv.setContents(data.getInventory());
        player.openInventory(inv);
    }

    public static void openEnderchestGui(@NotNull Player player, @NotNull PlayerData data) {
        String title = Message.ENDERCHEST_TITLE.getMessage()
                .replace("%player%", data.getName().orElse("UNKNOWN"))
                .replace("%date%", Formatter.datetime(data.getDateTime()));

        Inventory inv = new DataBackupGui(27, title).getInventory();
        inv.setContents(data.getEnderchest());
        player.openInventory(inv);
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inv;
    }
}
