package net.okocraft.databackup;

import com.github.siroshun09.sirolibrary.config.BukkitYaml;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserList extends BukkitYaml {
    private final static UserList INSTANCE = new UserList();

    private UserList() {
        super(DataBackup.get().getDataFolder().toPath().resolve("users.yml"), true);
    }

    public static UserList get() {
        return INSTANCE;
    }

    public void addUser(@NotNull Player player) {
        if (!getUsers().contains(player.getName())) {
            getConfig().set(player.getName(), player.getUniqueId().toString());
            save();
        }
    }

    public void removeUser(@NotNull String name) {
        getConfig().set(name, null);
        save();
    }

    public void setUser(@NotNull String name, @NotNull UUID uuid) {
        getConfig().set(name, uuid.toString());
        save();
    }

    public void updateAllUsers() {
        DataBackup.get().getLogger().info("Start to update user list.");
        for (String name : getConfig().getKeys(false)) {
            getUUID(name).ifPresent(uuid -> {
                String temp_name = Bukkit.getOfflinePlayer(uuid).getName();
                if (temp_name != null && !name.equals(temp_name)) {
                    setUser(name, uuid);
                    removeUser(name);
                }
            });
        }
        DataBackup.get().getLogger().info("Completed to update user list.");
    }

    public Optional<UUID> getUUID(@NotNull String name) {
        try {
            return Optional.of(UUID.fromString(getString(name, "")));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    public Set<String> getUsers() {
        return Set.copyOf(getConfig().getKeys(false));
    }
}