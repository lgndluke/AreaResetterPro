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
    // -> Add Reset Trigger Events inside the Menu.
    // -> Add Enable/Disable Resets feature for Areas.
    // -> Add Unit-Tests to ensure the Projects quality.
    // -> Ensure Spawnpoint is not set inside currently selected Area.

    @Override
    public void onEnable() {

        UpdateHandler.check();
        //MetricsHandler.connect(this); --> Wait for update to JDK 21 || Self-Update Metrics to support JDK21.
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

        //MetricsHandler.disconnect();
        DatabaseHandler.disconnect();

    }

}
