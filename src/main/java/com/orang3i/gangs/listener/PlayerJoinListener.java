package com.orang3i.gangs.listener;

import com.orang3i.gangs.Gangs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        Gangs.getPluginStatic().getDAO().initPlayer(player.getUniqueId());
    }
}
