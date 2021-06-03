package it.alexdev_.customrepair;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class CustomRepair extends JavaPlugin {

    private final HashMap<Player, ItemStack> map = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Commands commands = new Commands(this);
        Bukkit.getServer().getPluginManager().registerEvents(new Events(this), this);
        getCommand("cr").setExecutor(commands);
    }

    public HashMap<Player, ItemStack> getMap() {
        return map;
    }
}
