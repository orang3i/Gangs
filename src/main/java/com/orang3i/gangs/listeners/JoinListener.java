package com.orang3i.gangs.listeners;

import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.commands.GangsCommands;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class JoinListener implements Listener {

    private final Gangs gangs;
    public JoinListener(Gangs gangs){
        this.gangs = gangs;
    }

    public static Boolean vaultExists = true;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
        try {
            GangsCommands.tpCooldowns.put(event.getPlayer().getName(), (long) -1);
            GangsCommands.challengeCooldowns.put(event.getPlayer().getName(), (long) -1);
            GangsCommands.allyreqCooldowns.put(event.getPlayer().getName(), (long) -1);
            GangsCommands.inviteCooldowns.put(event.getPlayer().getName(), (long) -1);
            if(!gangs.getService().playerExists(event.getPlayer())){
                gangs.getService().addPlayer((event.getPlayer()));
            }
            if(!vaultExists){
                if(event.getPlayer().hasPermission("gangs.admin")){
                    gangs.adventure().player(event.getPlayer()).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>Vault Plugin Required For Gangs Not Installed Some Plugins May Produce Error!\nhttps://www.spigotmc.org/resources/vault.34315\n[CLICK ME]</gradient>").clickEvent(ClickEvent.openUrl("https://www.spigotmc.org/resources/vault.34315")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
