package me.goldmember33.playervaultsxpapiexpansion;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerVaultsXPAPIMain extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {

            getLogger().info("PlaceholderAPI found!");

            if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                new PlayerVaultsXExpansion().register();
            }

        } else {

            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
