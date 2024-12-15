package com.lgndluke.arearesetterpro;

import com.lgndluke.arearesetterpro.autoresets.AutoResetHandler;
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

    //TODO Before Releasing AreaResetterPro Version 1.4.0.
    // -> Add Enable/Disable Resets feature for Areas.
    // -> Add Area-Visualization when holding the arp_tool in hand.
    // -> <Optional>: Rework UI to make it more pretty.
    // -> Implement better Error Handling

    private final List<AbstractHandler> handlerList = new ArrayList<>();
    private final List<Boolean> handlerInitList = new ArrayList<>();
    private AreaResetterProExpansion areaResetterProExpansion;

    @Override
    public void onEnable() {

        MetricsHandler metricsHandler = new MetricsHandler(this, 19274);
        handlerList.add(metricsHandler);
        handlerInitList.add(metricsHandler.initialize());

        UpdateHandler updateHandler = new UpdateHandler(this, 109372);
        handlerList.add(updateHandler);
        handlerInitList.add(updateHandler.initialize());

        PositionsHandler positionsHandler = new PositionsHandler(this, "Positions.yml");
        handlerList.add(positionsHandler);
        handlerInitList.add(positionsHandler.initialize());

        SpawnPointHandler spawnPointHandler = new SpawnPointHandler(this, "SpawnPoint.yml");
        handlerList.add(spawnPointHandler);
        handlerInitList.add(spawnPointHandler.initialize());

        ConfigHandler configHandler = new ConfigHandler(this);
        handlerList.add(configHandler);
        handlerInitList.add(configHandler.initialize());

        MessageHandler messageHandler = new MessageHandler(this);
        handlerList.add(messageHandler);
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
        handlerList.add(autoResetHandler);
        handlerInitList.add(autoResetHandler.initialize());

        CommandLoader commandLoader = new CommandLoader(this);
        commandLoader.load();

        ListenerLoader listenerLoader = new ListenerLoader(this);
        listenerLoader.load();

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            areaResetterProExpansion = new AreaResetterProExpansion();
            areaResetterProExpansion.register();
            if(((Boolean) configHandler.get("EnableAutoResets")) && autoResetHandler.isInitialized()) {
                AreaResetterPro.getPlugin(AreaResetterPro.class).getAreaResetterProExpansion().updateValues();
            }
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

    public AreaResetterProExpansion getAreaResetterProExpansion() {
        return this.areaResetterProExpansion;
    }

}
