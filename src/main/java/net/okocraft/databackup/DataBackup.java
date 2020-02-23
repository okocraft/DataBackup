package net.okocraft.databackup;

import com.github.siroshun09.sirolibrary.SiroExecutors;
import com.github.siroshun09.sirolibrary.bukkitutils.BukkitUtil;
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
    private VaultHooker vaultHooker;

    public DataBackup() {
        INSTANCE = this;
    }

    public static DataBackup get() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Configuration.init();
        Messages.init();
        executor.submit(new BackupCheckingTask());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        vaultHooker = new VaultHooker();
        BukkitUtil.registerEvents(PlayerListener.get(), this);
        BukkitUtil.setCommandExecutor(getCommand("databackup"), CommandListener.get());
        UserList.get().updateAllUsers();
        executor.schedule(new PlayerBackupTask(), Configuration.get().getBackupInterval(), TimeUnit.MINUTES);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        BukkitUtil.unregisterEvents(this);
        Bukkit.getScheduler().cancelTasks(this);
        executor.shutdown();
    }

    @NotNull
    public ScheduledExecutorService getExecutor() {
        return executor;
    }

    public VaultHooker getVaultHooker() {
        return vaultHooker;
    }
}
