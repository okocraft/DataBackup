package net.okocraft.databackup.tasks;

import com.github.siroshun09.sirolibrary.file.FileUtil;
import net.okocraft.databackup.Configuration;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.data.PlayerData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class BackupCheckingTask implements Runnable {

    @Override
    public void run() {
        if (FileUtil.isNotExist(PlayerData.getDataDir())) return;

        DataBackup.get().getLogger().info("Checking backup files task is starting...");
        long startTime = System.currentTimeMillis();

        List<Path> dirPaths;
        try {
            dirPaths = Files.list(PlayerData.getDataDir())
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            DataBackup.get().getLogger().severe("Failed to complete checking task.");
            e.printStackTrace();
            return;
        }

        int count = 0;

        for (Path dir : dirPaths) {
            try {
                List<Path> files = Files.list(dir)
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".yml"))
                        .filter(this::isExpired)
                        .collect(Collectors.toList());

                for (Path file : files) {
                    Files.deleteIfExists(file);
                    DataBackup.get().getLogger().info("Deleted the file:" + file.toAbsolutePath().toString());
                    count++;
                }
            } catch (IOException e) {
                DataBackup.get().getLogger().severe("Failed to complete checking task.");
                e.printStackTrace();
                return;
            }
        }

        long took = System.currentTimeMillis() - startTime;
        DataBackup.get().getLogger().info(count + " files have been deleted.");
        DataBackup.get().getLogger().info("Checking backup files task was completed. (" + took + "ms)");
    }

    private boolean isExpired(@NotNull Path path) {
        try {
            return Configuration.get().getBackupPeriod()
                    <= Duration.between(Files.getLastModifiedTime(path).toInstant(), Instant.now()).toDays();
        } catch (IOException e) {
            DataBackup.get().getLogger().severe("Failed to check file, ignore " + path.toAbsolutePath().toString());
            e.printStackTrace();
            return false;
        }
    }

}
