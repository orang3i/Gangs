package com.orang3i.gangs.database;

import java.sql.SQLException;

public class DAO {

    private final DB db;

    public DAO() {
        this.db = new DB(Connector.getConnection());
    }

    public void createTestTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS test (User CHAR)";
        db.execute(sql);
    }

    public void insertData() throws SQLException {
        String sql = "INSERT INTO test VALUES (?)";
        db.execute(sql , stmt -> {
            stmt.setString(1,"orang3i");
        });
    }

    public void getData() throws SQLException {
        String sql = "SELECT * FROM test";
        db.query(sql, resultSet -> {
            if(resultSet.next()) {
                System.out.println(resultSet.getString("User"));
            }
        });
    }
}
