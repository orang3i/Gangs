package com.orang3i.gangs.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.database.entities.PlayerStats;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class GangsService {

    private final Dao<PlayerStats,String> playerStatsDao;


    public GangsService() throws SQLException {

        Gangs gangs = Gangs.getPlugin();
        String dbType = gangs.getConfig().getString("database.type");
        ConnectionSource connectionSource = null;
        String host = gangs.getConfig().getString("database.host");
        String username = gangs.getConfig().getString("database.user");
        String password = gangs.getConfig().getString("database.password");
        String port = gangs.getConfig().getString("database.port");
        String dbname = gangs.getConfig().getString("database.db-name");
        if(dbType.equals("sqlite")){
          connectionSource   = new JdbcConnectionSource("jdbc:sqlite:"+gangs.getDataFolder()+"/"+"\\gangs.db");
        }
        if(dbType.equals("mysql")){
            //mysql://<username>:<password>@<host>:<port>/<db_name>
            try {
                Class.forName("com.mysql.jdbc.Driver");
            }
            catch (ClassNotFoundException e) {

                e.printStackTrace();
            }
            String url = "jdbc:mysql://"+username+":"+password+"@"+host+":"+port+"/"+dbname;
            System.out.println(url);

            connectionSource   = new JdbcConnectionSource(url);


        }

        TableUtils.createTableIfNotExists(connectionSource,PlayerStats.class);
        playerStatsDao = DaoManager.createDao(connectionSource,PlayerStats.class);
    }


    public  PlayerStats addPlayer(Player player) throws SQLException {
        PlayerStats playerStats = new PlayerStats();
        playerStats.setUuid(player.getUniqueId().toString());
        playerStats.setUsername(player.getDisplayName());
        playerStatsDao.create(playerStats);
        System.out.println("player added");
        return playerStats;
    }

    public boolean playerExists(Player player) throws SQLException{
        System.out.println("player exsits");
        return playerStatsDao.idExists(player.getUniqueId().toString());

    }

    public void setPlayerGang(Player player,String gang) throws SQLException{
        PlayerStats playerStats = playerStatsDao.queryForId(player.getUniqueId().toString());
        if(playerStats != null){
            playerStats.setGang(gang);
            playerStatsDao.update(playerStats);
        }
    }

    public void setPlayerRank(Player player,String rank) throws SQLException{
        PlayerStats playerStats = playerStatsDao.queryForId(player.getUniqueId().toString());
        if(playerStats != null){
            playerStats.setRank(rank);
            playerStatsDao.update(playerStats);
        }
    }

    public PlayerStats getPlayerStats(Player player) throws SQLException {

        return playerStatsDao.queryForId(player.getUniqueId().toString());
    }

    public void deletePlayer(Player player) throws SQLException {
        playerStatsDao.deleteById(player.getUniqueId().toString());
    }

    public final Dao<PlayerStats,String> getDao(){
        return playerStatsDao;
    }


}
