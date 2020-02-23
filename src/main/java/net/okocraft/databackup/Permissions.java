package net.okocraft.databackup;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class Permissions {
    private final static String PREFIX = "databackup.";

    public final static Permission CMD_BACKUP = createOpPerm("cmd.backup");
    public final static Permission CMD_ROLLBACK = createOpPerm("cmd.rollback");
    public final static Permission CMD_SHOW_BACKUP = createOpPerm("cmd.show");

    @NotNull
    @Contract("_ -> new")
    private static Permission createOpPerm(@NotNull String name) {
        Permission perm = new Permission(PREFIX + name, PermissionDefault.OP);
        // if (!BukkitUtil.existPermission(perm)) BukkitUtil.addPermission(perm);
        return perm;
    }
}