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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

public void setBalance(String gang,String balance)throws SQLException{
        ServerStats serverStats = serverStatsDao.queryForId(gang);
        serverStats.setBalance(balance);
        serverStatsDao.update(serverStats);
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
    public void setFriendlyFireAllies(String gang,String ally) throws SQLException {
        ServerStats serverStats = serverStatsDao.queryForId(gang);


        String alliesString = getServerStats(gang).getFriendlyFireAllies().substring(1, getServerStats(gang).getFriendlyFireAllies().length() - 1);
        //split the string into an array
        //List<String> currentAllies = new ArrayList<String>( Arrays.asList(alliesString.split("\\s*,\\s*")));
        ArrayList<String> tmp= new ArrayList<String>( Arrays.asList(alliesString.split(",")));

        alliesString="[";
        int i;
        for (i=0;i<tmp.size();i++){
            String test = tmp.get(i).trim();
            alliesString = alliesString+test+",";
        }
        alliesString = alliesString+ally+"]";
        System.out.println(alliesString);
        serverStats.setFriendlyFireAllies(alliesString);
        serverStatsDao.update(serverStats);

    }
    public ArrayList<String> getAlliesFriendlyFire(String gang) throws SQLException {
        String alliesString = getServerStats(gang).getFriendlyFireAllies().substring(1, getServerStats(gang).getFriendlyFireAllies().length() - 1);
        //split the string into an array
        ArrayList<String> tmp= new ArrayList<String>( Arrays.asList(alliesString.split(",")));
        String[] str = new String[tmp.size()];
        int i;
        for (i=0;i<tmp.size();i++){
            String test = tmp.get(i).trim();
            str[i] = test;
        }
        ArrayList<String> currentAllies = new ArrayList<>(Arrays.asList(str));
        return currentAllies;
    }

    public void removeAlliesFriendlyFire(String gang,String ally) throws SQLException {
        ArrayList<String> listA = getAllies(gang);
        listA.remove(ally);
        ArrayList<String> listB = getAllies(ally);
        listB.remove(gang);
        String stringA = "[";
        String stringB = "[";
        int i;
        for (i=0;i<listA.size()-1;i++){
            stringA = stringA+listA.get(i).trim()+",";
        }
        stringA = stringA+listA.get(listA.size()-1)+"]";
        for (i=0;i<listB.size()-1;i++){
            stringB = stringB+listB.get(i).trim()+",";
        }
        stringB = stringB+listB.get(listB.size()-1)+"]";

        ServerStats serverStatsA = serverStatsDao.queryForId(gang);
        ServerStats serverStatsB = serverStatsDao.queryForId(ally);
        serverStatsA.setFriendlyFireAllies(stringA);
        serverStatsDao.update(serverStatsA);
        serverStatsB.setFriendlyFireAllies(stringB);
        serverStatsDao.update(serverStatsB);
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
        //List<String> currentAllies = new ArrayList<String>( Arrays.asList(alliesString.split("\\s*,\\s*")));
        ArrayList<String> tmp= new ArrayList<String>( Arrays.asList(alliesString.split(",")));
        alliesString="[";
        int i;
        for (i=0;i<tmp.size();i++){
            String test = tmp.get(i).trim();
            alliesString = alliesString+test+",";
        }
        alliesString = alliesString+ally+"]";
        serverStats.setAllies(alliesString);
        serverStatsDao.update(serverStats);

    }
    public void tempSolGangCreateAlly(String gang) throws SQLException {
        ServerStats serverStats = serverStatsDao.queryForId(gang);
        String allies = "["+gang+",none]";
        serverStats.setAllies(allies);
        serverStatsDao.update(serverStats);
    }

    public void tempSolGangCreateAllyFriendlyFire(String gang) throws SQLException {
        ServerStats serverStats = serverStatsDao.queryForId(gang);
        String allies = "[none,none]";
        serverStats.setFriendlyFireAllies(allies);
        serverStatsDao.update(serverStats);
    }

    public void tempSolGangCreateBase(String gang) throws SQLException {
        ServerStats serverStats = serverStatsDao.queryForId(gang);
        String baseName = "[none,none]";
        String baseCoords = "[none,none]";
        serverStats.setBaseCoords(baseCoords);
        serverStats.setBaseName(baseName);
        serverStatsDao.update(serverStats);
    }
    public ArrayList<String> getAllies(String gang) throws SQLException {
        String alliesString = getServerStats(gang).getAllies().substring(1, getServerStats(gang).getAllies().length() - 1);
        //split the string into an array
        ArrayList<String> tmp= new ArrayList<String>( Arrays.asList(alliesString.split(",")));
        String[] str = new String[tmp.size()];
        int i;
        for (i=0;i<tmp.size();i++){
            String test = tmp.get(i).trim();
            str[i] = test;
        }
        ArrayList<String> currentAllies = new ArrayList<>(Arrays.asList(str));
        return currentAllies;
    }
    public ArrayList<String> getBases(String gang) throws SQLException {
        String baseString = getServerStats(gang).getBaseName().substring(1, getServerStats(gang).getBaseName().length() - 1);
        //split the string into an array
        ArrayList<String> tmp= new ArrayList<String>( Arrays.asList(baseString.split(",")));
        String[] str = new String[tmp.size()];
        int i;
        for (i=0;i<tmp.size();i++){
            String test = tmp.get(i).trim();
            str[i] = test;
        }
        ArrayList<String> currentBase = new ArrayList<>(Arrays.asList(str));
        return currentBase;
    }

    public ArrayList<String> getBasesCoordsList(String gang) throws SQLException {
        String baseString = getServerStats(gang).getBaseCoords().substring(1, getServerStats(gang).getBaseCoords().length() - 1);
        //split the string into an array
        ArrayList<String> tmp= new ArrayList<String>( Arrays.asList(baseString.split(",")));
        String[] str = new String[tmp.size()];
        int i;
        for (i=0;i<tmp.size();i++){
            String test = tmp.get(i).trim();
            str[i] = test;
        }
        ArrayList<String> currentBase = new ArrayList<>(Arrays.asList(str));
        return currentBase;
    }

    public String getBaseCoords(String gang,String base) throws SQLException {

        int coordsIndex = getBases(gang).indexOf(base);
        System.out.println(coordsIndex);
        String coords = getBasesCoordsList(gang).get(coordsIndex);
        System.out.println(coords);
        return coords;
    }
    public void setBases(String gang,String base,String x , String y , String z) throws SQLException {
        ServerStats serverStats = serverStatsDao.queryForId(gang);

        String baseString = getServerStats(gang).getBaseName().substring(1, getServerStats(gang).getBaseName().length() - 1);

        //split the string into an array
        //List<String> currentAllies = new ArrayList<String>( Arrays.asList(alliesString.split("\\s*,\\s*")));
        ArrayList<String> tmp= new ArrayList<String>( Arrays.asList(baseString.split(",")));
        baseString="[";
        int i;
        for (i=0;i<tmp.size();i++){
            String test = tmp.get(i).trim();
            baseString = baseString+test+",";
        }
        baseString = baseString+base+"]";
        System.out.println(baseString);
        String coords = x+" "+y+" "+z;
        System.out.println(coords);
        String baseCoordsString = getServerStats(gang).getBaseCoords().substring(1, getServerStats(gang).getBaseCoords().length() - 1);
        ArrayList<String> tmm= new ArrayList<String>( Arrays.asList(baseCoordsString.split(",")));
        baseCoordsString="[";
        int j;
        for (j=0;j<tmm.size();j++){
            String test = tmm.get(j).trim();
            System.out.println(j);
            baseCoordsString = baseCoordsString+test+",";
        }
        baseCoordsString = baseCoordsString+coords+"]";

        serverStats.setBaseName(baseString);
        serverStats.setBaseCoords(baseCoordsString);
        serverStatsDao.update(serverStats);

    }

    public void removeBases(String gang,String base) throws SQLException {
        ArrayList<String> BasesCoords = getBasesCoordsList(gang);
        BasesCoords.remove(getBaseCoords(gang,base));
        String  BaseCoordsStr = "[";

        int j;
        for (j=0;j<BasesCoords.size()-1;j++){
            BaseCoordsStr = BaseCoordsStr+BasesCoords.get(j).trim()+",";
        }
        BaseCoordsStr = BaseCoordsStr+BasesCoords.get(BasesCoords.size()-1)+"]";



        ArrayList<String> Bases = getBases(gang);
        Bases.remove(base);
        String  BaseStr = "[";

        int i;
        for (i=0;i<Bases.size()-1;i++){
            BaseStr = BaseStr+Bases.get(i).trim()+",";
        }
        BaseStr = BaseStr+Bases.get(Bases.size()-1)+"]";

        ServerStats serverStats = serverStatsDao.queryForId(gang);
        serverStats.setBaseName(BaseStr);
        serverStats.setBaseCoords(BaseCoordsStr);
        serverStatsDao.update(serverStats);
    }
    public void removeAllies(String gang,String ally) throws SQLException {
        ArrayList<String> listA = getAllies(gang);
        listA.remove(ally);
        ArrayList<String> listB = getAllies(ally);
        listB.remove(gang);
        String stringA = "[";
        String stringB = "[";
        int i;
        for (i=0;i<listA.size()-1;i++){
        stringA = stringA+listA.get(i).trim()+",";
        }
        stringA = stringA+listA.get(listA.size()-1)+"]";
        for (i=0;i<listB.size()-1;i++){
            stringB = stringB+listB.get(i).trim()+",";
        }
        stringB = stringB+listB.get(listB.size()-1)+"]";

        ServerStats serverStatsA = serverStatsDao.queryForId(gang);
        ServerStats serverStatsB = serverStatsDao.queryForId(ally);
        serverStatsA.setAllies(stringA);
        serverStatsDao.update(serverStatsA);
        serverStatsB.setAllies(stringB);
        serverStatsDao.update(serverStatsB);
    }
    public void setPlayerGangChat(Player player,String val) throws SQLException{
        PlayerStats playerStats = playerStatsDao.queryForId(player.getUniqueId().toString());
        if(playerStats != null){
            playerStats.setGangchat(val);
            playerStatsDao.update(playerStats);
        }
    }
    public void setPlayerAllyChat(Player player,String val) throws SQLException{
        PlayerStats playerStats = playerStatsDao.queryForId(player.getUniqueId().toString());
        if(playerStats != null){
            playerStats.setAllychat(val);
            playerStatsDao.update(playerStats);
        }
    }
}
