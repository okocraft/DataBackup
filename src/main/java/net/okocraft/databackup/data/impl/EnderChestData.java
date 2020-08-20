package net.okocraft.databackup.data.impl;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import net.okocraft.databackup.data.BackupData;
import net.okocraft.databackup.gui.DataBackupGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class EnderChestData implements BackupData {

    private final static String DATA_NAME = "enderchest";

    private final ItemStack[] items;

    private EnderChestData(@NotNull ItemStack[] item) {
        this.items = item;
    }

    @NotNull
    public static String getName() {
        return DATA_NAME;
    }

    @Contract("_ -> new")
    @NotNull
    public static EnderChestData load(@NotNull BukkitYaml yaml) {
        ItemStack[] items = new ItemStack[27];

        for (int i = 0; i < items.length; i++) {
            items[i] = yaml.getItemStack(DATA_NAME + "." + i);
        }

        return new EnderChestData(items);
    }

    @Contract("_ -> new")
    @NotNull
    public static EnderChestData backup(@NotNull Player player) {
        return new EnderChestData(player.getEnderChest().getContents());
    }

    @Override
    public void save(@NotNull BukkitYaml yaml) {
        for (int i = 0; i < items.length; i++) {
            yaml.set(DATA_NAME + "." + i, items[i]);
        }
    }

    @Override
    public void rollback(@NotNull Player player) {
        player.getEnderChest().setContents(items);
    }

    @Override
    public void show(@NotNull Player player, @NotNull UUID owner, @NotNull LocalDateTime backupTime) {
        DataBackupGui.openEnderchestGui(player, items, owner, backupTime);
    }

    @NotNull
    public ItemStack[] getItems() {
        return items;
    }
}
