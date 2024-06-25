package com.lgndluke.arearesetterpro.data;

import com.lgndluke.arearesetterpro.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This Class is used to create a connection with bStats Metrics.
 * @author lgndluke
 **/
public class MetricsHandler {

    private static final int pluginID = 19274;
    private static Metrics metrics;

    /**
     * Establishes a connection to bStats metrics.
     **/
    public static void connect(JavaPlugin javaPlugin) {
        metrics = new Metrics(javaPlugin, pluginID);
    }

    /**
     * Terminates an existing bStats Metrics connection.
     **/
    public static void disconnect() {
        if(metrics != null) {
            metrics.shutdown();
        }
    }

}
