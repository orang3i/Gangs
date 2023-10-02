package com.orang3i.gangs.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedUpdate;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.orang3i.gangs.Gangs;
import com.orang3i.gangs.database.entities.PlayerStats;
import com.orang3i.gangs.database.entities.ServerStats;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GangsService {

    private final Dao<PlayerStats,String> playerStatsDao;
    private final Dao<ServerStats,String> serverStatsDao;


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
        TableUtils.createTableIfNotExists(connectionSource, ServerStats.class);
        serverStatsDao = DaoManager.createDao(connectionSource,ServerStats.class);

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

    public boolean playerExists(UUID uuid) throws SQLException{
        System.out.println("player exsits");
        return playerStatsDao.idExists(uuid.toString());

    }

    public void setPlayerGang(Player player,String gang) throws SQLException{
        PlayerStats playerStats = playerStatsDao.queryForId(player.getUniqueId().toString());
        if(playerStats != null){
            playerStats.setGang(gang);
            playerStatsDao.update(playerStats);
        }
    }
    public void setPlayerGang(UUID uuid,String gang) throws SQLException{
        PlayerStats playerStats = playerStatsDao.queryForId(uuid.toString());
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
    public void setPlayerRank(UUID uuid,String rank) throws SQLException{
        PlayerStats playerStats = playerStatsDao.queryForId(uuid.toString());
        if(playerStats != null){
            playerStats.setRank(rank);
            playerStatsDao.update(playerStats);
        }
    }
    public PlayerStats getPlayerStats(Player player) throws SQLException {

        return playerStatsDao.queryForId(player.getUniqueId().toString());
    }

    public PlayerStats getPlayerStats(UUID uuid) throws SQLException {

        return playerStatsDao.queryForId(uuid.toString());
    }

    public void deletePlayer(Player player) throws SQLException {
        playerStatsDao.deleteById(player.getUniqueId().toString());
    }

    public void deletePlayer(UUID uuid) throws SQLException {
        playerStatsDao.deleteById(uuid.toString());
    }

    public UUID getPlayerUUID(String username) throws SQLException {
        QueryBuilder<PlayerStats,String> qb = playerStatsDao.queryBuilder();
        // select 2 aggregate functions as the return
        qb.where().eq("username",username);
        // the results will contain 2 string values for the min and max
        GenericRawResults<String[]> rawResults = playerStatsDao.queryRaw(qb.prepareStatementString());
        // page through the results
        List<String[]> results = rawResults.getResults();
        final UUID[] rankerUuidList = {null};
        results.forEach(s -> rankerUuidList[0] =  UUID.fromString( s[0]));
        UUID rankerUuid = rankerUuidList[0];

        return rankerUuid;
    }

    public  List<String[]> getRawResults(String column,String query) throws SQLException {
        Gangs gangs = Gangs.getPlugin();
        QueryBuilder<ServerStats, String> qb = gangs.getService().getServerStatsDao().queryBuilder();
        // select 2 aggregate functions as the return
        qb.where().eq(column,query);
        // the results will contain 2 string values for the min and max
        GenericRawResults<String[]> rawResults = gangs.getService().getServerStatsDao().queryRaw(qb.prepareStatementString());
        // page through the results

        List<String[]> results = rawResults.getResults();

        return results;
    }

    public  List<String[]> getRawPlayerResults(String column,String query) throws SQLException {
        Gangs gangs = Gangs.getPlugin();
        QueryBuilder<PlayerStats, String> qb = gangs.getService().getPlayerStatsDao().queryBuilder();
        // select 2 aggregate functions as the return
        qb.where().eq(column,query);
        // the results will contain 2 string values for the min and max
        GenericRawResults<String[]> rawResults = gangs.getService().getPlayerStatsDao().queryRaw(qb.prepareStatementString());
        // page through the results

        List<String[]> results = rawResults.getResults();

        return results;
    }

    public final Dao<PlayerStats,String> getPlayerStatsDao(){
        return playerStatsDao;
    }
    public final Dao<ServerStats,String> getServerStatsDao(){
        return serverStatsDao;
    }
    public void addGangs(String gang) throws SQLException {
        System.out.println("added");
        ServerStats serverStats = new ServerStats();
        serverStats.setGangs(gang);
        serverStatsDao.create(serverStats);
    }

    public void setFriendlyFire(String gang,String friendlyFire) throws SQLException {
        ServerStats serverStats = serverStatsDao.queryForId(gang);

        serverStats.setFriendlyFire(friendlyFire);
        serverStatsDao.update(serverStats);

    }

    public void deleteGangs(String gang) throws SQLException {
        //delete a gang
        System.out.println("deleted");
        serverStatsDao.deleteById(gang);
    }

    public ServerStats getServerStats(String gangs) throws SQLException {

        return serverStatsDao.queryForId(gangs);
    }
    public void setAllies(String gang,String ally) throws SQLException {
        ServerStats serverStats = serverStatsDao.queryForId(gang);


        String alliesString = getServerStats(gang).getAllies().substring(1, getServerStats(gang).getAllies().length() - 1);
        //split the string into an array
        String[] strArray = alliesString.split(", ");
        ArrayList<String> currentAllies = new  ArrayList<String>(Arrays.asList(strArray));
        currentAllies.add(currentAllies.size(),ally);
        alliesString = currentAllies.toString();
        serverStats.setAllies(alliesString);
        serverStatsDao.update(serverStats);

    }
    public ArrayList<String> getAllies(String gang) throws SQLException {
        String alliesString = getServerStats(gang).getAllies().substring(1, getServerStats(gang).getAllies().length() - 1);
        //split the string into an array
        String[] strArray = alliesString.split(", ");
        ArrayList<String> currentAllies = new  ArrayList<String>(Arrays.asList(strArray));

        return currentAllies;
    }
    public void setPlayerGangChat(Player player,String val) throws SQLException{
        PlayerStats playerStats = playerStatsDao.queryForId(player.getUniqueId().toString());
        if(playerStats != null){
            playerStats.setGangchat(val);
            playerStatsDao.update(playerStats);
        }
    }

}
