package net.okocraft.databackup;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import net.okocraft.databackup.command.DataBackupCommand;
import net.okocraft.databackup.data.BackupStorage;
import net.okocraft.databackup.hooker.mcmmo.McMMORegister;
import net.okocraft.databackup.hooker.vault.MoneyData;
import net.okocraft.databackup.task.BackupTask;
import net.okocraft.databackup.task.FileCheckTask;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class DataBackup extends JavaPlugin {

    private BackupStorage storage;
    private Configuration config;
    private ScheduledExecutorService scheduler;

    @Override
    public void onLoad() {
        config = new Configuration(this);
        debug("config.yml loaded.");

        Message.setMessageConfig(new BukkitConfig(this, "message.yml", true));
        debug("message.yml loaded.");

        getLogger().info("DataBackup v" + getDescription().getVersion() + " has been loaded!");
    }

    @Override
    public void onEnable() {
        storage = new BackupStorage(config.getDestinationDir());
        scheduler = Executors.newSingleThreadScheduledExecutor();

        hook();

        Optional.ofNullable(getCommand("databackup")).ifPresent(cmd -> new DataBackupCommand(this).register(cmd));

        int interval = config.getBackupInterval();
        scheduler.scheduleAtFixedRate(new BackupTask(this), interval, interval, TimeUnit.HOURS);

        scheduler.execute(new FileCheckTask(this));
    }

    @Override
    public void onDisable() {
        config = null;
        Message.setMessageConfig(null);

        storage = null;
        scheduler.shutdownNow();
        getServer().getScheduler().cancelTasks(this);
        scheduler = null;
    }

    @NotNull
    public Configuration getConfiguration() {
        return config;
    }

    @NotNull
    public BackupStorage getStorage() {
        return storage;
    }

    @NotNull
    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public void debug(@NotNull String log) {
        if (config.isDebugMode()) {
            getLogger().info("Debug: " + log);
        }
    }

    private void hook() {
        if (isLoaded("Vault")) {
            storage.registerDataType(MoneyData.getName(), MoneyData::load, MoneyData::backup);
            getLogger().info("Economy data is now backed up!");
        }

        if (isLoaded("mcMMO")) {
            McMMORegister.register(storage);
            getLogger().info("mcMMO is now backed up!");
        }
    }

    private boolean isLoaded(@NotNull String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }
}
