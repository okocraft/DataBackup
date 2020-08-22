package net.okocraft.databackup.gui;

import net.okocraft.databackup.Message;
import net.okocraft.databackup.user.UserList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DataBackupGui implements InventoryHolder {

    private final Inventory inv;

    private DataBackupGui(int size, @NotNull String title) {
        inv = Bukkit.createInventory(this, size, title);
    }

    public static void openInventoryGui(@NotNull Player player, @NotNull ItemStack[] items,
                                        @NotNull UUID owner, @NotNull LocalDateTime backupTime) {
        String title =
                Message.INVENTORY_TITLE.replacePlayer(UserList.getName(owner)).replaceDate(backupTime).getColorized();

        Inventory inv = new DataBackupGui(45, title).getInventory();
        inv.setContents(items);
        player.openInventory(inv);
    }

    public static void openEnderchestGui(@NotNull Player player, @NotNull ItemStack[] items,
                                         @NotNull UUID owner, @NotNull LocalDateTime backupTime) {
        String title =
                Message.ENDERCHEST_TITLE.replacePlayer(UserList.getName(owner)).replaceDate(backupTime).getColorized();

        Inventory inv = new DataBackupGui(27, title).getInventory();
        inv.setContents(items);
        player.openInventory(inv);
    }

    public static void openSearchResultGui(@NotNull Player player,
                                           @NotNull List<ItemStack> items, @NotNull Material material, int page) {
        String title = Message.SEARCH_RESULT_TITLE.replaceMaterial(material).replacePage(page).getColorized();

        Inventory inv = new DataBackupGui(54, title).getInventory();
        inv.setContents(items.toArray(new ItemStack[0]));
        player.openInventory(inv);
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inv;
    }
}
