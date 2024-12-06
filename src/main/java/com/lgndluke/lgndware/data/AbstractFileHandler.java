package com.lgndluke.lgndware.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Abstract base class for file implementations.
 * @author lgndluke
 **/
public abstract class AbstractFileHandler extends AbstractHandler {

    private final File file;
    private FileConfiguration fileConfig;

    /**
     * @param plugin The JavaPlugin instance associated with this AbstractFileHandler.
     * @param fileName The file name including filetype.
     **/
    protected AbstractFileHandler(JavaPlugin plugin, String fileName) {
        super(plugin);
        createDataFolder();
        this.file = new File(super.getPlugin().getDataFolder().getAbsoluteFile(), fileName);
        this.fileConfig = YamlConfiguration.loadConfiguration(this.file);
    }

    /**
     * Asynchronously initializes the file.
     * @return True, if the initialization was successful. Otherwise, false.
     **/
    @Override
    public boolean initialize() {
        FutureTask<Boolean> initAbstractFileHandler = new FutureTask<>(() -> {
            createFile();
            this.fileConfig = YamlConfiguration.loadConfiguration(this.file);
            this.fileConfig.options().copyDefaults(true);
            return save();
        });
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), initAbstractFileHandler, 10, TimeUnit.SECONDS);
    }

    /**
     * Terminates the AbstractFileHandler.
     * @return True, if the termination was successful. Otherwise, false.
     **/
    @Override
    public boolean terminate() {
        super.getDefaultAsyncExecutor().shutdown();
        super.getScheduledAsyncExecutor().shutdown();
        return true;
    }

    /**
     * Asynchronously reloads the file.
     * @return True, if the file was reloaded successfully. Otherwise, false.
     **/
    public boolean reload() {
        FutureTask<Boolean> reloadFile = new FutureTask<>(() -> {
            this.fileConfig = YamlConfiguration.loadConfiguration(this.file);
            this.fileConfig.options().copyDefaults(true);
            save();
            return true;
        });
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), reloadFile, 10, TimeUnit.SECONDS);
    }

    /**
     * Asynchronously saves the file.
     * @return True, if the file was saved successfully. Otherwise, false.
     **/
    public boolean save() {
        FutureTask<Boolean> saveFile = new FutureTask<>(() -> {
            try {
                this.fileConfig.save(this.file);
            } catch (IOException e) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't save data to " + this.file.getName(), e);
            }
            return true;
        });
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), saveFile, 10, TimeUnit.SECONDS);
    }

    /**
     * @return The AbstractFileHandlers file configuration object.
     **/
    protected FileConfiguration getFileConfig() {
        return this.fileConfig;
    }

    /**
     * @return The AbstractFileHandlers file object.
     **/
    protected File getFile() {
        return this.file;
    }

    /**
     * This method creates the Plugin folder inside the Servers plugins folder.
     **/
    private void createDataFolder() {
        if(!super.getPlugin().getDataFolder().exists()) {
            boolean isCreated = super.getPlugin().getDataFolder().mkdir();
            if(isCreated) {
                super.getPlugin().getLogger().log(Level.INFO, "Successfully created " + super.getPlugin().getName() + " folder inside plugins folder.");
            }
        }
    }

    /**
     * This method creates the file itself.
     **/
    protected void createFile() {
        if(!this.file.exists()) {
            try {
                boolean fileCreated = this.file.createNewFile();
                if(fileCreated) {
                    super.getPlugin().getLogger().log(Level.INFO, "Successfully created " + this.file.getName() + " file.");
                }
            } catch (IOException e) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Could not create " + this.file.getName() + " file!", e);
            }
        }
    }

}
