package com.orang3i.gangs;

import org.bukkit.plugin.java.JavaPlugin;

public final class Gangs extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info(String.format("Gangs Plugin Version %s Enabled", getPluginMeta().getVersion()));
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("Gangs Plugin Version %s Disabled", getPluginMeta().getVersion()));
    }
}
