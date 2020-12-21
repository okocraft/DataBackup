package net.okocraft.databackup.data;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class ItemSearchableImpl extends BackupDataImpl<List<ItemStack>> implements ItemSearchable {

    ItemSearchableImpl(@NotNull UUID owner, long backupTime, @NotNull List<ItemStack> data) {
        super(owner, backupTime, data);
    }

    @Override
    public @NotNull List<ItemStack> search(@NotNull Material material) {
        var items = get();
        return items.stream()
                .filter(i -> i.getType() == material)
                .collect(Collectors.toUnmodifiableList());
    }
}
