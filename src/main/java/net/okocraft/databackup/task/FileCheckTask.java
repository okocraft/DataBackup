package net.okocraft.databackup.task;

import net.okocraft.databackup.DataBackup;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.stream.Stream;

public class FileCheckTask implements Runnable {

    private final DataBackup plugin;

    public FileCheckTask(@NotNull DataBackup plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (!Files.exists(plugin.getStorage().getPlayerDataDir())) {
            return;
        }

        plugin.getLogger().info("Starting file check task...");

        long startTime = System.currentTimeMillis();

        int deleted = checkDirectory();
        printResult(deleted);

        long took = System.currentTimeMillis() - startTime;
        plugin.getLogger().info("File check task task was completed. (" + took + "ms)");
    }

    private int checkDirectory() {
        AtomicInteger count = new AtomicInteger();

        getUserDirectories(plugin.getStorage().getPlayerDataDir()).forEach(p -> count.addAndGet(checkUserDirectory(p)));

        return count.get();
    }

    private int checkUserDirectory(Path dir) {
        AtomicInteger count = new AtomicInteger();

        getExpiredFiles(dir).forEach(p -> {
            try {
                Files.deleteIfExists(p);
                count.incrementAndGet();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to delete file: " + p.toAbsolutePath().toString(), e);
            }
        });

        try {
            if (Files.list(dir).count() == 0) {
                Files.deleteIfExists(dir);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Failed to delete the directory: " + dir.toAbsolutePath().toString(), e);
        }

        return count.get();
    }

    private boolean isExpired(@NotNull Path path) {
        try {
            return plugin.getConfiguration().getBackupPeriod()
                    <= Duration.between(Files.getLastModifiedTime(path).toInstant(), Instant.now()).toDays();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to check file, ignore " + path.toAbsolutePath().toString());
            e.printStackTrace();
            return false;
        }
    }

    private void printResult(int deleted) {
        if (1 < deleted) {
            plugin.getLogger().info(deleted + " files have been deleted.");
        } else if (deleted == 0) {
            plugin.getLogger().info("No file has been deleted.");
        } else if (deleted == 1) {
            plugin.getLogger().info("1 file has been deleted.");
        }
    }

    private Stream<Path> getUserDirectories(@NotNull Path parent) {
        try {
            return Files.list(parent)
                    .filter(Files::isDirectory);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Failed to get user directories: " + parent.toAbsolutePath().toString(), e);
            return Stream.empty();
        }
    }

    private Stream<Path> getExpiredFiles(@NotNull Path dir) {
        try {
            return Files.list(dir)
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".yml"))
                    .filter(this::isExpired);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE,
                    "Failed to get user files (" + dir.toAbsolutePath().toString() + ")", e);
            return Stream.empty();
        }
    }
}
