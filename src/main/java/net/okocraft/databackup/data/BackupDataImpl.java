package net.okocraft.databackup.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

class BackupDataImpl<T> implements BackupData<T> {

    private final UUID owner;
    private final long backupTime;
    private final T data;

    BackupDataImpl(@NotNull UUID owner, long backupTime, @NotNull T data) {
        this.owner = owner;
        this.backupTime = backupTime;
        this.data = data;
    }

    @Override
    public @NotNull UUID getOwner() {
        return owner;
    }

    @Override
    public long getBackupTime() {
        return backupTime;
    }

    @Override
    public @NotNull T get() {
        return data;
    }
}
