package me.drkmatr1984.BlocksAPI;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BlocksAPI extends JavaPlugin
{
    private static BlocksAPI plugin;
    private Logger log;
    private PluginManager pm; //Used to register Events
    private BlocksAPIConfig config;
  
    @Override
    public void onEnable()
    {
	    plugin = this; //creates a plugin instance for easy access to plugin
	    log = plugin.getServer().getLogger();
	    pm = plugin.getServer().getPluginManager();
        config = new BlocksAPIConfig(this);
        this.log.info("BlocksAPI enabled!");
    }
  
    public void onDisable()
    {
	  
    }
  
    public static BlocksAPI getInstance(){
	  return plugin;
    }
    
    
    //gotta create config loading class
    private void loadDatabase() {
	  
    }

    public PluginManager getPm() {
	    return pm;
    }
}