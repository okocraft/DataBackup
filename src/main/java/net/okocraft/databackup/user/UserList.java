package net.okocraft.databackup.user;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class UserList {

    @SuppressWarnings("deprecation")
    @NotNull
    public static Optional<UUID> getUUID(@NotNull String name) {
        Optional<UUID> uuid;

        if (isUserAPIEnabled()) {
            uuid = UserAPIHooker.getUUID(name);
        } else {
            uuid = Optional.empty();
        }

        if (uuid.isPresent()) {
            return uuid;
        } else {

            return Optional.of(Bukkit.getOfflinePlayer(name).getUniqueId());
        }
    }

    @NotNull
    public static String getName(@NotNull UUID uuid) {
        String name;

        if (isUserAPIEnabled()) {
            name = UserAPIHooker.getName(uuid);
        } else {
            name = Bukkit.getOfflinePlayer(uuid).getName();
        }

        return StringUtils.isEmpty(name) ? "UNKNOWN" : name;
    }

    @Unmodifiable
    @NotNull
    public static Set<String> getUsers() {
        Set<String> users;

        if (isUserAPIEnabled()) {
            users = UserAPIHooker.getUsers();
        } else {
            users = Collections.emptySet();
        }

        if (users.isEmpty()) {
            users = Arrays.stream(Bukkit.getOfflinePlayers())
                    .map(OfflinePlayer::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
        }

        return users;
    }

    private static boolean isUserAPIEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("UserAPI");
    }
}
