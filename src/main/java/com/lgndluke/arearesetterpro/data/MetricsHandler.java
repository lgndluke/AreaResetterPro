package com.lgndluke.arearesetterpro.data;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This Class is used to create a connection with bStats Metrics.
 * @author lgndluke
 **/
public class MetricsHandler {

    //Static Attributes
    private static final int pluginID = 19274;
    private static Metrics metrics;

    //Static Methods

    /**
     * Establishes a connection to bStats metrics system.
     **/
    public static void connect(JavaPlugin javaPlugin) {
        metrics = new Metrics(javaPlugin, pluginID);
    }

    /**
     * Terminates the existing bStats metrics connection on server shutdown.
     **/
    public static void disconnect() {
        if(metrics != null) {
            metrics.shutdown();
        }
    }

}
