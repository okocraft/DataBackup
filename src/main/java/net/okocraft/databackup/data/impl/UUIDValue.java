package net.okocraft.databackup.data.impl;

import com.github.siroshun09.configapi.common.Configuration;
import com.github.siroshun09.configapi.common.configurable.AbstractConfigurableValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class UUIDValue extends AbstractConfigurableValue<UUID> {

    private static final UUID DEFAULT = new UUID(0, 0);
    public static final UUIDValue INSTANCE = new UUIDValue("uuid");

    private UUIDValue(@NotNull String key) {
        super(key, DEFAULT);
    }

    @Override
    public @Nullable UUID getValueOrNull(@NotNull Configuration configuration) {
        try {
            return UUID.fromString(configuration.getString(getKey()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public @NotNull String serialize(@NotNull UUID value) {
        return value.toString();
    }
}
