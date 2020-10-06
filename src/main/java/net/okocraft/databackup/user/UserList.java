package net.okocraft.databackup.user;

import com.github.siroshun09.mccommand.common.argument.parser.ArgumentParser;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class UserList {

    @SuppressWarnings("deprecation")
    public static final ArgumentParser<UUID> PARSER = argument -> {
        UUID uuid;
        if (isUserAPIEnabled()) {
            uuid = UserAPIHooker.getUUID(argument.get());
        } else {
            uuid = null;
        }

        return Objects.requireNonNullElseGet(uuid, () -> Bukkit.getOfflinePlayer(argument.get()).getUniqueId());
    };

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
