package com.lgndluke.lgndware.data;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for handling configuration related operations.
 * @author lgndluke
 **/
public class ConfigHandler extends AbstractFileHandler {

    /**
     * @param plugin The JavaPlugin instance associated with this ConfigHandler.
     **/
    public ConfigHandler(JavaPlugin plugin) {
        super(plugin, "config.yml");
    }

    /**
     * Initializes the 'config.yml' file on server startup.
     * @return True, if the initialization was successful. Otherwise, false.
     **/
    @Override
    public boolean initialize() {
        FutureTask<Boolean> initConfigHandler = new FutureTask<>(() -> {
            super.getPlugin().getConfig().options().copyDefaults(true);
            super.getPlugin().saveDefaultConfig();
            return true;
        });
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), initConfigHandler, 10, TimeUnit.SECONDS);
    }

    /**
     * Asynchronously reloads the 'config.yml' file from disk.
     * @return True, if the reload was successful. Otherwise, false.
     **/
    @Override
    public boolean reload() {
        FutureTask<Boolean> reloadConfig = new FutureTask<>(() -> {
            super.getPlugin().reloadConfig();
            super.getPlugin().getConfig().options().copyDefaults(true);
            super.getPlugin().saveConfig();
            return true;
        });
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), reloadConfig, 10, TimeUnit.SECONDS);
    }

    /**
     * Asynchronously saves the 'config.yml' file to disk.
     * @return True, if the save operation was successful. Otherwise, false.
     **/
    @Override
    public boolean save() {
        FutureTask<Boolean> saveConfig = new FutureTask<>(() -> {
            super.getPlugin().saveConfig();
            return true;
        });
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), saveConfig, 10, TimeUnit.SECONDS);
    }

    /**
     * Retrieves a configuration value from 'config.yml'
     *
     * @param value The key for the configuration value.
     * @return The requested Object from 'config.yml'.
     * @throws NullPointerException If the value isn't set inside 'config.yml'.
     **/
    public Object get(String value) throws NullPointerException {
        FutureTask<Object> getConfigValue = new FutureTask<>(() -> super.getPlugin().getConfig().get(value));
        return super.getDefaultAsyncExecutor().fetchExecutionResult(super.getPlugin().getLogger(), getConfigValue, 10, TimeUnit.SECONDS);
    }

}
