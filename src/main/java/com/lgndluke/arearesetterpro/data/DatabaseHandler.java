package com.lgndluke.arearesetterpro.data;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.placeholders.AreaResetterProExpansion;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This class handles database operations.
 * @author lgndluke
 **/
public class DatabaseHandler {

    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static final String dbPath = areaPlugin.getDataFolder().getAbsolutePath() + "/AreaResetterPro.db";
    private static Connection dbCon;

    public static void initialize() {

        new Thread(() -> {
            createDatabase();
            connect();
            createTables();
            //Enable auto-resetter.
            if((boolean) ConfigHandler.get("EnableAutoResets")) {
                AutoResetHandler.initialize();
            }
            //Enable PlaceholderAPI expansion.
            AreaResetterProExpansion.updateValues();
        }).start();

    }

    public static void disconnect() {
        if(dbCon != null) {
            try {
                dbCon.close();
            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Error whilst trying to close the database connection!", se);
            }
        }
    }

    public static ResultSet getAreaData() {
        try {
            PreparedStatement prepState = dbCon.prepareStatement("SELECT * FROM AreaData;");
            return prepState.executeQuery();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            return null;
        }
    }

    public static ResultSet getAreaData(String areaName) {
        try {
            PreparedStatement prepState = dbCon.prepareStatement("SELECT * FROM AreaData WHERE areaName = ?;");
            prepState.setString(1, areaName);
            return prepState.executeQuery();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            return null;
        }
    }

    public static void insertAreaData(UUID uuid, String areaName, String worldName, Location pos1, Location pos2, Location spawn) {
        try {

            PreparedStatement prepState = dbCon.prepareStatement("INSERT INTO AreaData " +
                    "(uuid, areaName, world, xValPos1, yValPos1, zValPos1, xValPos2, yValPos2, zValPos2, xValSpawn, yValSpawn, zValSpawn)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

            prepState.setString(1, uuid.toString());
            prepState.setString(2, areaName);
            prepState.setString(3, worldName);
            prepState.setInt(4, pos1.getBlockX());
            prepState.setInt(5, pos1.getBlockY());
            prepState.setInt(6, pos1.getBlockZ());
            prepState.setInt(7, pos2.getBlockX());
            prepState.setInt(8, pos2.getBlockY());
            prepState.setInt(9, pos2.getBlockZ());
            prepState.setInt(10, spawn.getBlockX());
            prepState.setInt(11, spawn.getBlockY());
            prepState.setInt(12, spawn.getBlockZ());

            prepState.execute();
            prepState.close();

        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't insert AreaData!", se);
        }

    }

    public static void deleteAreaData(String areaName) {
        try {
            PreparedStatement prepState = dbCon.prepareStatement("DELETE FROM AreaData WHERE areaName = ?;");
            prepState.setString(1, areaName);
            prepState.execute();
            prepState.close();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't delete AreaData!", se);
        }
    }

    public static ResultSet getAreaStats(UUID uuid) {
        try {
            PreparedStatement prepState = dbCon.prepareStatement("SELECT * FROM AreaStats WHERE uuid = ?;");
            prepState.setObject(1, uuid);
            return prepState.executeQuery();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaStats!", se);
            return null;
        }
    }

    public static void insertAreaStats(UUID uuid, long overallBlocks) {
        try {
            PreparedStatement prepState = dbCon.prepareStatement("INSERT INTO AreaStats (uuid, timesReset, overallBlocks, createdOn) VALUES (?, 0, ?, ?);");
            prepState.setString(1, uuid.toString());
            prepState.setLong(2, overallBlocks);
            prepState.setString(3, new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
            prepState.execute();
            prepState.close();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't insert AreaStats!", se);
        }
    }

    public static void updateAreaStatsTimesReset(UUID uuid, int timesReset) {
        try {
            PreparedStatement prepState = dbCon.prepareStatement("UPDATE AreaStats SET timesReset = ? WHERE uuid = ?;");
            prepState.setInt(1, Math.addExact(timesReset, 1));
            prepState.setObject(2, uuid);
            prepState.execute();
            prepState.close();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't update AreaStats 'timesReset'!", se);
        }
    }

    public static void deleteAreaStats(UUID uuid) {
        try {
            PreparedStatement prepState = dbCon.prepareStatement("DELETE FROM AreaStats WHERE uuid = ?;");
            prepState.setObject(1, uuid);
            prepState.execute();
            prepState.close();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't delete AreaStats", se);
        }
    }

    public static ResultSet getAreaTimer(UUID uuid) {
        try {
            PreparedStatement prepState = dbCon.prepareStatement("SELECT * FROM AreaTimer WHERE uuid = ?;");
            prepState.setObject(1, uuid);
            return prepState.executeQuery();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaTimer!", se);
            return null;
        }
    }

    public static void insertAreaTimer(UUID uuid, int configTimerValue) {
        try {
            PreparedStatement prepState = dbCon.prepareStatement("INSERT INTO AreaTimer (uuid, timerValue) VALUES (?, ?);");
            prepState.setString(1, uuid.toString());
            prepState.setInt(2, Math.max(configTimerValue, 1));
            prepState.execute();
            prepState.close();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't insert AreaTimer!", se);
        }
    }

    public static void updateAreaTimerTimerValue(UUID uuid, int timerValue) {
        try {
            PreparedStatement prepState = dbCon.prepareStatement("UPDATE AreaTimer SET timerValue = ? WHERE uuid = ?;");
            prepState.setInt(1, Math.max(timerValue, 1));
            prepState.setObject(2, uuid);
            prepState.execute();
            prepState.close();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't update AreaTimer 'timerValue'!", se);
        }
    }

    public static void deleteAreaTimer(UUID uuid) {
        try {
            PreparedStatement prepState = dbCon.prepareStatement("DELETE FROM AreaTimer WHERE uuid = ?;");
            prepState.setObject(1, uuid);
            prepState.execute();
            prepState.close();
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't delete AreaTimer!", se);
        }
    }

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

    private static void createTables() {

        String sqlAreaData = "CREATE TABLE IF NOT EXISTS AreaData (" +
                "uuid TEXT NOT NULL, " +
                "areaName TEXT NOT NULL, " +
                "world TEXT NOT NULL, " +
                "xValPos1 INT NOT NULL, " +
                "yValPos1 INT NOT NULL, " +
                "zValPos1 INT NOT NULL, " +
                "xValPos2 INT NOT NULL, " +
                "yValPos2 INT NOT NULL, " +
                "zValPos2 INT NOT NULL, " +
                "xValSpawn INT NOT NULL, " +
                "yValSpawn INT NOT NULL, " +
                "zValSpawn INT NOT NULL, " +
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
                "timerValue INT NOT NULL," +
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
