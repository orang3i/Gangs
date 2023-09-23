package com.orang3i.gangs;

import com.orang3i.gangs.commands.*;
import com.orang3i.gangs.database.GangsService;
import com.orang3i.gangs.listeners.EntityDamageByEntityListener;
import com.orang3i.gangs.listeners.JoinListener;
import com.orang3i.gangs.listeners.PlayerChatListener;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import static net.kyori.adventure.text.Component.text;

public final class Gangs extends JavaPlugin {
    private BukkitAudiences adventure;
    private GangsService gangsService;

    private static Gangs instance;

    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    public void createConfig(){
        File datafolder = new File(this.getDataFolder() + "/");
        File config = new File(getDataFolder(), "config.yml");
        if(!datafolder.exists()){
            datafolder.mkdir();
            try {
                InputStream stream = this.getResource("config.yml");
                FileUtils.copyInputStreamToFile(stream, new File(this.getDataFolder(),"config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!config.isFile()) {
            try {
                InputStream stream = this.getResource("config.yml");
                FileUtils.copyInputStreamToFile(stream, new File(this.getDataFolder(),"config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    public void init(){
        instance = this;
        this.adventure = BukkitAudiences.create(this);

    }


    public void registerCommands(){
        getCommand("tester").setExecutor(new Tester(this));
        getCommand("setgang").setExecutor(new SetGang(this));
        getCommand("getgang").setExecutor(new GetGang(this));
        getCommand("getgangmembers").setExecutor(new GetGangMembers(this));
        getCommand("gangs").setExecutor(new GangsCommands(this));
        getCommand("gangs").setTabCompleter(new GangsTabCompleter((this)));
        getCommand("adventurecommand").setExecutor(new AdventureCommand(this));
    }

    public void registerEvents(){
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
    }

    public void connectToDatabase(){
        try {
            gangsService = new GangsService();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Cannot connect to database" + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void testLogger(){
        System.out.println("lmao");
    }
    @Override
    public void onEnable() {
        init();
        createConfig();
        registerCommands();
        registerEvents();
        connectToDatabase();
        testLogger();
    }
    public GangsService getService() {
        return gangsService;
    }

    public static Gangs getPlugin() {
        return instance;
    }


    @Override
    public void onDisable() {

    }
}
