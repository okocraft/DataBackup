package net.okocraft.databackup;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DataBackup extends JavaPlugin {

    private Configuration config;

    @Override
    public void onLoad() {
        config = new Configuration(this);
    }

    @Override
    public void onDisable() {
        config = null;
    }

    @NotNull
    public Configuration getConfiguration() {
        return config;
    }
}
