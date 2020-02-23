package net.okocraft.databackup.tasks;

import com.github.siroshun09.sirolibrary.file.FileUtil;
import net.okocraft.databackup.Configuration;
import net.okocraft.databackup.DataBackup;
import net.okocraft.databackup.data.PlayerData;

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

        List<Path> dirPaths;
        try {
            dirPaths = Files.list(PlayerData.getDataDir())
                    .filter(Files::isDirectory)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for (Path dir : dirPaths) {
            try {
                List<Path> files = Files.list(dir)
                        .filter(Files::isRegularFile)
                        .filter(p -> p.toString().endsWith(".yml"))
                        .filter(this::isExpired)
                        .collect(Collectors.toList());

                for (Path file : files) {
                    Files.deleteIfExists(file);
                    DataBackup.get().getLogger().info("ファイルを削除しました: " + file.toAbsolutePath().toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isExpired(Path path) {
        try {
            return Configuration.get().getBackupPeriod()
                    < Duration.between(Files.getLastModifiedTime(path).toInstant(), Instant.now()).toDays();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
