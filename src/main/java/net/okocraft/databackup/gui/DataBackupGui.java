package net.okocraft.databackup.gui;

import com.github.siroshun09.mccommand.bukkit.sender.BukkitSender;
import net.okocraft.databackup.lang.DefaultMessage;
import net.okocraft.databackup.lang.MessageProvider;
import net.okocraft.databackup.lang.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DataBackupGui implements InventoryHolder {

    private final Inventory inv;

    private DataBackupGui(int size, @NotNull String title) {
        inv = Bukkit.createInventory(this, size, title);
    }

    public static void openInventoryGui(@NotNull Player player, @NotNull ItemStack[] items,
                                        @NotNull UUID owner, @NotNull LocalDateTime backupTime) {

        String title =
                MessageProvider.getBuilder(DefaultMessage.INVENTORY_TITLE, new BukkitSender(player))
                        .replace(
                                Placeholders.PLAYER_NAME,
                                Optional.ofNullable(Bukkit.getOfflinePlayer(owner).getName()).orElse("Unknown")
                        )
                        .replace(Placeholders.DATE, backupTime)
                        .build()
                        .get();

        Inventory inv = new DataBackupGui(45, title).getInventory();
        inv.setContents(items);
        player.openInventory(inv);
    }

    public static void openEnderchestGui(@NotNull Player player, @NotNull ItemStack[] items,
                                         @NotNull UUID owner, @NotNull LocalDateTime backupTime) {
        String title =
                MessageProvider.getBuilder(DefaultMessage.ENDERCHEST_TITLE, new BukkitSender(player))
                        .replace(
                                Placeholders.PLAYER_NAME,
                                Optional.ofNullable(Bukkit.getOfflinePlayer(owner).getName()).orElse("Unknown")
                        )
                        .replace(Placeholders.DATE, backupTime)
                        .build()
                        .get();

        Inventory inv = new DataBackupGui(27, title).getInventory();
        inv.setContents(items);
        player.openInventory(inv);
    }

    public static void openSearchResultGui(@NotNull Player player,
                                           @NotNull List<ItemStack> items, @NotNull Material material, int page) {
        String title =
                MessageProvider.getBuilder(DefaultMessage.SEARCH_RESULT_TITLE, new BukkitSender(player))
                        .replace(Placeholders.MATERIAL, material)
                        .replace(Placeholders.PAGE, page)
                        .build()
                        .get();

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
