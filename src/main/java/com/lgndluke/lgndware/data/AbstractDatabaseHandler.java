package com.lgndluke.lgndware.data;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Abstract base class for database implementations.
 * @author lgndluke
 **/
public abstract class AbstractDatabaseHandler extends AbstractHandler {

    private final String dbPath = super.getPlugin().getDataFolder().getAbsolutePath() + "/" + super.getPlugin().getName() + ".db";
    private Connection dbCon;

    /**
     * @param plugin The JavaPlugin instance associated with this AbstractDatabaseHandler.
     **/
    protected AbstractDatabaseHandler(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Asynchronously initializes the database.
     * @return True, if the initialization was successful. Otherwise, false.
     **/
    @Override
    public boolean initialize() {
        FutureTask<Boolean> initAbstractDatabaseHandler = new FutureTask<>(() -> {
            createDatabase();
            connect();
            createTables();
            return true;
        });
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), initAbstractDatabaseHandler, 10, TimeUnit.SECONDS);
    }

    /**
     * Terminates the AbstractDatabaseHandler.
     * @return True, if the termination was successful. Otherwise, false.
     **/
    @Override
    public boolean terminate() {
        try {
            if(!dbCon.isClosed()) {
                dbCon.close();
            }
        } catch (SQLException se) {
            super.getPlugin().getLogger().log(Level.SEVERE, "An error occurred, whilst trying to close the database connection!", se);
        }
        super.getDefaultAsyncExecutor().shutdown();
        super.getScheduledAsyncExecutor().shutdown();
        return true;
    }

    /**
     * Abstract method to be implemented by subclasses for creating the database.
     **/
    protected abstract void createDatabase();

    /**
     * Abstract method to be implemented by subclasses for creating database tables.
     **/
    protected abstract void createTables();

    /**
     * This method establishes a connection to the database.
     **/
    protected void connect() {
        String dbURL = "jdbc:sqlite:" + dbPath;
        try {
            this.dbCon = DriverManager.getConnection(dbURL);
            if(this.dbCon != null) {
                super.getPlugin().getLogger().log(Level.INFO, "Successfully connected to Database.");
            }
        } catch (Exception e) {
            super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't connect to Database", e);
        }
    }

    /**
     * @return The file path of the database.
     **/
    protected String getDbPath() {
        return this.dbPath;
    }

    /**
     * @return The database connection.
     **/
    protected Connection getDbCon() {
        return this.dbCon;
    }

}
