package com.orang3i.gangs.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DB {
    private final Connection conn;

    public DB(Connection conn) {
        this.conn = conn;
    }

    @FunctionalInterface
    public interface StatementCallback {
        void accept(PreparedStatement stmt) throws SQLException;
    }

    @FunctionalInterface
    public interface QueryCallback {
        void accept(ResultSet rs) throws SQLException;
    }

    public void execute(String sql, StatementCallback paramSetter) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            paramSetter.accept(stmt);
            stmt.executeUpdate();
        }
    }

    public void execute(String sql) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    public void query(String sql, StatementCallback paramSetter, QueryCallback resultHandler) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            paramSetter.accept(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                resultHandler.accept(rs);
            }
        }
    }

    public void query(String sql, QueryCallback resultHandler) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            resultHandler.accept(rs);
        }
    }

}
