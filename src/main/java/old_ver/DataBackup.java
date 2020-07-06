package old_ver;

import com.github.siroshun09.sirolibrary.SiroExecutors;
import com.github.siroshun09.sirolibrary.bukkitutils.BukkitUtil;
import com.github.siroshun09.sirolibrary.message.BukkitMessage;
import old_ver.listeners.CommandListener;
import old_ver.listeners.PlayerListener;
import old_ver.tasks.BackupCheckingTask;
import old_ver.tasks.PlayerBackupTask;
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
        debug("Submitted the checking backup file task.");

        BukkitUtil.registerEvents(PlayerListener.get(), this);
        BukkitUtil.setCommandExecutor(getCommand("databackup"), CommandListener.get());
        debug("Registered the command \"/databackup\" (/db) and listeners.");

        UserList.get().update();

        executor.schedule(new PlayerBackupTask(), Configuration.get().getBackupInterval(), TimeUnit.MINUTES);
        debug("Scheduled the backup task.");

        BukkitMessage.printEnabledMsg(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        BukkitUtil.unregisterEvents(this);
        debug("Unregistered the listener.");

        Bukkit.getScheduler().cancelTasks(this);
        executor.shutdownNow();
        debug("Cancelled the backup task.");

        BukkitMessage.printDisabledMsg(this);
    }

    @NotNull
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public void debug(@NotNull String log) {
        if (Configuration.get().isDebugMode()) {
            getLogger().info("DEBUG | " + log);
        }
    }
}
