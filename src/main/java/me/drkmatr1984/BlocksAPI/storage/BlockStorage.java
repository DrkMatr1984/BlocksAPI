package me.drkmatr1984.BlocksAPI.storage;

import java.util.Set;

import me.drkmatr1984.BlocksAPI.BlocksAPI;
import me.drkmatr1984.BlocksAPI.objects.SBlock;

/**
 * Abstract class specifying methods to be implemented by any class
 * responsible for storing and loading blocks
 */
public abstract class BlockStorage {

    BlocksAPI plugin;

    public BlockStorage(BlocksAPI plugin)
    {
        this.plugin = plugin;
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
	public abstract void saveBlocks(String identifier, Set<SBlock> sBlocks);

}
