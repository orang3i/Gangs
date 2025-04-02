package com.orang3i.gangs.test;

import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.formatter.MiniMessageDeserializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.sql.SQLException;

public class AdventureTests implements Listener {

    private final Gangs plugin;

    public AdventureTests(Gangs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Component welcome = MiniMessageDeserializer.mm("Hello, you joined orang3i's test server!",true).clickEvent(ClickEvent.callback(audience -> {
            try {
                secretMessage("You found the secret",player);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
        player.sendMessage(welcome);
    }

    private void secretMessage(String message , Player player) throws SQLException {
        player.sendMessage(MiniMessageDeserializer.mm(message));
        plugin.getDAO().createTestTable();
        plugin.getDAO().insertData();
        plugin.getDAO().getData();
    }

}
