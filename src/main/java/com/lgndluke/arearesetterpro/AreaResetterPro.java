package com.lgndluke.arearesetterpro;

import com.lgndluke.arearesetterpro.data.AutoResetHandler;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.arearesetterpro.data.PositionsHandler;
import com.lgndluke.arearesetterpro.data.SpawnPointHandler;
import com.lgndluke.arearesetterpro.loaders.CommandLoader;
import com.lgndluke.arearesetterpro.loaders.ListenerLoader;
import com.lgndluke.arearesetterpro.placeholders.AreaResetterProExpansion;
import com.lgndluke.lgndware.data.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * AreaResetterPro is a Minecraft Plugin enabling you to create, manage and reset selected areas of your world.
 * @author lgndluke
**/
public final class AreaResetterPro extends JavaPlugin {

    //TODO Before Releasing AreaResetterPro Version 1.4
    // -> IMPORTANT -> Add GUI and COMMAND capabilities to lgndware and re-write the plugin's whole command section.
    // -> IMPORTANT -> Fix to-do in AutoResetHandler.
    // -> Add Enable/Disable Resets feature for Areas.
    // -> Change the entity saving mechanism to not be a config-option anymore! -> Make it a create command argument.
    // -> Take a look at the Spawnpoint system and think about how to improve it further.

    private final List<AbstractHandler> handlerList = new ArrayList<>();
    private final List<Boolean> handlerInitList = new ArrayList<>();

    @Override
    public void onEnable() {

        MetricsHandler metricsHandler = new MetricsHandler(this, 19274);
        handlerList.add(metricsHandler); //0
        handlerInitList.add(metricsHandler.initialize());

        UpdateHandler updateHandler = new UpdateHandler(this, 109372);
        handlerList.add(updateHandler); //1
        handlerInitList.add(updateHandler.initialize());

        PositionsHandler positionsHandler = new PositionsHandler(this, "Positions.yml");
        handlerList.add(positionsHandler); //2
        handlerInitList.add(positionsHandler.initialize());

        SpawnPointHandler spawnPointHandler = new SpawnPointHandler(this, "SpawnPoint.yml");
        handlerList.add(spawnPointHandler); //3
        handlerInitList.add(spawnPointHandler.initialize());

        ConfigHandler configHandler = new ConfigHandler(this);
        handlerList.add(configHandler); //4
        handlerInitList.add(configHandler.initialize());

        MessageHandler messageHandler = new MessageHandler(this);
        handlerList.add(messageHandler); //5
        handlerInitList.add(messageHandler.initialize());

        while(!handlerInitList.get(5)) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        handlerList.add(databaseHandler); //6
        handlerInitList.add(databaseHandler.initialize());

        while(!handlerInitList.get(6)) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        AutoResetHandler autoResetHandler = new AutoResetHandler(this);
        handlerList.add(autoResetHandler); //7
        handlerInitList.add(autoResetHandler.initialize());

        CommandLoader commandLoader = new CommandLoader(this);
        commandLoader.load();

        ListenerLoader listenerLoader = new ListenerLoader(this);
        listenerLoader.load();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new AreaResetterProExpansion().register();
        }

    }

    @Override
    public void onDisable() {
        for(AbstractHandler handler : this.handlerList) {
            handler.terminate();
        }
    }

    public PositionsHandler getPositionsHandler() {
        return (PositionsHandler) this.handlerList.get(2);
    }

    public SpawnPointHandler getSpawnPointHandler() {
        return (SpawnPointHandler) this.handlerList.get(3);
    }

    public ConfigHandler getConfigHandler() {
        return (ConfigHandler) this.handlerList.get(4);
    }

    public MessageHandler getMessageHandler() {
        return (MessageHandler) this.handlerList.get(5);
    }

    public DatabaseHandler getDatabaseHandler() {
        return (DatabaseHandler) this.handlerList.get(6);
    }

    public AutoResetHandler getAutoResetHandler() {
        return (AutoResetHandler) this.handlerList.get(7);
    }

}
