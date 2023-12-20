package com.orang3i.gangs;

import com.orang3i.gangs.commands.*;
import com.orang3i.gangs.database.GangsService;
import com.orang3i.gangs.listeners.EntityDamageByEntityListener;
import com.orang3i.gangs.listeners.JoinListener;
import com.orang3i.gangs.listeners.PlayerChatListener;
import com.orang3i.gangs.listeners.PlayerMoveEventListener;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
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

    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;


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
        getCommand("gangs").setExecutor(new GangsCommands(this));
        getCommand("gangs").setTabCompleter(new GangsTabCompleter((this)));
        getCommand("adventurecommand").setExecutor(new AdventureCommand(this));
    }

    public void registerEvents(){
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveEventListener(this), this);
    }

    public void connectToDatabase(){
        try {
            gangsService = new GangsService();
        } catch (SQLException e) {
            getLogger().severe(String.format("Cannot Connect To Database!", getDescription().getName()));
            getLogger().severe(e.getStackTrace().toString());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    public void testLogger(){
        String startup = """
                  
                  ____   ____  ____    ____  _____     ____  _      __ __   ____  ____  ____         ___  ____    ____  ____   _        ___  ___  \s
                 /    | /    ||    \\  /    |/ ___/    |    \\| |    |  |  | /    ||    ||    \\       /  _]|    \\  /    ||    \\ | |      /  _]|   \\ \s
                |   __||  o  ||  _  ||   __(   \\_     |  o  ) |    |  |  ||   __| |  | |  _  |     /  [_ |  _  ||  o  ||  o  )| |     /  [_ |    \\\s
                |  |  ||     ||  |  ||  |  |\\__  |    |   _/| |___ |  |  ||  |  | |  | |  |  |    |    _]|  |  ||     ||     || |___ |    _]|  D  |
                |  |_ ||  _  ||  |  ||  |_ |/  \\ |    |  |  |     ||  :  ||  |_ | |  | |  |  |    |   [_ |  |  ||  _  ||  O  ||     ||   [_ |     |
                |     ||  |  ||  |  ||     |\\    |    |  |  |     ||     ||     | |  | |  |  |    |     ||  |  ||  |  ||     ||     ||     ||     |
                |___,_||__|__||__|__||___,_| \\___|    |__|  |_____| \\__,_||___,_||____||__|__|    |_____||__|__||__|__||_____||_____||_____||_____|""";


        getLogger().info(startup);
    }

    public void initVault(){
        if (!setupEconomy() ) {
            getLogger().severe(String.format("Vault dependency not found some commands may produce error!", getDescription().getName()));
            //getServer().getPluginManager().disablePlugin(this);
            JoinListener.vaultExists = false;
            return;
        }
        setupPermissions();
        setupChat();
    }
    @Override
    public void onEnable() {
        init();
        createConfig();
        registerCommands();
        registerEvents();
        connectToDatabase();
        initVault();
        testLogger();
    }
    public GangsService getService() {
        return gangsService;
    }

    public static Gangs getPlugin() {
        return instance;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }


    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));

    }
}
