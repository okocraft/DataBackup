package net.okocraft.databackup.data.impl;

import com.github.siroshun09.configapi.bukkit.BukkitYaml;
import com.github.siroshun09.mccommand.bukkit.sender.BukkitSender;
import net.okocraft.databackup.data.BackupData;
import net.okocraft.databackup.data.DataType;
import net.okocraft.databackup.data.ItemSearchable;
import net.okocraft.databackup.gui.DataBackupGui;
import net.okocraft.databackup.lang.DefaultMessage;
import net.okocraft.databackup.lang.MessageProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class InventoryData implements DataType<List<ItemStack>> {

    private static final int ARRAY_LENGTH = 41;

    @Override
    public @NotNull String getName() {
        return "inventory";
    }

    @Override
    public @NotNull Function<Player, BackupData<List<ItemStack>>> backup() {
        return player -> {
            var array = player.getInventory().getContents();
            var listed = new ArrayList<ItemStack>(ARRAY_LENGTH);
            for (int i = 0; i < ARRAY_LENGTH; i++) {
                var item = array[i];
                listed.add(item != null ? item : new ItemStack(Material.AIR));
            }
            return ItemSearchable.createData(player.getUniqueId(), listed);
        };
    }

    @Override
    public @NotNull BiConsumer<BackupData<List<ItemStack>>, Player> rollback() {
        return (itemData, player) -> {
            var array = new ItemStack[ARRAY_LENGTH];
            if (itemData.get().size() == array.length) {
                for (int i = 0; i < array.length; i++) {
                    array[i] = itemData.get().get(i);
                }
                player.getInventory().setContents(array);
                player.updateInventory();
            } else {
                throw new IllegalArgumentException();
            }
        };
    }

    @Override
    public @NotNull BiConsumer<BackupData<List<ItemStack>>, BukkitSender> show() {
        return (itemData, player) -> {
            if (player.getCommandSender() instanceof Player) {
                DataBackupGui.openInventoryGui(
                        (Player) player.getCommandSender(),
                        itemData.get().toArray(new ItemStack[0]),
                        itemData.getOwner(),
                        BackupTimeValue.toLocalDateTime(itemData.getBackupTime())
                );
            } else {
                MessageProvider.sendMessageWithPrefix(DefaultMessage.COMMAND_ONLY_PLAYER, player);
            }
        };
    }

    @Override
    public @NotNull Function<BukkitYaml, BackupData<List<ItemStack>>> load() {
        return yaml -> {
            List<ItemStack> items = new ArrayList<>(ARRAY_LENGTH);
            for (int i = 0; i < ARRAY_LENGTH; i++) {
                var oldPath = getName() + "." + i;
                if (yaml.get(oldPath) != null) {
                    items.add(yaml.getItemStack(oldPath));
                } else {
                    var data = yaml.getString(getName() + "-2." + i);
                    var item = ItemStackSerializer.deserialize(data);
                    items.add(item);
                }
            }
            return ItemSearchable.createData(
                    UUIDValue.INSTANCE.getValue(yaml),
                    BackupTimeValue.INSTANCE.getValue(yaml),
                    items
            );
        };
    }

    @Override
    public @NotNull BiConsumer<BackupData<List<ItemStack>>, BukkitYaml> save() {
        return (itemData, yaml) -> {
            for (int i = 0; i < ARRAY_LENGTH || i < itemData.get().size(); i++) {
                var item = itemData.get().get(i);
                var data = ItemStackSerializer.serialize(item);
                if (!data.isEmpty()) {
                    yaml.set(getName() + "-2." + i, data);
                }
            }
        };
    }
}
