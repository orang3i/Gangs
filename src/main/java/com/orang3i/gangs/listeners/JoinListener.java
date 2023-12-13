package com.orang3i.gangs.listeners;

import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.commands.GangsCommands;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class JoinListener implements Listener {

    private final Gangs gangs;
    public JoinListener(Gangs gangs){
        this.gangs = gangs;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        try {
            GangsCommands.tpCooldowns.put(event.getPlayer().getName(), (long) -1);
            if(!gangs.getService().playerExists(event.getPlayer())){
                System.out.println("player does not exist yet");
                gangs.getService().addPlayer((event.getPlayer()));
                System.out.println("player hopefully exists now ");


            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
