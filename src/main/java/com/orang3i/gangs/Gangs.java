package com.orang3i.gangs;

import com.orang3i.gangs.commands.*;
import com.orang3i.gangs.database.GangsService;
import com.orang3i.gangs.listeners.EntityDamageByEntityListener;
import com.orang3i.gangs.listeners.JoinListener;
import com.orang3i.gangs.listeners.PlayerChatListener;
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

    public void initVault(){
        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
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
        testLogger();
        initVault();
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

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            getLogger().info("Only players are supported for this Example Plugin, but you should not do this!!!");
            return true;
        }

        Player player = (Player) sender;

        if(command.getLabel().equals("test-economy")) {
            // Lets give the player 1.05 currency (note that SOME economic plugins require rounding!)
            sender.sendMessage(String.format("You have %s", econ.format(econ.getBalance(player.getName()))));
            EconomyResponse r = econ.depositPlayer(player, 1.05);
            if(r.transactionSuccess()) {
                sender.sendMessage(String.format("You were given %s and now have %s", econ.format(r.amount), econ.format(r.balance)));
            } else {
                sender.sendMessage(String.format("An error occured: %s", r.errorMessage));
            }
            return true;
        } else if(command.getLabel().equals("test-permission")) {
            // leets test if user has the node "example.plugin.awesome" to determine if they are awesome or just suck
            if(perms.has(player, "example.plugin.awesome")) {
                sender.sendMessage("You are awesome!");
            } else {
                sender.sendMessage("You suck!");
            }
            return true;
        } else {
            return false;
        }
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
