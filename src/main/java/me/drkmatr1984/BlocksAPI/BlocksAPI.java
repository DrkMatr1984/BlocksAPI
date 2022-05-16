package me.drkmatr1984.BlocksAPI;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BlocksAPI extends JavaPlugin
{
    private JavaPlugin plugin;
    private Logger log;
    private PluginManager pm; //Used to register Events
    private BlocksAPIConfig config;
    private static BlocksAPI instance;
    
    @Override
    public void onEnable()
    {
    	instance = new BlocksAPI(this);
    }
    
    public BlocksAPI(JavaPlugin plugin)
    {
    	instance = this;
	    this.plugin = plugin; //creates a plugin instance for easy access to plugin
	    log = plugin.getServer().getLogger();
	    pm = plugin.getServer().getPluginManager();
        config = new BlocksAPIConfig(plugin, this);
        this.log.info("BlocksAPI enabled!");
    }
  
    @Override
    public void onDisable()
    {
	    //save cached blocks into database
    }
  
    public JavaPlugin getPlugin(){
	    return plugin;
    }
    
    public static BlocksAPI getInstance(){
	    return instance;
    }

    public PluginManager getPm() {
	    return pm;
    }

	public BlocksAPIConfig getConfiguration() {
		return config;
	}
}