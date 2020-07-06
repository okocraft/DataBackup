package old_ver;

import net.okocraft.userapi.api.UserAPI;
import net.okocraft.userapi.api.data.UserData;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserList {
    private final static UserList INSTANCE = new UserList();

    private Set<String> allUserName = new HashSet<>();

    private UserList() {
    }

    public static UserList get() {
        return INSTANCE;
    }

    public void update() {
        DataBackup.get().getExecutor().execute(this::updateAllUsers);
    }

    public void updateAllUsers() {
        DataBackup.get().debug("Start to update user list.");
        try {
            allUserName = UserAPI.getAllUserName();
        } catch (SQLException e) {
            DataBackup.get().getLogger().severe("Failed to update user list.");
            e.printStackTrace();
            return;
        }
        DataBackup.get().debug("Completed to update user list.");
    }

    @NotNull
    public Optional<UUID> getUUID(@NotNull String name) {
        Optional<UUID> uuid = Optional.empty();
        try {
            Optional<UserData> data = UserAPI.getUserDataByName(name);
            if (data.isPresent()) uuid = Optional.of(data.get().getUuid());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uuid;
    }

    @NotNull
    public Set<String> getUsers() {
        return allUserName;
    }
}