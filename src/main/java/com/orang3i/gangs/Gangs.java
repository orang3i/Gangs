package com.orang3i.gangs;

import com.orang3i.gangs.test.AdventureTests;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Gangs extends JavaPlugin {

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new AdventureTests(this), this);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info(String.format("Gangs Plugin Version %s Enabled", getPluginMeta().getVersion()));
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("Gangs Plugin Version %s Disabled", getPluginMeta().getVersion()));
    }

    public Audience getAudiences() {
        return (Audience) Bukkit.getServer();
    }
}
