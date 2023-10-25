package com.orang3i.gangs.database.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.List;

@DatabaseTable(tableName = "server_stats")

public class ServerStats {
    @DatabaseField(id = true)
    private String gangs;

    @DatabaseField(canBeNull = false,defaultValue = "false")
    private String friendlyFire;

    @DatabaseField(canBeNull = false,defaultValue = "[]")
    private String friendlyFireAllies;

    @DatabaseField(canBeNull = false,defaultValue = "[]")
    private String allies;

    @DatabaseField(canBeNull = false,defaultValue = "[]")
    private String baseCoords;

    @DatabaseField(canBeNull = false,defaultValue = "[]")
    private String baseName;

    @DatabaseField(canBeNull = false,defaultValue = "0")
    private String balance;
    public String getGangs() {
        return gangs;
    }

    public void setGangs(String gangs) {
        this.gangs = gangs;
    }

    public String getBaseCoords(){
        return  baseCoords;
    }

    public void setBaseCoords(String baseCoords){
        this.baseCoords = baseCoords;
    }

    public String getBaseName(){
        return  baseName;
    }

    public void setBaseName(String baseName){
        this.baseName = baseName;
    }

    public String getFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(String friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public String getFriendlyFireAllies() {
        return friendlyFireAllies;
    }

    public void setFriendlyFireAllies(String friendlyFireAllies) {
        this.friendlyFireAllies = friendlyFireAllies;
    }

    public String getAllies(){
        return allies;
    }
    public void setAllies(String allies){
        this.allies = allies;
    }

    public String getBalance(){
        return balance;
    }

    public void setBalance(String balance){
        this.balance = balance;
    }


}
