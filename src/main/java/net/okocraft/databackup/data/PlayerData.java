package net.okocraft.databackup.data;

import com.github.siroshun09.sirolibrary.config.BukkitYaml;
import com.github.siroshun09.sirolibrary.file.FileUtil;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.UserList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerData extends AbstractData {
    private final static String NAME = "PlayerData";
    private final static Path DATA_DIR = BACKUP_DIR.resolve(NAME);
    private final static List<String> TYPES = Arrays.asList("inventory", "enderchest", "xp", "money");

    private final float xp;
    private final double money;
    private final ItemStack[] inventory = new ItemStack[41];
    private final ItemStack[] enderChest = new ItemStack[27];

    private PlayerData(UUID uuid, LocalDateTime dateTime, float xp, double money, @NotNull ItemStack[] inventory, ItemStack[] enderChest) {
        super(uuid, dateTime);
        this.xp = xp;
        this.money = money;

        if (inventory.length == 41) {
            for (int i = 0; i < 41; i++) {
                this.inventory[i] = Objects.requireNonNullElse(inventory[i], new ItemStack(Material.AIR));
            }
        } else {
            throw new IllegalArgumentException("inventory length must be 41.");
        }

        if (enderChest.length == 27) {
            for (int i = 0; i < 27; i++) {
                this.enderChest[i] = Objects.requireNonNullElse(enderChest[i], new ItemStack(Material.AIR));
            }
        } else {
            throw new IllegalArgumentException("enderChest length must be 27.");
        }
    }

    public PlayerData(@NotNull Player player) {
        this(player.getUniqueId(), LocalDateTime.now(), player.getExp(), DataBackup.get().getVaultHooker().getBalance(player),
                player.getInventory().getContents(), player.getEnderChest().getContents());
    }

    public PlayerData(@NotNull BukkitYaml yaml) {
        this(loadUUID(yaml), loadDateTime(yaml),
                loadXP(yaml), loadMoney(yaml), loadInventory(yaml), loadEnderChest(yaml));
    }

    private static float loadXP(@NotNull BukkitYaml yaml) {
        return (float) yaml.getDouble("xp", 0);
    }

    private static double loadMoney(@NotNull BukkitYaml yaml) {
        return yaml.getDouble("money", 0);
    }

    @NotNull
    private static ItemStack[] loadInventory(@NotNull BukkitYaml yaml) {
        ItemStack[] inventory = new ItemStack[41];
        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = yaml.getItemStack("inventory." + i, new ItemStack(Material.AIR));
        }
        return inventory;
    }

    @NotNull
    private static ItemStack[] loadEnderChest(@NotNull BukkitYaml yaml) {
        ItemStack[] enderChest = new ItemStack[27];
        for (int i = 0; i < enderChest.length; i++) {
            enderChest[i] = yaml.getItemStack("enderChest." + i, new ItemStack(Material.AIR));
        }
        return enderChest;
    }

    @NotNull
    public static Path getDataDir() {
        return DATA_DIR;
    }

    public static void addType(@NotNull String type) {
        TYPES.add(type);
    }

    @NotNull
    public static List<String> getTypes() {
        return List.copyOf(TYPES);
    }

    @NotNull
    public static List<String> getBackupList(@NotNull String target) {
        Optional<UUID> uuid = UserList.get().getUUID(target);
        if (uuid.isEmpty()) return new ArrayList<>();
        return getBackupList(uuid.get());
    }

    @NotNull
    public static List<String> getBackupList(@NotNull UUID uuid) {
        List<String> result = new ArrayList<>();
        Path dirPath = DATA_DIR.resolve(uuid.toString());

        if (FileUtil.isNotExist(dirPath)) return result;

        try {
            List<Path> files = Files.list(dirPath)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".yml"))
                    .collect(Collectors.toList());
            for (Path path : files) {
                result.add(path.getFileName().toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public float getXp() {
        return xp;
    }

    public double getMoney() {
        return money;
    }

    @NotNull
    public ItemStack[] getInventory() {
        return inventory;
    }

    @NotNull
    public ItemStack[] getEnderChest() {
        return enderChest;
    }

    @Override
    @NotNull
    public Path getBackupFilePath() {
        return DATA_DIR.resolve(uuid.toString()).resolve(getFormattedDateTime() + ".yml");
    }

    @Override
    public void save() {
        BukkitYaml yaml = new BukkitYaml(getBackupFilePath(), true);

        saveUUID(yaml);
        saveDateTime(yaml);
        saveXP(yaml);
        saveMoney(yaml);
        saveInventory(yaml);
        saveEnderChest(yaml);

        yaml.save();
    }

    private void saveXP(@NotNull BukkitYaml yaml) {
        yaml.getConfig().set("xp", (double) xp);
    }

    private void saveMoney(@NotNull BukkitYaml yaml) {
        yaml.getConfig().set("money", money);
    }

    private void saveInventory(@NotNull BukkitYaml yaml) {
        for (int i = 0; i < inventory.length; i++) {
            yaml.getConfig().set("inventory." + i, inventory[i]);
        }
    }

    private void saveEnderChest(@NotNull BukkitYaml yaml) {
        for (int i = 0; i < enderChest.length; i++) {
            yaml.getConfig().set("enderChest." + i, enderChest[i]);
        }
    }

}
