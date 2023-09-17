package com.orang3i.gangs.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "server_stats")

public class ServerStats {
    @DatabaseField(id = true)
    private String gangs;

    @DatabaseField(canBeNull = false,defaultValue = "false")
    private String friendlyFire;
    public String getGangs() {
        return gangs;
    }

    public void setGangs(String gangs) {
        this.gangs = gangs;
    }

    public String getFriendlyFire() {
        return friendlyFire;
    }

    public void setFriendlyFire(String friendlyFire) {
        this.friendlyFire = friendlyFire;
    }



}
