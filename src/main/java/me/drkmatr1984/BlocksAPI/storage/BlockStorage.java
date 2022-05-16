package me.drkmatr1984.BlocksAPI.storage;

import java.util.Set;

import org.bukkit.plugin.java.JavaPlugin;

import me.drkmatr1984.BlocksAPI.BlocksAPI;
import me.drkmatr1984.BlocksAPI.objects.SBlock;

/**
 * Abstract class specifying methods to be implemented by any class
 * responsible for storing and loading blocks
 */
public abstract class BlockStorage {

    public BlocksAPI instance;
    public JavaPlugin plugin;

    public BlockStorage(JavaPlugin plugin, BlocksAPI instance)
    {
        this.plugin = plugin;
        this.instance = instance;
    }

    /**
     * Loads blocks from selected data source
     * identifier is String identifer you have
     * set when you save
     * @throws Exception 
     */
	public abstract Set<SBlock> loadBlocks(String identifier) throws Exception;
	
	/**
     * Saves blocks to selected data source
     * identifier is String identifer you will
     * need to retrieve the set of Sblocks
	 * @throws Exception 
     */
	public abstract void saveBlocks(String identifier, Set<SBlock> sBlocks) throws Exception;

}
