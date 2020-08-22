package net.okocraft.databackup.util;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Formatter {

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh-mm-ss");
    private final static DateTimeFormatter FILE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss");

    @NotNull
    public static String datetime(@NotNull LocalDateTime time) {
        return FORMATTER.format(time);
    }

    @NotNull
    public static String fileName() {
        return FILE_FORMATTER.format(LocalDateTime.now()) + ".yml";
    }
}
