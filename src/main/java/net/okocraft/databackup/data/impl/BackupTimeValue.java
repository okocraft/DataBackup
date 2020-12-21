package net.okocraft.databackup.data.impl;

import com.github.siroshun09.configapi.common.configurable.Configurable;
import com.github.siroshun09.configapi.common.configurable.LongValue;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class BackupTimeValue {

    public static final LongValue INSTANCE = Configurable.create("datetime", 0L);

    public static @NotNull LocalDateTime toLocalDateTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    private BackupTimeValue() {
        throw new UnsupportedOperationException();
    }
}
