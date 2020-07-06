package net.okocraft.databackup.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DataBackupGui implements InventoryHolder {

    private final Inventory inv;

    private DataBackupGui(int size, @NotNull String title) {
        inv = Bukkit.createInventory(this, size, title);
    }

    @NotNull
    @Contract("_ -> new")
    public static DataBackupGui createInventoryGui(@NotNull String title) {
        return new DataBackupGui(45, title);
    }

    @NotNull
    @Contract("_ -> new")
    public static DataBackupGui createEnderChestGui(@NotNull String title) {
        return new DataBackupGui(27, title);
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inv;
    }
}
