package com.lgndluke.arearesetterpro.data;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * This Class represents the 'SpawnPoint.yml' file.
 * @author lgndluke
 **/
public class SpawnPointHandler {

    //Static Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static File spawnPointFile;
    private static FileConfiguration spawnPointFileConf;

    //Static Methods

    /**
     * Initializes the PositionsHandler on server startup.
     **/
    public static void initialize() {

        if(!areaPlugin.getDataFolder().exists()) {
            boolean folderCreated = areaPlugin.getDataFolder().mkdir();
            if(folderCreated) {
                areaPlugin.getLogger().log(Level.INFO, "Successfully created 'AreaResetterPro' folder inside plugins folder.");
            }
        }

        spawnPointFile = new File(areaPlugin.getDataFolder().getAbsolutePath(), "SpawnPoint.yml");

        if(!spawnPointFile.exists()) {
            try {
                boolean fileCreated = spawnPointFile.createNewFile();
                if(fileCreated) {
                    areaPlugin.getLogger().log(Level.INFO, "Successfully created 'SpawnPoint.yml' file.");
                }
            } catch (IOException e) {
                areaPlugin.getLogger().log(Level.SEVERE, "Could not create 'SpawnPoint.yml' file!", e);
                return;
            }
        }

        spawnPointFileConf = YamlConfiguration.loadConfiguration(spawnPointFile);
        spawnPointFileConf.options().copyDefaults(true);
        save();

    }

    /**
     * Reloads the 'SpawnPoint.yml' file.
     * @throws IOException if 'SpawnPoint.yml' doesn't exist or can't be accessed.
     **/
    public static void reload() throws IOException {
        spawnPointFileConf = YamlConfiguration.loadConfiguration(spawnPointFile);
        spawnPointFileConf.options().copyDefaults(true);
        save();
    }

    /**
     * Saves the 'SpawnPoint.yml' file.
     **/
    public static void save() {
        try {
            spawnPointFileConf.save(spawnPointFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't save data to 'SpawnPoint.yml'", e);
        }
    }

    /**
     * Reads the Location inside the 'SpawnPoint.yml' file.
     * @return requested Location value.
     **/
    public static Location getSpawnPoint(SpawnPoint spawnPoint) {
        return spawnPointFileConf.getLocation(spawnPoint.toString());
    }

    /**
     * Writes the SpawnPoint into the 'SpawnPoint.yml' file.
     * @param spawnPoint value of the 'SpawnPoint' enum.
     * @param location represents the to be stored Position.
     **/
    public static void setSpawnPoint(SpawnPoint spawnPoint, Location location) {
        spawnPointFileConf.set(spawnPoint.toString(), location);
        save();
    }

    /**
     * Public Enum Position.
     * Decides, which Position is operated with.
     * @author lgndluke
     **/
    public enum SpawnPoint { SPAWNPOINT }

}
