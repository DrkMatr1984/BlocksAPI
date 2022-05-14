package me.drkmatr1984.BlocksAPI.storage;

import me.drkmatr1984.BlocksAPI.BlocksAPI;
import me.drkmatr1984.BlocksAPI.objects.SBlock;
import me.drkmatr1984.BlocksAPI.utils.BlockSerialization;

import org.bukkit.Bukkit;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Set;
import javax.sql.DataSource;

public class SQLBlockStorage extends BlockStorage {

	protected DataSource dataSource;
    protected DatabaseType driver;
    private HikariConfig config;
    protected String url = "";
    protected String username = "";
    protected String password = "";
    private BlocksAPI plugin;

    private Connection conn = null;

    public SQLBlockStorage(BlocksAPI plugin) throws SQLException
    {
        super(plugin);
        this.plugin = plugin;
        this.driver = DatabaseType.match(plugin.getConfig().getString(("storage.database.driver")));
        this.username = plugin.getConfig().getString("storage.database.username");
        this.password = plugin.getConfig().getString("storage.database.password");
        if(this.driver!=null) {
        	if(this.driver.equals(DatabaseType.SQLITE)){
            	this.url = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + System.getProperty("file.separator") + plugin.getConfig().getString("storage.database.url");        	
                try
            	{
                    if (!loadDriver()) {
                        throw new SQLException("Couldn't load driver");
                    }
                    this.conn = getConnection();           
                
                	if (conn==null) {
                        throw new SQLException("Couldn't connect to the database");
                    }

                    // Create table
                	String qry = "CREATE TABLE IF NOT EXISTS `blocks` (`identifier` VARCHAR(64) NOT NULL PRIMARY KEY, `serializedSet` VARCHAR(64), `size` VARCHAR(32);";
                    Statement stmt = this.conn.createStatement();
                    stmt.execute(qry);
            	}catch (Exception e) {
            		try
                    {
                            conn.rollback();
                    }
                    catch (SQLException e1)
                    {
                            e1.printStackTrace();
                    }
                    e.printStackTrace();
                    return;
            	}
            }else{
            	if(plugin.getConfig().getString("storage.database.autoReconnect").equalsIgnoreCase("true"))
            		this.url = "jdbc:" + plugin.getConfig().getString("storage.database.url") + plugin.getConfig().getString("storage.database.database") + "?useSSL=" + plugin.getConfig().getString("storage.database.useSSL") + "&autoReconnect=true";
            	else
            		this.url = "jdbc:" + plugin.getConfig().getString("storage.database.url") + plugin.getConfig().getString("storage.database.database") + "?useSSL=" + plugin.getConfig().getString("storage.database.useSSL");
            	config = new HikariConfig();
            	config.setJdbcUrl(this.url);
                config.setUsername(this.username);
                config.setPassword(this.password);
                config.setMaximumPoolSize(10);
                config.setMaxLifetime(360000);
                config.setValidationTimeout(60000);
                config.setDriverClassName(this.driver.driver);
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                dataSource = new HikariDataSource(config);
                try
            	{
            		this.conn = dataSource.getConnection();
                    if (conn==null) {
                        throw new SQLException("Couldn't connect to the database");
                    }
                    String qry = "CREATE TABLE IF NOT EXISTS `blocks` (`identifier` VARCHAR(64) NOT NULL PRIMARY KEY, `serializedSet` VARCHAR(64), `size` VARCHAR(32);";
                    Statement stmt = this.conn.createStatement();
                    stmt.execute(qry);
            	}catch (Exception e) {
            		try
                    {
                            conn.rollback();
                    }
                    catch (SQLException e1)
                    {
                            e1.printStackTrace();
                    }
                    e.printStackTrace();
                    return;
            	}
            }
        	//Start "keepAlive" task to keep connection active
        	Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> keepAlive(), 20*60*60*7, 20*60*60*7);
        }else {
        	plugin.getLogger().info("Database needs a type set. Possible values: H2, MYSQL, POSTGRE, SQLITE");
        }      
    }
    
    public void closeConnection() throws SQLException{
    	if(dataSource instanceof HikariDataSource) {
    		if(!((HikariDataSource) dataSource).isClosed()) {
    			((HikariDataSource) dataSource).close();
    		}
    	}else {
    		if (conn != null && !conn.isClosed()) conn.close();
    	}
    	 
    }
    
    @SuppressWarnings("deprecation")
	private boolean loadDriver()
    {
        try {
            this.getClass().getClassLoader().loadClass(this.driver.driver).newInstance();
            return true;
        } catch (IllegalAccessException e) {
            // Constructor is private, OK for DriverManager contract
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Connection getConnection() throws SQLException{
    	if (conn != null)
    	      try {
    	        conn.createStatement().execute("SELECT 1;");
    	      } catch (SQLException sqlException) {
    	        if (sqlException.getSQLState().equals("08S01"))
    	          try {
    	            conn.close();
    	          } catch (SQLException sQLException) {} 
    	      }
        if (conn == null || conn.isClosed() || !conn.isValid(60)) { //maybe change to lower than 60, like 4 seconds?
        	if(dataSource  instanceof HikariDataSource) {
                if((username.isEmpty() && password.isEmpty()))
                	conn = dataSource.getConnection();
                else
                	conn = dataSource.getConnection(username, password);
        	}else {
        		conn = (username.isEmpty() && password.isEmpty()) ? DriverManager.getConnection(url) : DriverManager.getConnection(url, username, password);
        	}           
        }
        // The connection could be null here (!)
        return conn;
    }
    
    private void keepAlive() {
        try {
            conn.isValid(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }              
    }

    private HashMap<String, Integer> getBlocks(String identifier)
    {
    	String baseString = null;
    	Integer size = null;
        String qry = "SELECT * FROM `blocks` WHERE `identifier` = ?;";
        try {
            PreparedStatement stmt = getConnection().prepareStatement(qry);
            stmt.setString(1, identifier);
            ResultSet result = stmt.executeQuery();

            if(result.next())
            {
                baseString = result.getString("serializedSet");
                size = Integer.parseInt(result.getString("size"));
            }
            stmt.close();
        }
        catch (SQLException ex)
        {
        	System.out.println("[BlocksAPI] ERROR");
        	System.out.println("Could not load blocks with identifier" + identifier);
        	System.out.println("Reason: " + ex.getMessage());
        }
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(baseString, size);
        return map;   
    }

    @Override
    public void saveBlocks(String identifier, Set<SBlock> sBlocks)
    {
    	Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try {
				saveAsync(identifier, BlockSerialization.toBase64(sBlocks), sBlocks.size());
			} catch (SQLException ex) {
				System.out.println("[BlocksAPI] ERROR");
		       	System.out.println("Could not save blocks with identifier" + identifier);
		       	System.out.println("Reason: " + ex.getMessage());		       	
			}
		});
    	   
    }
    
    @SuppressWarnings("resource")
	private void saveAsync(String identifier, String serializedSet, Integer size) throws SQLException {
    	// Check if the identifier has an Existing Row
       String existing;
       String qry = "SELECT `identifier` FROM `blocks` WHERE `identifier` = ?;";
       PreparedStatement stmt = getConnection().prepareStatement(qry);
       stmt.setString(1, identifier);
       
       ResultSet result = stmt.executeQuery();
       existing = result.next() ? result.getString("identifier") : null;
       stmt.close();
            
       if(existing != null){
           stmt = getConnection().prepareStatement("UPDATE `blocks` SET `serializedSet` = ?, `size` = ? WHERE `identifier` = ?;");
           stmt.setString(1, serializedSet);
           stmt.setInt(2, size);
           stmt.setString(3, identifier);
       }else{
           stmt = getConnection().prepareStatement("INSERT INTO `blocks` VALUES (?, ?, ?);");
           stmt.setString(1, identifier);
           stmt.setString(2, serializedSet);
           stmt.setInt(3, size);
       }
       stmt.executeUpdate();
       stmt.close();
    }

	@Override
	public Set<SBlock> loadBlocks(String identifier) {
		HashMap<String, Integer> map = getBlocks(identifier);
		if(map !=null) {
			for(String baseString : map.keySet()) {
				Integer size = map.get(baseString);
				try {
					return BlockSerialization.fromBase64(baseString, size);
				} catch (IOException e) {
					System.out.println("[BlocksAPI] ERROR");
		        	System.out.println("Could not deserialize blocks with identifier" + identifier);
		        	System.out.println("Reason: " + e.getMessage());
				}
			}
		}
	    return null;		
	}
}
