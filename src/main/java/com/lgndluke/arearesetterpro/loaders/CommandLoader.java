package com.lgndluke.arearesetterpro.loaders;

import com.lgndluke.arearesetterpro.commands.*;
import com.lgndluke.lgndware.loaders.AbstractCommandLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This Class is used to register Plugin commands during server startup.
 * @author lgndluke
 **/
public class CommandLoader extends AbstractCommandLoader {

    public CommandLoader(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void load() {

        super.getPlugin().getCommand("arp_reload").setExecutor(new ReloadCmd());
        super.getPlugin().getCommand("arp_tool").setExecutor(new ToolCmd());
        super.getPlugin().getCommand("arp_getpos").setExecutor(new GetPosCmd());
        super.getPlugin().getCommand("arp_setspawnpoint").setExecutor(new SetSpawnPointCmd());
        super.getPlugin().getCommand("arp_getspawnpoint").setExecutor(new GetSpawnPointCmd());
        super.getPlugin().getCommand("arp_create").setExecutor(new CreateCmd());
        super.getPlugin().getCommand("arp_remove").setExecutor(new RemoveCmd());
        super.getPlugin().getCommand("arp_reset").setExecutor(new ResetCmd());
        super.getPlugin().getCommand("arp_menu").setExecutor(new MenuCmd());
        super.getPlugin().getCommand("arp_help").setExecutor(new HelpCmd());
        super.getPlugin().getCommand("arp_enable").setExecutor(new EnableCmd());
        super.getPlugin().getCommand("arp_disable").setExecutor(new DisableCmd());

    }

}
