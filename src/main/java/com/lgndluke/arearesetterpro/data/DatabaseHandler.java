package com.lgndluke.arearesetterpro.data;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * This class handles database operations.
 * @author lgndluke
 **/
public class DatabaseHandler {

    //Static Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static final String dbPath = areaPlugin.getDataFolder().getAbsolutePath() + "/AreaResetterPro.db";
    private static Connection dbCon;

    //Static Methods

    /**
     * Initializes the DatabaseHandler asynchronously on server startup.
     **/
    public static void initialize() {

        new Thread(() -> {
            createDatabase();
            connect();
            createTables();
            //Enable auto-resetter.
            if((boolean) ConfigHandler.get("EnableAutoResets")) {
                AutoResetHandler.initialize();
            }
        }).start();

    }

    /**
     * Closes an existing database connection.
     **/
    public static void disconnect() {
        if(dbCon != null) {
            try {
                dbCon.close();
            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Error whilst trying to close the database connection!", se);
            }
        }
    }

    /**
     * Conveniently executes an SQL-Query specified as String.
     * @param sqlStatement has to be a valid SQL query request.
     * @return List<String> containing the results of the 1. Column of the queried RecordSet.
     **/
    public static List<String> executeQuery(String sqlStatement) {

        List<String> results = new ArrayList<>();

        try {

            Statement dbStatement = dbCon.createStatement();
            ResultSet result = dbStatement.executeQuery(sqlStatement);

            while(result.next()) {
                results.add(result.getString(1));
            }

            dbStatement.close();

        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't query SQL-statement!", se);
        }

        return results;

    }

    /**
     * Conveniently executes an SQL DML statement.
     * @param sqlStatement has to be a valid SQL DML statement.
     **/
    public static void execute(String sqlStatement) {

        try {
            Statement dbStatement = dbCon.createStatement();
            dbStatement.execute(sqlStatement);
            dbStatement.close();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't execute SQL-statement!", se);
        }

    }

    /**
     * Creates a new SQLite Database inside this Plugins folder.
     **/
    private static void createDatabase() {

        try {
            areaPlugin.getLogger().log(Level.INFO, "Looking for SQLite-Database ... ");
            File database = new File(dbPath);
            boolean isCreated = database.createNewFile();

            if(isCreated) {
                areaPlugin.getLogger().log(Level.WARNING, "No Database was found!");
                areaPlugin.getLogger().log(Level.INFO, "Trying to create new Database ... ");
                areaPlugin.getLogger().log(Level.INFO, "Successfully created new Database.");
            } else {
                areaPlugin.getLogger().log(Level.INFO, "Existing Database was found.");
            }

        } catch (IOException io) {
            areaPlugin.getLogger().log(Level.SEVERE, "Failed to create new Database!");
            areaPlugin.getLogger().log(Level.SEVERE, "A IO-Exception occurred.", io);
        } catch (SecurityException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Failed to create new Database!");
            areaPlugin.getLogger().log(Level.SEVERE, "A SecurityException occurred", se);
        }

    }

    /**
     * Creates necessary SQLite database tables.
     **/
    private static void createTables() {

        String sqlAreaData = "CREATE TABLE IF NOT EXISTS AreaData (" +
                "uuid TEXT NOT NULL, " +
                "areaName TEXT NOT NULL, " +
                "world TEXT NOT NULL, " +
                "x INT NOT NULL, " +
                "y INT NOT NULL, " +
                "z INT NOT NULL, " +
                "PRIMARY KEY (uuid)" +
                ");";

        String sqlStats = "CREATE TABLE IF NOT EXISTS AreaStats (" +
                "uuid TEXT NOT NULL, " +
                "timesReset INT NOT NULL, " +
                "overallBlocks BIGINT NOT NULL, " +
                "createdOn TEXT NOT NULL, " +
                "PRIMARY KEY (uuid), " +
                "CONSTRAINT fk_AreaStats_AreaData " +
                "FOREIGN KEY (uuid) " +
                "REFERENCES AreaData (uuid) " +
                "ON UPDATE CASCADE " +
                "ON DELETE CASCADE " +
                ");";

        String sqlTimer = "CREATE TABLE IF NOT EXISTS AreaTimer (" +
                "uuid TEXT NOT NULL, " +
                "timerValue INT NOT NULL," + //TODO FIX
                "PRIMARY KEY (uuid), " +
                "CONSTRAINT fk_AreaTimer_AreaData " +
                "FOREIGN KEY (uuid) " +
                "REFERENCES AreaData (uuid) " +
                "ON UPDATE CASCADE " +
                "ON DELETE CASCADE " +
                ");";

        try {
            Statement dbStatement = dbCon.createStatement();
            dbStatement.execute(sqlAreaData);
            dbStatement.execute(sqlStats);
            dbStatement.execute(sqlTimer);
            dbStatement.close();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't create Database-Tables!", se);
        }

    }

    /**
     * Establishes a connection to the SQLite database.
     **/
    private static void connect() {
        String dbURL = "jdbc:sqlite:" + dbPath;
        try {
            dbCon = DriverManager.getConnection(dbURL);
            if(dbCon != null) {
                areaPlugin.getLogger().log(Level.INFO, "Successfully connected to Database.");
            }
        } catch (Exception e) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't connect to Database", e);
        }
    }

}
