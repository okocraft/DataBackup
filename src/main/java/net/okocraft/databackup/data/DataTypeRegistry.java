package net.okocraft.databackup.data;

import com.github.siroshun09.mccommand.common.argument.Argument;
import com.github.siroshun09.mccommand.common.argument.parser.ArgumentParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DataTypeRegistry {

    private final Set<DataType<?>> registeredDataType = new HashSet<>();
    private final ArgumentParser<DataType<?>> argumentParser =
            arg -> registeredDataType.stream()
                    .filter(d -> d.getName().equalsIgnoreCase(arg.get()))
                    .findFirst().orElse(null);

    public synchronized void registerDataType(@NotNull DataType<?> type) {
        registeredDataType.add(type);
    }

    public synchronized void registerDataType(@NotNull Collection<DataType<?>> types) {
        registeredDataType.addAll(types);
    }

    public synchronized void unregisterDataType(@NotNull DataType<?> type) {
        registeredDataType.remove(type);
    }

    public synchronized void unregisterDataType(@NotNull Collection<DataType<?>> types) {
        registeredDataType.removeAll(types);
    }

    @NotNull
    @Unmodifiable
    public Set<DataType<?>> getRegisteredDataType() {
        return Set.copyOf(registeredDataType);
    }

    @NotNull
    public Optional<DataType<?>> get(@NotNull Argument argument) {
        return argumentParser.parseOptional(argument);
    }
}
