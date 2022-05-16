package me.drkmatr1984.BlocksAPI.storage;

import me.drkmatr1984.BlocksAPI.BlocksAPI;
import me.drkmatr1984.BlocksAPI.objects.SBlock;
import me.drkmatr1984.BlocksAPI.utils.BlockSerialization;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class YMLBlockStorage extends BlockStorage {

    FileConfiguration blocksConfig  = null;
    File blocksFile  = null;

    public YMLBlockStorage(JavaPlugin plugin, BlocksAPI instance)
    {
        super(plugin, instance);
        File folder = new File(plugin.getDataFolder().toString() + "/data");
        this.blocksFile = new File(folder, "blocks.yml");
        plugin.saveResource("data/blocks.yml", false);
        this.blocksConfig = YamlConfiguration.loadConfiguration(this.blocksFile);
    }

    @Override
	public Set<SBlock> loadBlocks(String identifier) throws IOException {
    	if(blocksConfig.contains("identifiers." + identifier)){
    		String set = blocksConfig.getString("identifiers." + identifier + ".serializedSet");
    		Integer size = blocksConfig.getInt("identifiers." + identifier + ".size");
    		if(set!=null && size !=null)
    			return BlockSerialization.fromBase64(set, size);
    		else {
    			System.out.println("[BlocksAPI] ERROR");
            	System.out.println("Could not load blocks with identifier" + identifier);
            	System.out.println("Reason: Can't find identifier in YML file");
    		}
    	}else{
    		System.out.println("[BlocksAPI] ERROR");
        	System.out.println("Could not load blocks with identifier" + identifier);
        	System.out.println("Reason: Can't find identifier in YML file");
    	}
    	return null;
    }

    @Override
	public void saveBlocks(String identifier, Set<SBlock> sBlocks)
    {
        String serializedSet = BlockSerialization.toBase64(sBlocks);
        Integer size = sBlocks.size();

        //set the config 
        this.blocksConfig.set("identifiers." + identifier + ".serializedSet", serializedSet==null ? null : serializedSet);
        this.blocksConfig.set("identifiers." + identifier + ".size", size==null ? null : size);

        // Save to file
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        	try {
				saveBlocksConfig(this.blocksConfig, this.blocksFile);
			} catch (IOException ex) {
				System.out.println("[BlocksAPI] ERROR");
		       	System.out.println("Could not save blocks with identifier" + identifier);
		       	System.out.println("Reason: " + ex.getMessage());		       	
			}
        });
    }


    private void saveBlocksConfig(FileConfiguration fc, File bf) throws IOException {
        if (fc == null || bf == null) {
            return;
        }
        fc.save(bf);
    }

}
