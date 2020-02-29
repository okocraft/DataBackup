package net.okocraft.databackup;

import com.github.siroshun09.sirolibrary.SiroExecutors;
import com.github.siroshun09.sirolibrary.bukkitutils.BukkitUtil;
import com.github.siroshun09.sirolibrary.message.BukkitMessage;
import net.okocraft.databackup.listeners.CommandListener;
import net.okocraft.databackup.listeners.PlayerListener;
import net.okocraft.databackup.tasks.BackupCheckingTask;
import net.okocraft.databackup.tasks.PlayerBackupTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataBackup extends JavaPlugin {
    private static DataBackup INSTANCE;

    private final ScheduledExecutorService executor = SiroExecutors.newSingleScheduler("DataBackup-Thread");

    public DataBackup() {
        if (INSTANCE == null) {
            INSTANCE = this;
        }
    }

    public static DataBackup get() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        super.onEnable();

        Configuration.init();
        Messages.init();

        executor.submit(new BackupCheckingTask());
        getLogger().info("Submitted the checking backup file task.");

        VaultHooker.register();
        getLogger().info("Connected to Vault.");

        BukkitUtil.registerEvents(PlayerListener.get(), this);
        BukkitUtil.setCommandExecutor(getCommand("databackup"), CommandListener.get());
        getLogger().info("Registered the command \"/databackup\" (/db) and listeners.");

        UserList.get().updateAllUsers();

        executor.schedule(new PlayerBackupTask(), Configuration.get().getBackupInterval(), TimeUnit.MINUTES);
        getLogger().info("Scheduled the backup task.");

        BukkitMessage.printEnabledMsg(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        BukkitUtil.unregisterEvents(this);
        getLogger().info("Unregistered the listener.");

        Bukkit.getScheduler().cancelTasks(this);
        executor.shutdown();
        getLogger().info("Cancelled the backup task.");

        BukkitMessage.printDisabledMsg(this);
    }

    @NotNull
    public ScheduledExecutorService getExecutor() {
        return executor;
    }
}
