package com.lgndluke.arearesetterpro.data;

import com.lgndluke.lgndware.data.AbstractFileHandler;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This Class represents the 'Positions.yml' file.
 * @author lgndluke
 **/
public class PositionsHandler extends AbstractFileHandler {

    public PositionsHandler(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);
    }

    /**
     * Reads a Location from the 'Positions.yml' file.
     * @param posName value of the 'Position' enum.
     * @return requested Location value.
     **/
    public Location getPosition(Position posName) {
        return super.getFileConfig().getLocation(posName.toString());
    }

    /**
     * Writes a Location into the 'Positions.yml' file.
     * @param posName value of the 'Position' enum.
     * @param location represents the to be stored Position.
     **/
    public void setPosition(Position posName, Location location) {
        super.getFileConfig().set(posName.toString(), location);
        save();
    }

    /**
     * Public Enum Position.
     * Decides, which Position is operated with.
     * @author lgndluke
     **/
    public enum Position { POS1, POS2 }

}
