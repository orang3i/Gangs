package com.orang3i.gangs;

import com.orang3i.gangs.command.CommandRoot;
import com.orang3i.gangs.database.Connector;
import com.orang3i.gangs.database.DAO;
import com.orang3i.gangs.test.AdventureTests;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class Gangs extends JavaPlugin {

    private static Gangs pluginStatic;
    private DAO dao;

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new AdventureTests(this), this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        pluginStatic = this;
        new Connector().init(this);
        dao = new DAO();
        registerEvents();
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(CommandRoot.getGangsCommandRoot().build());});
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
