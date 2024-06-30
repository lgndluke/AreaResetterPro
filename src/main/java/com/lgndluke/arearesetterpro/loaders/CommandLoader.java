package com.lgndluke.arearesetterpro.loaders;

import com.lgndluke.arearesetterpro.commands.*;
import com.lgndluke.lgndware.loaders.AbstractCommandLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Static Class used to register Plugin commands during server startup.
 * @author lgndluke
 **/
public class CommandLoader extends AbstractCommandLoader {

    public CommandLoader(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void load() {

        super.getPlugin().getCommand("arp_reload").setExecutor(new Reload());
        super.getPlugin().getCommand("arp_tool").setExecutor(new Tool());
        super.getPlugin().getCommand("arp_getpos").setExecutor(new GetPos());
        super.getPlugin().getCommand("arp_setspawnpoint").setExecutor(new SetSpawnPoint());
        super.getPlugin().getCommand("arp_getspawnpoint").setExecutor(new GetSpawnPoint());
        super.getPlugin().getCommand("arp_create").setExecutor(new Create());
        super.getPlugin().getCommand("arp_remove").setExecutor(new Remove());
        super.getPlugin().getCommand("arp_reset").setExecutor(new Reset());
        super.getPlugin().getCommand("arp_menu").setExecutor(new Menu());
        super.getPlugin().getCommand("arp_help").setExecutor(new Help());

    }

}
