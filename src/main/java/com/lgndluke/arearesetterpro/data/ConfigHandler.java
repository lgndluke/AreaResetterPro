package com.lgndluke.arearesetterpro.data;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

/**
 * This is a config related utility Class.
 * @author lgndluke
 **/
public class ConfigHandler {

    //Static Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);

    //Static Methods
    /**
     * Initializes the 'config.yml' file on server startup.
     **/
    public static void initialize() {
        areaPlugin.getConfig().options().copyDefaults();
        areaPlugin.saveDefaultConfig();
    }

    /** Reloads the 'config.yml' file.
     * @throws IOException if path to 'config.yml' is invalid or access is denied.
     **/
    public static void reload() throws IOException {
        areaPlugin.reloadConfig();
        areaPlugin.getConfig().options().copyDefaults();
        areaPlugin.saveConfig();
    }

    /** Provides easy access to objects from the Plugins config.
     * @param value has to be set inside the 'config.yml' file.
     * @return requested Object from 'config.yml' file.
     * @throws NullPointerException if value isn't set inside 'config.yml'
     **/
    public static Object get(String value) {
        return areaPlugin.getConfig().get(value);
    }

}
