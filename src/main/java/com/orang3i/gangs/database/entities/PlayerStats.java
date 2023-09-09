package com.orang3i.gangs.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "player_stats")
public class PlayerStats {

    @DatabaseField(id = true)
    private String uuid;

    @DatabaseField(canBeNull = false)
    private String username;

    @DatabaseField(canBeNull = false,defaultValue = "none")
    private String gang;

    @DatabaseField(canBeNull = false,defaultValue = "rookie")
    private String rank;

    public PlayerStats(){
        //req
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGang() {
        return gang;
    }

    public void setGang(String gang) {
        this.gang = gang;
    }
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
