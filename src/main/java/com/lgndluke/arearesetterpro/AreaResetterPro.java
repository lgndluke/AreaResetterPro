package com.lgndluke.arearesetterpro;

import com.lgndluke.arearesetterpro.data.*;
import com.lgndluke.arearesetterpro.loaders.CommandLoader;
import com.lgndluke.arearesetterpro.loaders.ListenerLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * AreaResetterPro is a Minecraft Plugin enabling you to create, manage and reset selected areas of your world.
 * @author lgndluke
**/
public final class AreaResetterPro extends JavaPlugin {

    @Override
    public void onEnable() {

        //TODO Before Releasing AreaResetterPro Version 1.3
        // -> Rework Database Supervisor to prohibit SQL Injection attacks.
        // -> Integrate PlaceholderAPI support.
        // -> Add Error codes.
        // -> Rework Area creation process.
        // -> Add spawnpoints to areas -> each area must have a spawnpoint. (location where all players inside the area get teleported to on area reset!)

        UpdateHandler.check();

        MetricsHandler.connect(this);

        PositionsHandler.initialize();
        MessageHandler.initialize();
        ConfigHandler.initialize();
        DatabaseHandler.initialize();

        CommandLoader.load(this);
        ListenerLoader.load(this);

    }

    @Override
    public void onDisable() {

        MetricsHandler.disconnect();
        DatabaseHandler.disconnect();

    }

}
