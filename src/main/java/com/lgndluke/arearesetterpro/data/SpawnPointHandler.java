package com.lgndluke.arearesetterpro.data;

import com.lgndluke.lgndware.data.AbstractFileHandler;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This Class represents the 'SpawnPoint.yml' file.
 * @author lgndluke
 **/
public class SpawnPointHandler extends AbstractFileHandler {

    public SpawnPointHandler(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
    }

    /**
     * Reads the Location inside the 'SpawnPoint.yml' file.
     * @return requested Location value.
     **/
    public Location getSpawnPoint(SpawnPoint spawnPoint) {
        return super.getFileConfig().getLocation(spawnPoint.toString());
    }

    /**
     * Writes the SpawnPoint into the 'SpawnPoint.yml' file.
     * @param spawnPoint value of the 'SpawnPoint' enum.
     * @param location represents the to be stored Position.
     **/
    public void setSpawnPoint(SpawnPoint spawnPoint, Location location) {
        super.getFileConfig().set(spawnPoint.toString(), location);
        save();
    }

    /**
     * Public Enum Position.
     * Decides, which Position is operated with.
     * @author lgndluke
     **/
    public enum SpawnPoint { SPAWNPOINT }

}
