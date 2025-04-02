package com.orang3i.gangs.database;

import java.sql.SQLException;

public class DAO {

    private final DB db;

    public DAO() {
        this.db = new DB(Connector.getConnection());
    }

    public void createTestTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS test (Name INT)";
        db.execute(sql);
    }

    public void insertData() throws SQLException {
        String sql = String.format("INSERT INTO test VALUES (%s)","'test'");
    }

}
