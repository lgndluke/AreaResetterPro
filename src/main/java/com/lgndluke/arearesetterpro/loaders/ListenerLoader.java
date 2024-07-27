package com.lgndluke.arearesetterpro.loaders;

import com.lgndluke.arearesetterpro.listeners.SetPosToolListener;
import com.lgndluke.lgndware.loaders.AbstractListenerLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This Class used to register Plugin listeners during server startup and manage them thereafter.
 * @author lgndluke
 **/
public class ListenerLoader extends AbstractListenerLoader {

    private final SetPosToolListener setPosToolListener = new SetPosToolListener();

    public ListenerLoader(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void load() {
        super.getPlugin().getServer().getPluginManager().registerEvents(setPosToolListener, super.getPlugin());
    }

}
