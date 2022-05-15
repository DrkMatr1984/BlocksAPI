package me.drkmatr1984.BlocksAPI;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.drkmatr1984.BlocksAPI.storage.BlockStorage;
import me.drkmatr1984.BlocksAPI.storage.SQLBlockStorage;
import me.drkmatr1984.BlocksAPI.storage.YMLBlockStorage;

public class BlocksAPIConfig {
	
	private BlocksAPI plugin;
	private List<String> banList;
	private List<String> worldBanList;
	private String type;
	private BlockStorage storage;
	private boolean recordBlockBreak;
	private boolean recordBlockPlaced; // create config that asks if they would also like to record blockPlaces
	private boolean recordEntityExplode;
	private boolean recordBlockExplode;
	private boolean recordBlockBurn;
	private boolean recordBlockIgnite;
	private boolean recordBlockFromTo;
	private boolean recordPlayerBucketEmpty;
	private boolean useListeners;
	private boolean debugMessages;
	
	public BlocksAPIConfig(BlocksAPI plugin) {
		this.plugin = plugin;
		initializeConfig();
	}
	
	public void initializeConfig() {
		banList = new ArrayList<String>();
		worldBanList = new ArrayList<String>();
		File file = new File(plugin.getDataFolder(), "settings.yml");	
		if(!file.exists()) {
			plugin.saveResource("settings.yml", false);
	    }
		FileConfiguration f = YamlConfiguration.loadConfiguration(file);
		this.type = f.getString("storage.type");
		if(type.equalsIgnoreCase("database") || type.equalsIgnoreCase("sql"))
	    {
	        if(this.storage == null) {
	      	    try {
	                this.storage = new SQLBlockStorage(this.plugin);
	            }
	            catch( SQLException ex){
	                this.plugin.getLogger().log(Level.SEVERE,"Could not create SQLStorage, falling back to file storage");
	                this.plugin.getLogger().log(Level.SEVERE,"Reason: " + ex.getMessage());
	                // Fall Back to YML
	                this.storage = new YMLBlockStorage(this.plugin);
	            }
	      	}          
	    }
	    else
	    {
	      	if(this.storage == null)
	      		this.storage = new YMLBlockStorage(this.plugin);
	    }
	    this.useListeners = f.getBoolean("useListeners");
	    if(this.useListeners){
	    	this.recordBlockBreak = f.getBoolean("listeners.BlockBreak");
	    	this.recordBlockPlaced = f.getBoolean("listeners.BlockPlace");
	    	this.recordEntityExplode = f.getBoolean("listeners.EntityExplode");
	    	this.recordBlockExplode = f.getBoolean("listeners.BlockExplode");
	    	this.recordBlockBurn = f.getBoolean("listeners.BlockBurn");
	    	this.recordBlockIgnite = f.getBoolean("listeners.BlockIgnite");
	    	this.recordBlockFromTo = f.getBoolean("listeners.BlockFromTo");
	    	this.recordPlayerBucketEmpty = f.getBoolean("listeners.PlayerBucketEmpty");;
	    }else{
	    	this.recordBlockBreak = false;
	    	this.recordBlockPlaced = false;
	    	this.recordEntityExplode = false;
	    	this.recordBlockExplode = false;
	    	this.recordBlockBurn = false;
	    	this.recordBlockIgnite = false;
	    	this.recordBlockFromTo = false;
	    	this.recordPlayerBucketEmpty = false;
	    }	    
	    this.debugMessages = f.getBoolean("debugMessages");
	    this.worldBanList = f.getStringList("blacklistWorlds");
	    this.banList = f.getStringList("blockBlacklist");
	}

	public List<String> getBanList() {
		return banList;
	}

	public List<String> getWorldBanList() {
		return worldBanList;
	}

	public boolean isRecordBlockBreak() {
		return recordBlockBreak;
	}

	public boolean isRecordEntityExplode() {
		return recordEntityExplode;
	}

	public boolean isRecordBlockPlaced() {
		return recordBlockPlaced;
	}

	public boolean isRecordBlockExplode() {
		return recordBlockExplode;
	}

	public boolean isRecordBlockBurn() {
		return recordBlockBurn;
	}

	public boolean isRecordBlockIgnite() {
		return recordBlockIgnite;
	}

	public boolean isRecordBlockFromTo() {
		return recordBlockFromTo;
	}

	public boolean isRecordPlayerBucketEmpty() {
		return recordPlayerBucketEmpty;
	}

	public boolean isDebugMessages() {
		return debugMessages;
	}
		
}
