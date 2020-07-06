package old_ver;

import com.github.siroshun09.sirolibrary.config.BukkitConfig;
import org.jetbrains.annotations.NotNull;

public class Configuration extends BukkitConfig {
    private final static Configuration INSTANCE = new Configuration();

    private Configuration() {
        super(DataBackup.get(), "config.yml", true);
    }

    @NotNull
    public static Configuration get() {
        return INSTANCE;
    }

    public static void init() {
    }

    public boolean isDebugMode() {
        return getBoolean("debug", false);
    }

    public long getBackupInterval() {
        return getLong("interval", 30);
    }

    public long getBackupPeriod() {
        return getLong("period", 5);
    }
}
