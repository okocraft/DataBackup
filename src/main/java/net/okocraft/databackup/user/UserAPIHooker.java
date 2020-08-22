package net.okocraft.databackup.user;

import net.okocraft.userapi.api.UserAPI;
import net.okocraft.userapi.api.data.UserData;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

final class UserAPIHooker {

    @NotNull
    static Optional<UUID> getUUID(@NotNull String name) {
        try {
            Optional<UserData> data = UserAPI.getUserDataByName(name);
            if (data.isPresent()) {
                return Optional.of(data.get().getUuid());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    @NotNull
    static String getName(@NotNull UUID uuid) {
        try {
            return UserAPI.getUserData(uuid).getName();
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    @NotNull
    static Set<String> getUsers() {
        try {
            return UserAPI.getAllUserName();
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }
}
