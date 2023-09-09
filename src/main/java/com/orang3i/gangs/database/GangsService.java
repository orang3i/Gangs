package com.orang3i.gangs.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.orang3i.gangs.database.entities.PlayerStats;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class GangsService {

    private final Dao<PlayerStats,String> playerStatsDao;


    public GangsService(String path) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:"+path);
        TableUtils.createTableIfNotExists(connectionSource,PlayerStats.class);
        playerStatsDao = DaoManager.createDao(connectionSource,PlayerStats.class);
    }


    public  PlayerStats addPlayer(Player player) throws SQLException {
        PlayerStats playerStats = new PlayerStats();
        playerStats.setUuid(player.getUniqueId().toString());
        playerStats.setUsername(player.getDisplayName());
        playerStatsDao.create(playerStats);

        return playerStats;
    }

    public boolean playerExists(Player player) throws SQLException{
        return playerStatsDao.idExists(player.getUniqueId().toString());
    }

    public void setPlayerGang(Player player,String gang) throws SQLException{
        PlayerStats playerStats = playerStatsDao.queryForId(player.getUniqueId().toString());
        if(playerStats != null){
            playerStats.setGang(gang);
            playerStatsDao.update(playerStats);
        }
    }

    public PlayerStats getPlayerStats(Player player) throws SQLException {

        return playerStatsDao.queryForId(player.getUniqueId().toString());
    }

    public void deletePlayer(Player player) throws SQLException {
        playerStatsDao.deleteById(player.getUniqueId().toString());
    }


}
