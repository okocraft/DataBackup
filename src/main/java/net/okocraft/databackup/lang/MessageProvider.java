package net.okocraft.databackup.lang;

import com.github.siroshun09.mcmessage.MessageReceiver;
import com.github.siroshun09.mcmessage.builder.PlainTextBuilder;
import com.github.siroshun09.mcmessage.loader.LanguageLoader;
import com.github.siroshun09.mcmessage.message.KeyedMessage;
import com.github.siroshun09.mcmessage.message.Message;
import com.github.siroshun09.mcmessage.translation.Translation;
import com.github.siroshun09.mcmessage.translation.TranslationRegistry;
import net.okocraft.databackup.DataBackup;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class MessageProvider {

    private static final TranslationRegistry TRANSLATION_REGISTRY = TranslationRegistry.create();

    public static @NotNull Message getMessage(@NotNull DefaultMessage defaultMessage, @NotNull MessageReceiver receiver) {
        return TRANSLATION_REGISTRY.getMessage(defaultMessage, receiver.getLocale());
    }

    public static @NotNull PlainTextBuilder getBuilder(@NotNull DefaultMessage defaultMessage, @NotNull MessageReceiver receiver) {
        return getMessage(defaultMessage, receiver)
                .toPlainTextBuilder()
                .setColorize(true);
    }

    public static @NotNull PlainTextBuilder getBuilderWithPrefix(@NotNull DefaultMessage defaultMessage, @NotNull MessageReceiver receiver) {
        return getBuilder(defaultMessage, receiver)
                .addPrefix(getPrefix(receiver));
    }

    public static void sendMessage(@NotNull DefaultMessage defaultMessage, @NotNull MessageReceiver receiver) {
        getBuilder(defaultMessage, receiver).send(receiver);
    }

    public static void sendMessageWithPrefix(@NotNull DefaultMessage defaultMessage, @NotNull MessageReceiver receiver) {
        getBuilderWithPrefix(defaultMessage, receiver).setColorize(true).send(receiver);
    }

    private static @NotNull Message getPrefix(@NotNull MessageReceiver receiver) {
        return getMessage(DefaultMessage.PREFIX, receiver);
    }

    public static void sendNoPermission(@NotNull MessageReceiver receiver, @NotNull String node) {
        getBuilderWithPrefix(DefaultMessage.COMMAND_NO_PERMISSION, receiver)
                .replace(Placeholders.PERMISSION, node)
                .send(receiver);
    }

    public static void reloadLanguages(@NotNull DataBackup plugin) throws IOException {
        TRANSLATION_REGISTRY.unregisterAll();
        var path = plugin.getDataFolder().toPath();
        var defLang = path.resolve("ja_JP.properties");
        var logger = plugin.getLogger();

        loadDefaultLanguage(defLang, logger);

        loadCustomLanguages(path, defLang, logger);

        plugin.getLogger().info(
                "Loaded languages: " +
                        TRANSLATION_REGISTRY.getTranslations()
                                .stream()
                                .map(Translation::getLocale)
                                .map(Locale::toString)
                                .collect(Collectors.joining(", "))
        );
    }

    private static void loadDefaultLanguage(@NotNull Path defLang, @NotNull Logger logger) throws IOException {
        if (Files.exists(defLang)) {
            loadLanguageFile(defLang, logger);
        } else {
            try (BufferedWriter writer = Files.newBufferedWriter(defLang, StandardCharsets.UTF_8, StandardOpenOption.CREATE)) {
                StringBuilder builder = new StringBuilder();
                for (KeyedMessage defMsg : DefaultMessage.values()) {
                    builder.setLength(0);
                    builder.append(defMsg.getKey()).append('=').append(defMsg.get());
                    writer.write(builder.toString());
                    writer.newLine();
                }
            }

            TRANSLATION_REGISTRY.register(
                    Translation.of(new Locale("ja_JP"), Set.of(DefaultMessage.values()))
            );
        }
    }

    private static void loadCustomLanguages(@NotNull Path root, @NotNull Path defLangToIgnore, @NotNull Logger logger) throws IOException {
        Files.list(root)
                .filter(Files::isRegularFile)
                .filter(p -> {
                    try {
                        return !Files.isSameFile(p, defLangToIgnore);
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        return true;
                    }
                })
                .filter(p -> p.toString().endsWith(".properties"))
                .forEach(p -> {
                    try {
                        loadLanguageFile(p, logger);
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Could not load " + p.toString(), e);
                    }
                });
    }

    private static void loadLanguageFile(@NotNull Path file, @NotNull Logger logger) throws IOException {
        var loader = LanguageLoader.fromPropertiesFile(file);
        var locale = loader.parseLocaleFromFileName();

        if (locale != null) {
            loader.load().forEach(invalid -> logger.warning(invalid.toString()));
            TRANSLATION_REGISTRY.register(loader.toTranslation(locale));
        } else {
            logger.warning("Unknown language:" + file.getFileName().toString());
        }
    }
}
