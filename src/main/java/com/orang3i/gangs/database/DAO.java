package com.orang3i.gangs.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DAO {

    private final DB db;

    public DAO() {
        this.db = new DB(Connector.getConnection());
    }

    public void createPlayersTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS PLAYERS (UUID CHAR, GANG_NAME CHAR REFERENCES GANGS(GANG_NAME), RANK CHAR)";
        db.execute(sql);
    }

    public void createGangsTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS GANGS (GANG_NAME CHAR UNIQUE)";
        db.execute(sql);
    }

    public void setPlayerGang(UUID uuid, String gangName) throws SQLException {
        String sql = "UPDATE PLAYERS SET GANG_NAME = ? WHERE UUID = ?";
        db.execute(sql, stmt -> {
            stmt.setString(1, gangName);
            stmt.setString(2, uuid.toString());
        });
    }

    public void setPlayerRank(UUID uuid, String rank) throws SQLException {
        String sql = "UPDATE PLAYERS SET RANK = ? WHERE UUID = ?";
        db.execute(sql, stmt -> {
            stmt.setString(1, rank);
            stmt.setString(2, uuid.toString());
        });
    }

    public boolean createGang(String gangName) throws SQLException {
        final boolean[] exists = {false};
        String sql = "SELECT 1 FROM GANGS WHERE gang_name = ? LIMIT 1";
        db.query(sql, statement -> {
            statement.setString(1, gangName);
        }, resultSet -> {
            if (resultSet.next()) {
                exists[0] = false;
            } else {
                exists[0] = true;
            }
        });

        if (!exists[0]) {
            return false;
        } else {
            sql = "INSERT INTO gangs VALUES (?)";
            db.execute(sql, stmt -> {
                stmt.setString(1, gangName);
            });
            return true;
        }

    }

    public String getPlayerGang(UUID uuid) throws SQLException {
        final String[] result = new String[1];
        String sql = "SELECT GANG_NAME FROM PLAYERS WHERE UUID = ?";
        db.query(sql, stmt -> {
            stmt.setString(1, uuid.toString());
        }, resultSet -> {
            if (resultSet.next()) {
                result[0] = resultSet.getString(1);
            }
        });
        return result[0];
    }

    public void insertData() throws SQLException {
        String sql = "INSERT INTO test VALUES (?)";
        db.execute(sql, stmt -> {
            stmt.setString(1, "orang3i");
        });
    }

    public void getData() throws SQLException {
        String sql = "SELECT * FROM test";
        db.query(sql, resultSet -> {
            if (resultSet.next()) {
                System.out.println(resultSet.getString("User"));
            }
        });
    }

    public void initPlayer(UUID uuid) throws SQLException {
        final boolean[] exists = {false};
        String sql = "SELECT 1 FROM PLAYERS WHERE UUID = ? LIMIT 1";
        db.query(sql, statement -> {
            statement.setString(1, uuid.toString());
        }, resultSet -> {
            if (resultSet.next()) {
                exists[0] = false;
            } else {
                exists[0] = true;
            }
        });
        if (exists[0]) {
            sql = "INSERT INTO PLAYERS VALUES (?,?,?)";
            db.execute(sql, stmt -> {
                stmt.setString(1, uuid.toString());
                stmt.setString(2, "NULLGANG");
                stmt.setString(3, "NULLRANK");
            });
        }
    }

    public void initGangs() throws SQLException {
        final boolean[] exists = {false};
        String sql = "SELECT 1 FROM GANGS WHERE gang_name = ? LIMIT 1";
        db.query(sql, statement -> {
            statement.setString(1, "NULLGANG");
        }, resultSet -> {
            if (resultSet.next()) {
                exists[0] = false;
            } else {
                exists[0] = true;
            }
        });
        if (exists[0]) {
            sql = "INSERT INTO gangs VALUES (?)";
            db.execute(sql, stmt -> {
                stmt.setString(1, "NULLGANG");
            });
        }
    }
}
