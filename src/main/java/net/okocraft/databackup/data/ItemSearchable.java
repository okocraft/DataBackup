package net.okocraft.databackup.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ItemSearchable extends BackupData<List<ItemStack>> {

    @Contract("_, _ -> new")
    static @NotNull ItemSearchable createData(@NotNull UUID owner, @NotNull List<ItemStack> data) {
        return new ItemSearchableImpl(owner, Instant.now().toEpochMilli(), data);
    }

    @Contract("_, _, _ -> new")
    static @NotNull ItemSearchable createData(@NotNull UUID owner, long backupTime, @NotNull List<ItemStack> data) {
        return new ItemSearchableImpl(owner, backupTime, data);
    }

    @NotNull List<ItemStack> search(@NotNull Material material);
}
