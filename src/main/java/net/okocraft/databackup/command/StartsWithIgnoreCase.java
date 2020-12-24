package net.okocraft.databackup.command;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.function.Predicate;

public final class StartsWithIgnoreCase {

    public static @NotNull Predicate<String> prefix(@NotNull String prefix) {
        return str -> str.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT));
    }
}
