package net.okocraft.databackup.data;


import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class DataLoader {
    private final static int INVENTORY_SLOT = 41;
    private final static int ENDER_CHEST_SLOT = 27;

    private final BukkitYaml yaml;

    public DataLoader(BukkitYaml yaml) {
        this.yaml = yaml;
    }

    public PlayerData load() {
        return new PlayerData(loadUUID(), loadTime(),
                loadXP(), loadMoney(), loadInventory(), loadEnderChest());
    }

    private UUID loadUUID() {
        return UUID.fromString(yaml.getString("uuid"));
    }

    private LocalDateTime loadTime() {
        return LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(yaml.getString("dateTime")));
    }

    private float loadXP() {
        return (float) yaml.getDouble("xp");
    }

    private double loadMoney() {
        return yaml.getDouble("money", 0);
    }

    private ItemStack[] loadInventory() {
        ItemStack[] inventory = new ItemStack[INVENTORY_SLOT];

        for (int i = 0; i < INVENTORY_SLOT; i++) {
            ItemStack item = yaml.getConfig().getItemStack("inventory." + i);
            inventory[i] = item != null ? item : new ItemStack(Material.AIR);
        }

        return inventory;
    }

    private ItemStack[] loadEnderChest() {
        ItemStack[] enderChest = new ItemStack[ENDER_CHEST_SLOT];

        for (int i = 0; i < ENDER_CHEST_SLOT; i++) {
            ItemStack item = yaml.getConfig().getItemStack("enderChest." + i);
            enderChest[i] = item != null ? item : new ItemStack(Material.AIR);
        }

        return enderChest;
    }
}
