package net.okocraft.databackup;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DataBackup extends JavaPlugin {

    private Configuration config;

    @Override
    public void onLoad() {
        config = new Configuration(this);
        debug("config.yml loaded.");
        Message.setMessageConfig(new BukkitConfig(this, "message.yml", true));
        debug("message.yml loaded.");

        getLogger().info("DataBackup v" + getDescription().getVersion() + " has been loaded!");
    }

    @Override
    public void onDisable() {
        config = null;
        Message.setMessageConfig(null);
    }

    @NotNull
    public Configuration getConfiguration() {
        return config;
    }

    public void debug(@NotNull String log) {
        if (config.isDebugMode()) {
            getLogger().info("Debug: " + log);
        }
    }
}
