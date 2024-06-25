package com.lgndluke.arearesetterpro;

import com.lgndluke.arearesetterpro.data.*;
import com.lgndluke.arearesetterpro.loaders.CommandLoader;
import com.lgndluke.arearesetterpro.loaders.ListenerLoader;
import com.lgndluke.arearesetterpro.placeholders.AreaResetterProExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * AreaResetterPro is a Minecraft Plugin enabling you to create, manage and reset selected areas of your world.
 * @author lgndluke
**/
public final class AreaResetterPro extends JavaPlugin {

    //TODO Before Releasing AreaResetterPro Version 1.4
    // -> Add Enable/Disable Resets feature for Areas.
    // -> Change the entity saving mechanism to not be a config-option anymore! -> Make it a create command argument.
    // -> Take a look at the Spawnpoint system and think about how to improve it further.

    @Override
    public void onEnable() {

        UpdateHandler.check();
        MetricsHandler.connect(this);
        PositionsHandler.initialize();
        SpawnPointHandler.initialize();
        MessageHandler.initialize();
        ConfigHandler.initialize();
        DatabaseHandler.initialize();
        CommandLoader.load(this);
        ListenerLoader.load();
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new AreaResetterProExpansion().register();
        }

    }

    @Override
    public void onDisable() {

        MetricsHandler.disconnect();
        DatabaseHandler.disconnect();

    }

}
