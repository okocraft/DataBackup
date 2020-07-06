package net.okocraft.databackup.data;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import net.okocraft.databackup.hooker.EconomyHooker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PlayerData extends BukkitYaml {
    private final static String UUID_PATH = "uuid";
    private final static String DATETIME_PATH = "datetime";
    private final static String EXP_PATH = "exp";
    private final static String MONEY_PATH = "money";
    private final static String INVENTORY_PREFIX = "inventory.";
    private final static String ENDERCHEST_PREFIX = "enderchest.";

    public PlayerData(@NotNull Path filePath) {
        super(filePath, true);
    }

    @NotNull
    public UUID getUUID() {
        return UUID.fromString(getString(UUID_PATH));
    }

    @NotNull
    public LocalDateTime getDateTime() {
        return LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(getString(DATETIME_PATH)));
    }

    public float getExp() {
        return (float) getDouble(EXP_PATH);
    }

    public double getMoney() {
        return getDouble(MONEY_PATH);
    }

    public ItemStack[] getInventory() {
        ItemStack[] inventory = new ItemStack[41];

        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = getItemStack(INVENTORY_PREFIX + i);
        }

        return inventory;
    }

    public ItemStack[] getEnderchest() {
        ItemStack[] enderchest = new ItemStack[27];

        for (int i = 0; i < enderchest.length; i++) {
            enderchest[i] = getItemStack(ENDERCHEST_PREFIX + i);
        }

        return enderchest;
    }

    public void setData(@NotNull Player player) {
        set(UUID_PATH, player.getUniqueId().toString());
        set(DATETIME_PATH, LocalDateTime.now());

        set(EXP_PATH, player.getExp());
        set(MONEY_PATH, EconomyHooker.getBalance(player));

        int index = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            set(INVENTORY_PREFIX + index, item);
            index++;
        }

        index = 0;
        for (ItemStack item : player.getEnderChest().getContents()) {
            set(ENDERCHEST_PREFIX + index, item);
            index++;
        }
    }
}
