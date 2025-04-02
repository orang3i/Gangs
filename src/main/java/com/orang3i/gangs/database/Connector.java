package com.orang3i.gangs.database;

import com.orang3i.gangs.Gangs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class Connector {

    private static Gangs plugin;
    private static Connection connection;

    private Connector() {}

    public static void init(Gangs pluginInstance) {
        plugin = pluginInstance;
        if (Objects.equals(plugin.getConfig().getString("database.type"), "mysql")) {
            mysql();
        } else {
            sqlite();
        }
    }

    private static void mysql() {
        if (connection == null) {
            try {
                String host = plugin.getConfig().getString("database.host");
                String port = plugin.getConfig().getString("database.port");
                String database = plugin.getConfig().getString("database.db-name");
                String user = plugin.getConfig().getString("database.user");
                String password = plugin.getConfig().getString("database.password");
                String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
                connection = DriverManager.getConnection(url, user, password);
                plugin.getLogger().info("Connected to database");
            } catch (SQLException e) {
                plugin.getLogger().warning("Failed to connect to database");
            }
        }
    }

    private static void sqlite() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/" + "gangs.db");
                plugin.getLogger().info("Connected to database");
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to connect: " + e.getMessage());
            }
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
                Gangs.getPluginStatic().getLogger().info("Connection closed");
                connection = null;
            }
        } catch (SQLException e) {
            Gangs.getPluginStatic().getLogger().severe("Failed to close connection: " + e.getMessage());
        }
    }

}
