package com.lgndluke.arearesetterpro.data;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.lgndware.data.AbstractDatabaseHandler;
import com.lgndluke.lgndware.data.ConfigHandler;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * This class handles database operations.
 * @author lgndluke
 **/
public class DatabaseHandler extends AbstractDatabaseHandler {

    public DatabaseHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean initialize() {
        FutureTask<Boolean> initDatabaseHandler = new FutureTask<>(() -> {
            createDatabase();
            connect();
            createTables();
            return true;
        });
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), initDatabaseHandler, 10, TimeUnit.SECONDS);
    }

    public ResultSet getAreaData() {
        FutureTask<ResultSet> getAreaDataTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("SELECT * FROM AreaData;");
                return prepState.executeQuery();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                return null;
            }
        });
        return super.getDefaultAsyncExecutor().fetchExecutionResult(getPlugin().getLogger(), getAreaDataTask, 10, TimeUnit.SECONDS);
    }

    public ResultSet getAreaData(String areaName) {
        FutureTask<ResultSet> getAreaDataTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("SELECT * FROM AreaData WHERE areaName = ?;");
                prepState.setString(1, areaName);
                return prepState.executeQuery();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                return null;
            }
        });
        return super.getDefaultAsyncExecutor().fetchExecutionResult(getPlugin().getLogger(), getAreaDataTask, 10, TimeUnit.SECONDS);
    }

    public void insertAreaData(UUID uuid, String areaName, String worldName, Location pos1, Location pos2, Location spawn) {
        FutureTask<Boolean> insertAreaDataTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("INSERT INTO AreaData " +
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
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't insert AreaData!", se);
            }
            return true;
        });
        super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), insertAreaDataTask, 10, TimeUnit.SECONDS);
    }

    public void deleteAreaData(String areaName) {
        FutureTask<Boolean> deleteAreaDataTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("DELETE FROM AreaData WHERE areaName = ?;");
                prepState.setString(1, areaName);
                prepState.execute();
                prepState.close();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't delete AreaData!", se);
            }
            return true;
        });
        super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), deleteAreaDataTask, 10, TimeUnit.SECONDS);
    }

    public List<String> getAreaDataNames() {
        FutureTask<List<String>> getAreaDataNamesTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("SELECT areaName AS name FROM AreaData;");
                ResultSet queryResults = prepState.executeQuery();
                List<String> results = new ArrayList<>();
                while(queryResults.next()) {
                    results.add(queryResults.getString("name"));
                }
                return results;
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                return null;
            }
        });
        return super.getDefaultAsyncExecutor().fetchExecutionResultAsList(super.getPlugin().getLogger(), getAreaDataNamesTask, 10, TimeUnit.SECONDS);
    }

    public int getAreaDataSize() {
        FutureTask<ResultSet> getAreaDataSizeTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("SELECT COUNT(*) AS amount FROM AreaData;");
                return prepState.executeQuery();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                return null;
            }
        });
        try {
            return super.getDefaultAsyncExecutor().fetchExecutionResult(getPlugin().getLogger(), getAreaDataSizeTask, 10, TimeUnit.SECONDS).getInt("amount");
        } catch (SQLException se) {
            super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            return 0;
        }
    }

    public ResultSet getAreaStats(UUID uuid) {
        FutureTask<ResultSet> getAreaStatsTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("SELECT * FROM AreaStats WHERE uuid = ?;");
                prepState.setObject(1, uuid);
                return prepState.executeQuery();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaStats!", se);
                return null;
            }
        });
        return super.getDefaultAsyncExecutor().fetchExecutionResult(getPlugin().getLogger(), getAreaStatsTask, 10, TimeUnit.SECONDS);
    }

    public void insertAreaStats(UUID uuid, long overallBlocks) {
        final ConfigHandler configHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getConfigHandler();
        FutureTask<Boolean> insertAreaStatsTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("INSERT INTO AreaStats (uuid, timesReset, overallBlocks, entitiesSaved, createdOn) VALUES (?, 0, ?, ?, ?);");
                prepState.setString(1, uuid.toString());
                prepState.setLong(2, overallBlocks);
                prepState.setBoolean(3, (boolean) configHandler.get("SaveEntities"));
                prepState.setString(4, new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
                prepState.execute();
                prepState.close();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't insert AreaStats!", se);
            }
            return true;
        });
        super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), insertAreaStatsTask, 10, TimeUnit.SECONDS);
    }

    public void updateAreaStatsTimesReset(UUID uuid, int timesReset) {
        FutureTask<Boolean> updateAreaStatsTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("UPDATE AreaStats SET timesReset = ? WHERE uuid = ?;");
                prepState.setInt(1, Math.addExact(timesReset, 1));
                prepState.setObject(2, uuid);
                prepState.execute();
                prepState.close();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't update AreaStats 'timesReset'!", se);
            }
            return true;
        });
        super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), updateAreaStatsTask, 10, TimeUnit.SECONDS);
    }

    public void deleteAreaStats(UUID uuid) {
        FutureTask<Boolean> deleteAreaStatsTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("DELETE FROM AreaStats WHERE uuid = ?;");
                prepState.setObject(1, uuid);
                prepState.execute();
                prepState.close();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't delete AreaStats", se);
            }
            return true;
        });
        super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), deleteAreaStatsTask, 10, TimeUnit.SECONDS);
    }

    public int getAreaStatsSize() {
        FutureTask<ResultSet> getAreaStatsSizeTask= new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("SELECT COUNT(*) AS amount FROM AreaStats;");
                return prepState.executeQuery();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaStats!", se);
                return null;
            }
        });
        try {
            return super.getDefaultAsyncExecutor().fetchExecutionResult(getPlugin().getLogger(), getAreaStatsSizeTask, 10, TimeUnit.SECONDS).getInt("amount");
        } catch (SQLException se) {
            super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaStats!", se);
            return 0;
        }
    }

    public ResultSet getAreaTimer(UUID uuid) {
        FutureTask<ResultSet> getAreaTimerTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("SELECT * FROM AreaTimer WHERE uuid = ?;");
                prepState.setObject(1, uuid);
                return prepState.executeQuery();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaTimer!", se);
                return null;
            }
        });
        return super.getDefaultAsyncExecutor().fetchExecutionResult(getPlugin().getLogger(), getAreaTimerTask, 10, TimeUnit.SECONDS);
    }

    public void insertAreaTimer(UUID uuid, int configTimerValue) {
        FutureTask<Boolean> insertAreaTimerTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("INSERT INTO AreaTimer (uuid, timerValue) VALUES (?, ?);");
                prepState.setString(1, uuid.toString());
                prepState.setInt(2, Math.max(configTimerValue, 1));
                prepState.execute();
                prepState.close();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't insert AreaTimer!", se);
            }
            return true;
        });
        super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), insertAreaTimerTask, 10, TimeUnit.SECONDS);
    }

    public void updateAreaTimerTimerValue(UUID uuid, int timerValue) {
        FutureTask<Boolean> updateAreaTimerTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("UPDATE AreaTimer SET timerValue = ? WHERE uuid = ?;");
                prepState.setInt(1, Math.max(timerValue, 1));
                prepState.setObject(2, uuid);
                prepState.execute();
                prepState.close();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't update AreaTimer 'timerValue'!", se);
            }
            return true;
        });
        super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), updateAreaTimerTask, 10, TimeUnit.SECONDS);
    }

    public void deleteAreaTimer(UUID uuid) {
        FutureTask<Boolean> deleteAreaStatsTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("DELETE FROM AreaTimer WHERE uuid = ?;");
                prepState.setObject(1, uuid);
                prepState.execute();
                prepState.close();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't delete AreaTimer!", se);
            }
            return true;
        });
        super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), deleteAreaStatsTask, 10, TimeUnit.SECONDS);
    }

    public int getAreaTimerSize() {
        FutureTask<ResultSet> getAreaTimerSizeTask = new FutureTask<>(() -> {
            try {
                PreparedStatement prepState = super.getDbCon().prepareStatement("SELECT COUNT(*) AS amount FROM AreaTimer;");
                return prepState.executeQuery();
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaTimer!", se);
                return null;
            }
        });
        try {
            return super.getDefaultAsyncExecutor().fetchExecutionResult(getPlugin().getLogger(), getAreaTimerSizeTask, 10, TimeUnit.SECONDS).getInt("amount");
        } catch (SQLException se) {
            super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaTimer!", se);
            return 0;
        }
    }

    @Override
    protected void createDatabase() {

        try {
            super.getPlugin().getLogger().log(Level.INFO, "Looking for SQLite-Database ... ");
            File database = new File(super.getDbPath());
            boolean isCreated = database.createNewFile();

            if(isCreated) {
                super.getPlugin().getLogger().log(Level.WARNING, "No Database was found!");
                super.getPlugin().getLogger().log(Level.INFO, "Trying to create new Database ... ");
                super.getPlugin().getLogger().log(Level.INFO, "Successfully created new Database.");
            } else {
                super.getPlugin().getLogger().log(Level.INFO, "Existing Database was found.");
            }

        } catch (IOException io) {
            super.getPlugin().getLogger().log(Level.SEVERE, "Failed to create new Database!");
            super.getPlugin().getLogger().log(Level.SEVERE, "A IO-Exception occurred.", io);
        } catch (SecurityException se) {
            super.getPlugin().getLogger().log(Level.SEVERE, "Failed to create new Database!");
            super.getPlugin().getLogger().log(Level.SEVERE, "A SecurityException occurred", se);
        }

    }

    @Override
    protected void createTables() {

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
                "entitiesSaved BOOLEAN NOT NULL, " +
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
            Statement dbStatement = super.getDbCon().createStatement();
            dbStatement.execute(sqlAreaData);
            dbStatement.execute(sqlStats);
            dbStatement.execute(sqlTimer);
            dbStatement.close();
            super.getPlugin().getLogger().log(Level.INFO, "Successfully initialized Database tables!");
        } catch (SQLException se) {
            super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't create Database-Tables!", se);
        }

    }

}
