package net.okocraft.databackup.listeners;

import net.okocraft.databackup.UserList;
import net.okocraft.databackup.data.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerListener implements Listener {
    private final static PlayerListener INSTANCE = new PlayerListener();

    private PlayerListener() {
    }

    public static PlayerListener get() {
        return INSTANCE;
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        UserList.get().addUser(e.getPlayer());
        new PlayerData(e.getPlayer()).save();
    }
}
