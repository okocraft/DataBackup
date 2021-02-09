package net.okocraft.databackup.data.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

final class ItemStackSerializer {

    private static final boolean isPaper = checkPaper();
    private static final String EMPTY = "";
    private static final byte[] EMPTY_BYTE = new byte[0];
    private static final ItemStack AIR = new ItemStack(Material.AIR);

    static @NotNull String serialize(@NotNull ItemStack itemStack) {
        if (itemStack.getType().isAir()) {
            return EMPTY;
        }

        byte[] bytes;
        if (isPaper) {
            bytes = serializeByPaperMethod(itemStack);
        } else {
            bytes = serializeByBukkitMethod(itemStack);
        }

        if (bytes.length != 0) {
            return Base64.getEncoder().encodeToString(bytes);
        } else {
            return EMPTY;
        }
    }

    static @NotNull ItemStack deserialize(@NotNull String data) {
        if (data.isEmpty()) {
            return AIR;
        }

        var bytes = Base64.getDecoder().decode(data);

        if (isPaper) {
            return deserializeByPaperMethod(bytes);
        } else {
            return deserializeByBukkitMethod(bytes);
        }
    }

    private static byte[] serializeByPaperMethod(@NotNull ItemStack itemStack) {
        return itemStack.serializeAsBytes();
    }

    private static byte[] serializeByBukkitMethod(@NotNull ItemStack itemStack) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {
            dataOutput.writeObject(itemStack);
            return outputStream.toByteArray();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            return EMPTY_BYTE;
        }
    }

    private static @NotNull ItemStack deserializeByPaperMethod(byte[] bytes) {
        return ItemStack.deserializeBytes(bytes);
    }

    private static @NotNull ItemStack deserializeByBukkitMethod(byte[] bytes) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
            return (ItemStack) dataInput.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return AIR;
        }
    }

    private static boolean checkPaper() {
        try {
            ItemStack.class.getMethod("serializeAsBytes");
            ItemStack.class.getMethod("deserializeBytes", byte[].class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
