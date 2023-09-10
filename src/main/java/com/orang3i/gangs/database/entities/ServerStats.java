package com.orang3i.gangs.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "server_stats")

public class ServerStats {
    @DatabaseField(id = true)
    private String gangs;

    public String getGangs() {
        return gangs;
    }

    public void setGangs(String gangs) {
        this.gangs = gangs;
    }

}
