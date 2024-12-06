package com.lgndluke.arearesetterpro.autoresets;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.arearesetterpro.tasks.ResetTask;
import com.lgndluke.lgndware.data.AbstractHandler;
import com.lgndluke.lgndware.data.ConfigHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * This Class handles automatic area resets.
 * @author lgndluke
 **/
public class AutoResetHandler extends AbstractHandler {

    private final ConfigHandler configHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getConfigHandler();
    private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
    private Map<String, AutoResetter> autoResetterMap = new HashMap<>();
    private boolean isInitialized = false;

    public AutoResetHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean initialize() {
        FutureTask<Boolean> initAutoResetHandler = new FutureTask<>(() -> {
            if(!((Boolean) configHandler.get("EnableAutoResets"))) {
                isInitialized = false;
                return true;
            }
            ResultSet areaData = databaseHandler.getAreaData();
            ResultSet areaTimer = null;
            try {
                if(areaData != null) {
                    while (areaData.next()) {
                        areaTimer = databaseHandler.getAreaTimer(UUID.fromString(areaData.getString("uuid")));
                        addNewAutoResetter(areaData.getString("areaName"),
                                           areaTimer.getLong("timerValue"));
                    }
                    areaData.close();
                }
                if(areaTimer != null) {
                    areaTimer.close();
                }
                isInitialized = true;
            } catch(NumberFormatException nfe) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Failed to enable auto-resetter", nfe);
                return false;
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                return false;
            }
            return true;
        });
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), initAutoResetHandler, 10, TimeUnit.SECONDS);
    }

    @Override
    public boolean terminate() {
        super.getDefaultAsyncExecutor().shutdown();
        super.getScheduledAsyncExecutor().shutdown();
        return true;
    }

    public void addNewAutoResetter(String areaName, long resetInterval) {
        autoResetterMap.put(areaName, new AutoResetter(super.getPlugin(), areaName, resetInterval*20));
    }

    public void updateAreaResetInterval(String areaName, long resetInterval) {
        autoResetterMap.get(areaName).setResetInterval(resetInterval*20);
    }

    public void removeAreaResetter(String areaName) {
        autoResetterMap.remove(areaName);
    }

    public long getTimeRemaining(String areaName) {
        return autoResetterMap.get(areaName).timeRemaining;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    private class AutoResetter {

        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        private final Plugin plugin;
        private final String areaName;
        private long resetInterval;
        private long timeRemaining;

        private AutoResetter(Plugin plugin, String areaName, Long resetInterval) {
            this.plugin = plugin;
            this.areaName = areaName;
            this.resetInterval = resetInterval;
            this.timeRemaining = resetInterval/20;
            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, execute(), resetInterval);
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, this::countDown);
        }

        private FutureTask<Boolean> execute() {
            return new FutureTask<>(() -> {
                plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new ResetTask(null, areaName).execute());
                plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, execute(), resetInterval);
                timeRemaining = resetInterval/20;
                AreaResetterPro.getPlugin(AreaResetterPro.class).getAreaResetterProExpansion().updateValues();
                return true;
            });
        }

        private void countDown() {
            scheduler.scheduleAtFixedRate(() -> {
                if(timeRemaining > 0) {
                    timeRemaining--;
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        private void setResetInterval(long resetInterval) {
            this.resetInterval = resetInterval;
        }

    }

}
