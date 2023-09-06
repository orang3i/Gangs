package com.orang3i.gangs;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static net.kyori.adventure.text.Component.text;

public final class Gangs extends JavaPlugin {
    private BukkitAudiences adventure;
    public @NonNull BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
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
        String dbType = this.getConfig().getString("database.type");
        System.out.println(dbType);
        this.adventure = BukkitAudiences.create(this);
        getCommand("tester").setExecutor(new Tester(this));
        System.out.println("yessir");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
