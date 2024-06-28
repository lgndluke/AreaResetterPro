package com.lgndluke.arearesetterpro;

import com.lgndluke.arearesetterpro.data.AutoResetHandler;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.arearesetterpro.data.PositionsHandler;
import com.lgndluke.arearesetterpro.data.SpawnPointHandler;
import com.lgndluke.arearesetterpro.loaders.CommandLoader;
import com.lgndluke.arearesetterpro.loaders.ListenerLoader;
import com.lgndluke.arearesetterpro.placeholders.AreaResetterProExpansion;
import com.lgndluke.lgndware.data.*;
import com.lgndluke.lgndware.loaders.AbstractLoader;
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
    // -> Add Enable/Disable Resets feature for Areas.
    // -> Change the entity saving mechanism to not be a config-option anymore! -> Make it a create command argument.
    // -> Take a look at the Spawnpoint system and think about how to improve it further.

    private final List<AbstractHandler> handlerList = new ArrayList<>();
    private final List<AbstractLoader> loaderList = new ArrayList<>();

    @Override
    public void onEnable() {

        MetricsHandler metricsHandler = new MetricsHandler(this, 19274);
        metricsHandler.initialize();
        handlerList.add(metricsHandler); //0

        UpdateHandler updateHandler = new UpdateHandler(this, 109372);
        updateHandler.initialize();
        handlerList.add(updateHandler); //1

        PositionsHandler positionsHandler = new PositionsHandler(this, "Positions.yml");
        positionsHandler.initialize();
        handlerList.add(positionsHandler); //2

        SpawnPointHandler spawnPointHandler = new SpawnPointHandler(this, "SpawnPoint.yml");
        spawnPointHandler.initialize();
        handlerList.add(spawnPointHandler); //3

        ConfigHandler configHandler = new ConfigHandler(this);
        configHandler.initialize();
        handlerList.add(configHandler); //4

        MessageHandler messageHandler = new MessageHandler(this);
        messageHandler.initialize();
        handlerList.add(messageHandler); //5

        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        databaseHandler.initialize();
        handlerList.add(databaseHandler); //6

        AutoResetHandler autoResetHandler = new AutoResetHandler(this);
        autoResetHandler.initialize();
        handlerList.add(autoResetHandler); //7

        CommandLoader commandLoader = new CommandLoader(this);
        commandLoader.load();
        loaderList.add(commandLoader); //0

        ListenerLoader listenerLoader = new ListenerLoader(this);
        listenerLoader.load();
        loaderList.add(listenerLoader); //1

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

    public ListenerLoader getListenerLoader() { return (ListenerLoader) this.loaderList.get(1); }

}
