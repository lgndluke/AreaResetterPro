package com.lgndluke.arearesetterpro.data;

import com.fastasyncworldedit.core.FaweAPI;
import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.placeholders.AreaResetterProExpansion;
import com.lgndluke.lgndware.data.AbstractHandler;
import com.lgndluke.lgndware.data.MessageHandler;
import com.sk89q.worldedit.math.BlockVector3;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * This Class handles automatic resets.
 * @author lgndluke
 **/
public class AutoResetHandler extends AbstractHandler {

    private final Plugin areaPlugin = super.getPlugin();
    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
    private static final List<AutoResetter> autoResetterList = new ArrayList<>();

    public AutoResetHandler(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean initialize() {
        FutureTask<Boolean> initAutoResetHandler = new FutureTask<>(() -> {
            //Start auto-resetter for every area.
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
            } catch(NumberFormatException nfe) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Failed to enable auto-resetter", nfe);
                return false;
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                return false;
            }
            return true;
        });
        return super.getAsyncExecutor().executeFuture(super.getPlugin().getLogger(), initAutoResetHandler, 10, TimeUnit.SECONDS);
    }

    @Override
    public boolean terminate() {
        if(!super.getAsyncExecutor().isShutdown()) {
            super.getAsyncExecutor().shutdown();
            return true;
        }
        return false;
    }

    public void addNewAutoResetter(String areaName, long resetInterval) {
        autoResetterList.add(new AutoResetter(areaName, resetInterval*20));
    }

    public void updateAreaResetInterval(String areaName, long resetInterval) {
        for(AutoResetter autoResetter : autoResetterList) {
            if(autoResetter.getAreaName().equals(areaName)) {
                autoResetter.setResetInterval(resetInterval*20);
            }
        }
    }

    public void removeAreaResetter(String areaName) {
        for(AutoResetter autoResetter : autoResetterList) {
            if(autoResetter.getAreaName().equals(areaName)) {
                autoResetter = null;
                autoResetterList.remove(autoResetter);
            }
        }
    }

    public long getTimeRemaining(String areaName) {
        for(AutoResetter autoResetter : autoResetterList) {
            if(autoResetter.getAreaName().equals(areaName)) {
                return autoResetter.timeRemaining;
            }
        }
        return -1;
    }

    //Inner Classes
    private class AutoResetter implements Runnable {

        //Attributes
        private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
        private final Component resetMsgPlayer = messageHandler.getMessageAsComponent("ResetMessagePlayer");
        private final String areaName;
        private long resetInterval;
        private long timeRemaining;
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        //Constructor
        private AutoResetter(String areaName, Long resetInterval) {
            this.areaName = areaName;
            this.resetInterval = resetInterval;
            areaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(areaPlugin, this, this.resetInterval);
            areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, () -> countDown((resetInterval/20)));
        }

        //Runnable
        @Override
        public void run() { //TODO RE_WRITE PROCESS! -> Only resets/reloads timer on autoreset -> not on instant reset -> annoying!

            try {

                ResultSet results = databaseHandler.getAreaData();

                while(results.next()) {

                    if(results.getString("areaName").equals(areaName)) {

                        UUID uuid = UUID.fromString(results.getString("uuid"));
                        String worldName = results.getString("world");

                        int xVal1 = results.getInt("xValPos1");
                        int yVal1 = results.getInt("yValPos1");
                        int zVal1 = results.getInt("zValPos1");

                        int xVal2 = results.getInt("xValPos2");
                        int yVal2 = results.getInt("yValPos2");
                        int zVal2 = results.getInt("zValPos2");

                        int xValSpawn = results.getInt("xValSpawn");
                        int yValSpawn = results.getInt("yValSpawn");
                        int zValSpawn = results.getInt("zValSpawn");

                        results.close();

                        ResultSet areaStats = databaseHandler.getAreaStats(uuid);
                        int timesReset = areaStats.getInt("timesReset");
                        areaStats.close();

                        String filePath = "AreaData/" + uuid + ".schem";
                        File worldData = new File(areaPlugin.getDataFolder().getAbsolutePath(), filePath);

                        if(xVal1 != xValSpawn && yVal1 != yValSpawn && zVal1 != zValSpawn) {
                            Bukkit.getScheduler().runTask(areaPlugin, () -> {
                                List<Player> activePlayers = (List<Player>) Bukkit.getServer().getOnlinePlayers();
                                for (Player player : activePlayers) {
                                    if (isInsideArea(player, new Location(WorldCreator.name(worldName).createWorld(), xVal1, yVal1, zVal1),
                                            new Location(WorldCreator.name(worldName).createWorld(), xVal2, yVal2, zVal2))) {
                                        player.sendMessage(prefix.append(resetMsgPlayer));
                                        player.teleportAsync(new Location(WorldCreator.name(worldName).createWorld(), xValSpawn, yValSpawn, zValSpawn));
                                    }
                                }
                            });
                        }

                        FaweAPI.load(worldData).paste(FaweAPI.getWorld(worldName), BlockVector3.at(Math.min(xVal1, xVal2), Math.min(yVal1, yVal2), Math.min(zVal1, zVal2)));
                        databaseHandler.updateAreaStatsTimesReset(uuid, timesReset);

                    }

                }

            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            } catch (IOException e) {
                areaPlugin.getLogger().log(Level.SEVERE, "AutoResetter: Could not reset area: " + areaName);
                areaPlugin.getLogger().log(Level.SEVERE, "AutoResetter: An Error occurred whilst trying to reset the area.", e);
            }

            //Resets the timer with a new value.
            areaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(areaPlugin, this, this.resetInterval);
            this.timeRemaining = this.resetInterval/20;
            AreaResetterProExpansion.updateValues();
            //-----------------------------------------------------------

        }

        //Methods
        private boolean isInsideArea(Player player, Location pos1, Location pos2) {

            double xValPlayer = player.getLocation().getBlockX();
            double yValPlayer = player.getLocation().getBlockY();
            double zValPlayer = player.getLocation().getBlockZ();

            int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
            int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
            int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
            int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
            int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
            int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

            return ((xValPlayer >= minX && xValPlayer <= maxX) &&
                    (yValPlayer >= minY && yValPlayer <= maxY) &&
                    (zValPlayer >= minZ && zValPlayer <= maxZ));

        }

        private void countDown(long timeRemaining) {
            this.timeRemaining = timeRemaining;
            scheduler.scheduleAtFixedRate(() -> {
                if(this.timeRemaining > 0) {
                    this.timeRemaining--;
                } else {
                    scheduler.shutdown();
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        private String getAreaName() {
            return this.areaName;
        }

        private void setResetInterval(long resetInterval) {
            this.resetInterval = resetInterval;
        }

    }

}
