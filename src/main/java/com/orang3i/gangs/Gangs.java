package com.orang3i.gangs;

import com.orang3i.gangs.command.CommandRoot;
import com.orang3i.gangs.database.Connector;
import com.orang3i.gangs.database.DAO;
import com.orang3i.gangs.listener.PlayerJoinListener;
import com.orang3i.gangs.test.AdventureTests;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class Gangs extends JavaPlugin {

    private static Gangs pluginStatic;
    private DAO dao;

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new AdventureTests(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        pluginStatic = this;
        new Connector().init(this);
        dao = new DAO();
        try {
            dao.createPlayersTable();
            dao.createGangsTable();
            dao.initGangs();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        registerEvents();
        CommandRoot.register();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(CommandRoot.gangsCommandRoot.build());});
        getLogger().info(String.format("Gangs Plugin Version %s Enabled", getPluginMeta().getVersion()));
    }

    @Override
    public void onDisable() {
        Connector.closeConnection();
        getLogger().info(String.format("Gangs Plugin Version %s Disabled", getPluginMeta().getVersion()));
    }

    public static Gangs getPluginStatic() {
        return pluginStatic;
    }

    public DAO getDAO() {
        return dao;
    }
}
