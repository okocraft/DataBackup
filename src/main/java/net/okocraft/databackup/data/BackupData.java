package net.okocraft.databackup.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public interface BackupData<T> {

    @Contract("_, _ -> new")
    static @NotNull <T> BackupData<T> create(@NotNull UUID owner, @NotNull T data) {
        return create(owner, Instant.now().toEpochMilli(), data);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    static @NotNull <T> BackupData<T> create(@NotNull UUID owner, long backupTime, @NotNull T data) {
        return new BackupDataImpl<>(owner, backupTime, data);
    }

    @NotNull UUID getOwner();

    long getBackupTime();

    @NotNull T get();
}
