package me.conji.cauldron.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class CauldronInjector {
    Plugin plugin;

    public CauldronInjector(String dependsOn) {
        if (dependsOn != null) {
            Plugin pluginDependency = Bukkit.getPluginManager().getPlugin(dependsOn);
            if (pluginDependency != null) {
                plugin = pluginDependency;
                Bukkit.getPluginManager().enablePlugin(pluginDependency);
            }
        }
    }


}
