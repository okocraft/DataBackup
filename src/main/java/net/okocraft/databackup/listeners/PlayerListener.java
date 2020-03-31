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
        if (!UserList.get().getUsers().contains(e.getPlayer().getName())) {
            UserList.get().update();
        }
        new PlayerData(e.getPlayer()).save();
    }
}
