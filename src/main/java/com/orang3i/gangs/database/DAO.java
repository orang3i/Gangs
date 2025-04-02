package com.orang3i.gangs.database;

import java.nio.file.LinkOption;
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
        db.execute(sql);
    }

    public void getData() throws SQLException {
        String sql = "SELECT * FROM test";
        db.query(sql, resultSet -> {
            if(resultSet.next()) {
                System.out.println(resultSet.getString("Name"));
            }
        });
    }
}
