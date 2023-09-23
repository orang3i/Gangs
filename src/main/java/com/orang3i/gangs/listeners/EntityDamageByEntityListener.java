package com.orang3i.gangs.listeners;

import com.orang3i.gangs.Gangs;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.sql.SQLException;
import java.util.List;

public class EntityDamageByEntityListener implements Listener {
    private final Gangs gangs;
    public EntityDamageByEntityListener(Gangs gangs){
        this.gangs = gangs;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) throws SQLException {
        Boolean forceFriendlyFire = gangs.getConfig().getBoolean("gangs.force-friendly-fire");
        Boolean forceFriendlyFireDisable = gangs.getConfig().getBoolean("gangs.force-friendly-fire-disable");
        Boolean friendlyFireGang = gangs.getConfig().getBoolean("gangs.friendly-fire-gang");
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            if (forceFriendlyFire) {
            } else {
                if (forceFriendlyFireDisable) {
                    if (event.getEntity() instanceof Player) {
                        if (gangs.getService().getPlayerStats((Player) event.getDamager()).getGang().equals(gangs.getService().getPlayerStats((Player) event.getEntity()).getGang())) {
                            event.setCancelled(true);
                            gangs.adventure().player((Player) event.getDamager()).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>friendly fire is off</gradient>"));
                        }
                    }
                } else {
                    if (friendlyFireGang) {
                        if (gangs.getService().getServerStats(gangs.getService().getPlayerStats((Player) event.getDamager()).getGang()).getFriendlyFire().equals("true")) {
                            if (gangs.getService().getPlayerStats((Player) event.getDamager()).getGang().equals(gangs.getService().getPlayerStats((Player) event.getEntity()).getGang())) {
                                event.setCancelled(true);
                                gangs.adventure().player((Player) event.getDamager()).sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#8e28ed:#f52c2c>friendly fire is off</gradient>"));
                            }
                        }
                    }
                }
            }
        }
    }
}
