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
 * This Class represents the 'Positions.yml' file.
 * @author lgndluke
 **/
public class PositionsHandler {

    //Static Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static File posFile;
    private static FileConfiguration posFileConf;

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

        posFile = new File(areaPlugin.getDataFolder().getAbsolutePath(), "Positions.yml");

        if(!posFile.exists()) {
            try {
                boolean fileCreated = posFile.createNewFile();
                if(fileCreated) {
                    areaPlugin.getLogger().log(Level.INFO, "Successfully created 'Positions.yml' file.");
                }
            } catch (IOException e) {
                areaPlugin.getLogger().log(Level.SEVERE, "Could not create 'Positions.yml' file!", e);
                return;
            }
        }

        posFileConf = YamlConfiguration.loadConfiguration(posFile);
        posFileConf.options().copyDefaults(true);
        save();

    }

    /**
     * Reloads the 'Positions.yml' file.
     * @throws IOException if 'Positions.yml' doesn't exist or can't be accessed.
     **/
    public static void reload() throws IOException {
        posFileConf = YamlConfiguration.loadConfiguration(posFile);
        posFileConf.options().copyDefaults(true);
        save();
    }

    /**
     * Saves the 'Positions.yml' file.
     **/
    public static void save() {
        try {
            posFileConf.save(posFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Couldn't save data to 'Positions.yml'", e);
        }
    }

    /**
     * Reads a Location from the 'Positions.yml' file.
     * @param posName value of the 'Position' enum.
     * @return requested Location value.
     **/
    public static Location getPosition(Position posName) {
        return posFileConf.getLocation(posName.toString());
    }

    /**
     * Writes a Location into the 'Positions.yml' file.
     * @param posName value of the 'Position' enum.
     * @param location represents the to be stored Position.
     **/
    public static void setPosition(Position posName, Location location) {
        posFileConf.set(posName.toString(), location);
        save();
    }

    /**
     * Public Enum Position.
     * Decides, which Position is operated with.
     * @author lgndluke
     **/
    public enum Position { POS1, POS2 }

}
