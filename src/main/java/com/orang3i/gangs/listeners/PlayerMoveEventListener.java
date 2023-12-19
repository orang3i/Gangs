package com.orang3i.gangs.listeners;

import com.orang3i.gangs.Gangs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerMoveEventListener implements Listener {

    private static Gangs gangs;
    public PlayerMoveEventListener(Gangs gangs){
        PlayerMoveEventListener.gangs = gangs;
    }

    public static ArrayList<Player> teleport = new ArrayList<Player>() {
    };



    public static void appendTeleport(Player player){

        if(!teleport.contains(player)){
        teleport.add(player);}
    }

    public static void popTeleport(Player player){
        teleport.remove(player);
    }

    public List<Player> getTeleport(){
    return teleport;
    }

    @EventHandler
    public static void onPlayerMovement(PlayerMoveEvent event){
        Player player = event.getPlayer();


        if(teleport.contains(player)) {
            if(event.getFrom().getX() != event.getTo().getX() || event.getFrom().getZ() != event.getTo().getZ()) {
                gangs.adventure().player(player).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>You moved teleport cancelled</gradient>"));
                teleport.remove(player);

            }
    }

}}
