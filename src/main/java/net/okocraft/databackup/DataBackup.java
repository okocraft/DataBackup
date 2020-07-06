package net.okocraft.databackup;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class DataBackup extends JavaPlugin {

    private Configuration config;

    @Override
    public void onLoad() {
        config = new Configuration(this);
        Message.setMessageConfig(new BukkitConfig(this, "message.yml", true));
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
}
