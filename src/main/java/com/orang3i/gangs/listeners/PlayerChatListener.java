package com.orang3i.gangs.listeners;

import com.orang3i.gangs.Gangs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PlayerChatListener implements Listener {
    private final Gangs gangs;
    public PlayerChatListener(Gangs gangs){
        this.gangs = gangs;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) throws SQLException {
        if(gangs.getService().getPlayerStats(event.getPlayer()).getGangchat().equals("true")){
            System.out.println("sdsdss");
            event.setCancelled(true);
            List<String[]> gang_members = gangs.getService().getRawPlayerResults("gang", gangs.getService().getPlayerStats(event.getPlayer()).getGang());

            gang_members.forEach(strings -> {
                Player ranker = null;
                if (Bukkit.getPlayerExact(Bukkit.getPlayer(UUID.fromString(strings[0])).getDisplayName()) != null) {

                    ranker = Bukkit.getPlayer(Bukkit.getPlayer(UUID.fromString(strings[0])).getDisplayName());
                }
                if(ranker!= null) {
                    //ranker.sendMessage(event.getMessage());
                    try {
                        gangs.adventure().player(ranker).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>["+gangs.getService().getPlayerStats(event.getPlayer()).getGang()+"] <"+event.getPlayer().getDisplayName()+"> "+event.getMessage()+"</gradient>"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}
