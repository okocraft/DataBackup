package old_ver.user;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserList {

    public UserList() {
    }

    @SuppressWarnings("deprecation")
    public Optional<UUID> getUUID(@NotNull String name) {
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

    public Set<String> getUsers() {
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
                    .collect(Collectors.toSet());
        }

        return Set.copyOf(users);
    }

    private boolean isUserAPIEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("UserAPI");
    }

}
