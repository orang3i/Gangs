package com.orang3i.gangs.listeners;

import com.orang3i.gangs.Gangs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerChatListener implements Listener {
    private final Gangs gangs;
    public PlayerChatListener(Gangs gangs){
        this.gangs = gangs;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) throws SQLException {
        if(gangs.getService().getPlayerStats(event.getPlayer()).getGangchat().equals("true")){
            event.setCancelled(true);
            List<String[]> gang_members = gangs.getService().getRawPlayerResults("gang", gangs.getService().getPlayerStats(event.getPlayer()).getGang());

            gang_members.forEach(strings -> {
                Player ranker = null;
                if (Bukkit.getPlayerExact(Bukkit.getPlayer(UUID.fromString(strings[0])).getName()) != null) {

                    ranker = Bukkit.getPlayer(Bukkit.getPlayer(UUID.fromString(strings[0])).getName());
                }
                if(ranker!= null) {
                    //ranker.sendMessage(event.getMessage());
                    try {
                        gangs.adventure().player(ranker).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>["+gangs.getService().getPlayerStats(event.getPlayer()).getGang()+"] <"+event.getPlayer().getName()+"> "+event.getMessage()+"</gradient>"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        if(gangs.getService().getPlayerStats(event.getPlayer()).getAllychat().equals("true")){
            event.setCancelled(true);
            ArrayList<String> allies = gangs.getService().getAllies(gangs.getService().getPlayerStats(event.getPlayer().getUniqueId()).getGang());
            AtomicReference<String> ally_members = new AtomicReference<>("");
            allies.forEach(ally -> {
                try {
                    List<String[]> members = gangs.getService().getRawPlayerResults("gang", ally);
                    members.forEach( member ->{
                        ally_members.set(ally_members.get() + member[0] + ",");
                    });
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            String allymems = ally_members.toString();
            //split the string into an array
            ArrayList<String> tmp= new ArrayList<String>( Arrays.asList(allymems.split(",")));
            String[] str = new String[tmp.size()];
            int i;
            for (i=0;i<tmp.size();i++){
                String test = tmp.get(i).trim();
                str[i] = test;
            }
            ArrayList<String> currentAllies = new ArrayList<>(Arrays.asList(str));
            currentAllies.forEach(strings -> {
                Player ranker = null;
                if (Bukkit.getPlayerExact(Bukkit.getPlayer(UUID.fromString(strings)).getName()) != null) {

                    ranker = Bukkit.getPlayer(Bukkit.getPlayer(UUID.fromString(strings)).getName());
                }
                if(ranker!= null) {
                    //ranker.sendMessage(event.getMessage());
                    try {
                        gangs.adventure().player(ranker).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>["+gangs.getService().getPlayerStats(event.getPlayer()).getGang()+"] <"+event.getPlayer().getName()+"> "+event.getMessage()+"</gradient>"));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}
