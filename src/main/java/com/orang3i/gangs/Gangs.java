package com.orang3i.gangs;

import com.orang3i.gangs.commands.GetGang;
import com.orang3i.gangs.commands.GetGangMembers;
import com.orang3i.gangs.commands.SetGang;
import com.orang3i.gangs.commands.Tester;
import com.orang3i.gangs.database.GangsService;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
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

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
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
        this.adventure = BukkitAudiences.create(this);
        getCommand("tester").setExecutor(new Tester(this));
        getCommand("setgang").setExecutor(new SetGang(this));
        getCommand("getgang").setExecutor(new GetGang(this));
        getCommand("getgangmembers").setExecutor(new GetGangMembers(this));

            try {
                System.out.println("me here yuh" + datafolder);

                gangsService = new GangsService();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Cannot connect to database" + e.getMessage());
                Bukkit.getPluginManager().disablePlugin(this);
            }

    }
    public GangsService getService() {
        return gangsService;
    }

    public static Gangs getPlugin() {
        return instance;
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
