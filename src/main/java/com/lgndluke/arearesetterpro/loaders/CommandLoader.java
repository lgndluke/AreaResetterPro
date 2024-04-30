package com.lgndluke.arearesetterpro.loaders;

import com.lgndluke.arearesetterpro.commands.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Static Class used to register Plugin commands during server startup.
 * @author lgndluke
 **/
public class CommandLoader {

    //Static Method

    /**
     * Initialize CommandExecutor objects here.
     * Ignore possible NullPointerException.
     **/
    public static void load(JavaPlugin javaPlugin) {

        javaPlugin.getCommand("arp_reload").setExecutor(new Reload());
        javaPlugin.getCommand("arp_tool").setExecutor(new Tool());
        javaPlugin.getCommand("arp_getpos").setExecutor(new GetPos());
        javaPlugin.getCommand("arp_create").setExecutor(new Create());
        javaPlugin.getCommand("arp_remove").setExecutor(new Remove());
        javaPlugin.getCommand("arp_reset").setExecutor(new Reset());
        javaPlugin.getCommand("arp_menu").setExecutor(new Menu());
        javaPlugin.getCommand("arp_help").setExecutor(new Help());

    }

}
