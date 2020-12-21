package net.okocraft.databackup;

import com.github.siroshun09.configapi.bukkit.BukkitYamlFactory;
import com.github.siroshun09.configapi.common.Configuration;
import com.github.siroshun09.mccommand.bukkit.paper.AsyncTabCompleteListener;
import com.github.siroshun09.mccommand.bukkit.paper.PaperChecker;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import net.milkbowl.vault.economy.Economy;
import net.okocraft.databackup.command.DataBackupCommand;
import net.okocraft.databackup.data.DataTypeRegistry;
import net.okocraft.databackup.data.impl.EnderChestData;
import net.okocraft.databackup.data.impl.ExpData;
import net.okocraft.databackup.data.impl.InventoryData;
import net.okocraft.databackup.external.mcmmo.SkillXPData;
import net.okocraft.databackup.external.vault.MoneyData;
import net.okocraft.databackup.lang.MessageProvider;
import net.okocraft.databackup.listener.PluginListener;
import net.okocraft.databackup.storage.Storage;
import net.okocraft.databackup.task.BackupTask;
import net.okocraft.databackup.task.FileCheckTask;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public final class DataBackup extends JavaPlugin {

    private Configuration config;
    private DataTypeRegistry dataTypeRegistry;
    private Storage storage;
    private ScheduledExecutorService scheduler;

    @Override
    public void onLoad() {
        config = BukkitYamlFactory.loadUnsafe(this, "config.yml");

        try {
            MessageProvider.reloadLanguages(this);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not load languages.", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("DataBackup v" + getDescription().getVersion() + " has been loaded!");
    }

    @Override
    public void onEnable() {
        dataTypeRegistry = new DataTypeRegistry();

        String directory = Setting.BACKUP_DESTINATION_DIRECTORY.getValue(config);

        storage = new Storage(directory.isEmpty() ? Path.of(directory) : getDataFolder().toPath().resolve("backups"));

        try {
            storage.setup();
        } catch (Throwable e) {
            getLogger().log(Level.SEVERE, "Could not setup the storage.", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();

        dataTypeRegistry.registerDataType(Set.of(new InventoryData(), new EnderChestData(), new ExpData()));

        Optional.ofNullable(getCommand("databackup")).ifPresent(this::registerCommand);

        int interval = Setting.BACKUP_INTERVAL.getValue(config);
        scheduler.scheduleAtFixedRate(new BackupTask(this), interval, interval, TimeUnit.MINUTES);

        scheduler.execute(new FileCheckTask(this));

        getServer().getScheduler().runTask(this, this::hook);

        getLogger().info("DataBackup v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }

        getServer().getScheduler().cancelTasks(this);
        HandlerList.unregisterAll(this);

        getLogger().info("DataBackup v" + getDescription().getVersion() + " has been disabled!");
    }

    @NotNull
    public Configuration getConfiguration() {
        return config;
    }

    @NotNull
    public DataTypeRegistry getDataTypeRegistry() {
        return dataTypeRegistry;
    }

    @NotNull
    public Storage getStorage() {
        return storage;
    }

    @NotNull
    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }


    private void registerCommand(@NotNull PluginCommand command) {
        var cmd = new DataBackupCommand(this);
        cmd.register(command);
        if (PaperChecker.check()) {
            AsyncTabCompleteListener.register(this, cmd);
        }
    }

    public void hookMcMMO() {
        if (isLoaded("mcMMO")) {
            Arrays.stream(PrimarySkillType.values())
                    .filter(s -> !s.isChildSkill())
                    .map(SkillXPData::new)
                    .forEach(dataTypeRegistry::registerDataType);
            getLogger().info("mcMMO is now backed up!");
        }
    }

    public void hookVault() {
        if (isLoaded("Vault")) {
            var rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                dataTypeRegistry.registerDataType(new MoneyData(rsp.getProvider()));
                getLogger().info("Economy data is now backed up!");
            }
        }
    }

    private void hook() {
        hookVault();
        hookMcMMO();

        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
    }

    private boolean isLoaded(@NotNull String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }
}
