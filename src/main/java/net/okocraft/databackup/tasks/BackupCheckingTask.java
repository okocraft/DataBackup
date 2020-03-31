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
        int deleted;

        try {
            deleted = checkDir();
        } catch (IOException e) {
            DataBackup.get().getLogger().severe("Failed to complete checking task.");
            e.printStackTrace();
            return;
        }

        printResult(deleted);

        long took = System.currentTimeMillis() - startTime;
        DataBackup.get().getLogger().info("Backup files check task was completed. (" + took + "ms)");
    }

    private int checkDir() throws IOException {
        List<Path> dirPaths = getUserDirs();
        int count = 0;

        for (Path dir : dirPaths) {
            count += checkUserDir(dir);
        }

        return count;
    }

    private int checkUserDir(Path dir) throws IOException {
        List<Path> files = getBackupFiles(dir);
        int count = 0;

        for (Path file : files) {
            Files.deleteIfExists(file);
            DataBackup.get().debug("Deleted the file:" + file.toAbsolutePath().toString());
            count++;
        }

        return count;
    }

    private List<Path> getUserDirs() throws IOException {
        return Files.list(PlayerData.getDataDir())
                .filter(Files::isDirectory)
                .collect(Collectors.toList());
    }

    private List<Path> getBackupFiles(Path dir) throws IOException {
        return Files.list(dir)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".yml"))
                .filter(this::isExpired)
                .collect(Collectors.toList());
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

    private void printResult(int deleted) {
        if (1 < deleted) {
            DataBackup.get().getLogger().info(deleted + " files have been deleted.");
        } else if (deleted == 0) {
            DataBackup.get().getLogger().info("No file has been deleted.");
        } else if (deleted == 1) {
            DataBackup.get().getLogger().info("1 file has been deleted.");
        }
    }
}
