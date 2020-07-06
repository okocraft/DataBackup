package old_ver.data;

import com.github.siroshun09.sirolibrary.config.BukkitYaml;
import old_ver.DataBackup;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public abstract class AbstractData {
    protected final static Path BACKUP_DIR = DataBackup.get().getDataFolder().toPath().resolve("backups");
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss");
    protected final UUID uuid;
    protected final LocalDateTime dateTime;

    protected AbstractData(@NotNull UUID uuid, @NotNull LocalDateTime dateTime) {
        this.uuid = uuid;
        this.dateTime = dateTime;
    }

    @NotNull
    protected static UUID loadUUID(@NotNull BukkitYaml yaml) {
        return UUID.fromString(yaml.getString("uuid", ""));
    }

    @NotNull
    protected static LocalDateTime loadDateTime(@NotNull BukkitYaml yaml) {
        return LocalDateTime.from(DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse(yaml.getString("dateTime", "")));
    }

    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    @NotNull
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @NotNull
    public String getFormattedDateTime() {
        return FORMATTER.format(dateTime);
    }

    @NotNull
    public abstract Path getBackupFilePath();

    public abstract void save();

    protected void saveUUID(@NotNull BukkitYaml yaml) {
        yaml.getConfig().set("uuid", uuid.toString());
    }

    protected void saveDateTime(@NotNull BukkitYaml yaml) {
        yaml.getConfig().set("dateTime", DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(dateTime));
    }
}
