package com.lgndluke.arearesetterpro.data;

import com.fastasyncworldedit.core.FaweAPI;
import com.lgndluke.arearesetterpro.AreaResetterPro;
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
import java.util.concurrent.*;
import java.util.logging.Level;

public class AutoResetHandler extends AbstractHandler {

    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
    private List<AutoResetter> autoResetterList = new ArrayList<>();

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
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), initAutoResetHandler, 10, TimeUnit.SECONDS);
    }

    @Override
    public boolean terminate() {
        if(!super.getDefaultAsyncExecutor().isShutdown()) {
            super.getDefaultAsyncExecutor().shutdown();
            return true;
        }
        return false;
    }

    public void addNewAutoResetter(String areaName, long resetInterval) {
        autoResetterList.add(new AutoResetter(super.getPlugin(), areaName, resetInterval*20));
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

    private class AutoResetter {

        private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
        private final Component resetMsgPlayer = messageHandler.getMessageAsComponent("ResetMessagePlayer");
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
            return new FutureTask<Boolean>(() -> {
                try {
                    ResultSet results = databaseHandler.getAreaData();
                    while(results.next()) {
                        if(results.getString("areaName").equals(areaName)) {
                            UUID uuid = UUID.fromString(results.getString("uuid"));
                            String worldName = results.getString("world");

                            int[] pos1 = new int[] { results.getInt("xValPos1"),
                                                     results.getInt("yValPos1"),
                                                     results.getInt("zValPos1")};

                            int[] pos2 = new int[] { results.getInt("xValPos2"),
                                                     results.getInt("yValPos2"),
                                                     results.getInt("zValPos2")};

                            int[] spawn = new int[] { results.getInt("xValSpawn"),
                                                      results.getInt("yValSpawn"),
                                                      results.getInt("zValSpawn")};

                            ResultSet areaStats = databaseHandler.getAreaStats(uuid);
                            int timesReset = areaStats.getInt("timesReset");
                            areaStats.close();

                            String filePath = "AreaData/" + uuid + ".schem";
                            File worldData = new File(plugin.getDataFolder().getAbsolutePath(), filePath);

                            if(pos1[0] != spawn[0] && pos1[1] != spawn[1] && pos1[2] != spawn[2]) {
                                Bukkit.getScheduler().runTask(plugin, () -> {
                                    List<Player> activePlayers = (List<Player>) Bukkit.getServer().getOnlinePlayers();
                                    for (Player player : activePlayers) {
                                        if (isInsideArea(player, new Location(WorldCreator.name(worldName).createWorld(), pos1[0], pos1[1], pos1[2]),
                                                new Location(WorldCreator.name(worldName).createWorld(), pos2[0], pos2[1], pos2[2]))) {
                                            player.sendMessage(prefix.append(resetMsgPlayer));
                                            player.teleportAsync(new Location(WorldCreator.name(worldName).createWorld(), spawn[0], spawn[1], spawn[2]));
                                        }
                                    }
                                });
                            }
                            FaweAPI.load(worldData).paste(FaweAPI.getWorld(worldName), BlockVector3.at(Math.min(pos1[0], pos2[0]), Math.min(pos1[1], pos2[1]), Math.min(pos1[2], pos2[2])));
                            databaseHandler.updateAreaStatsTimesReset(uuid, timesReset);

                            plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, execute(), resetInterval);
                            this.timeRemaining = this.resetInterval/20;
                            AreaResetterPro.getPlugin(AreaResetterPro.class).getAreaResetterProExpansion().updateValues();
                        }
                    }
                    results.close();
                } catch (SQLException se) {
                    plugin.getLogger().log(Level.SEVERE, "AutoResetter: Couldn't fetch AreaData!", se);
                } catch (IOException io) {
                    plugin.getLogger().log(Level.SEVERE, "AutoResetter: Could not reset area: " + areaName);
                    plugin.getLogger().log(Level.SEVERE, "AutoResetter: An error occurred whilst trying to reset the area.", io);
                }
                return true;
            });
        }

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

        private void countDown() {
            scheduler.scheduleAtFixedRate(() -> {
                if(this.timeRemaining > 0) {
                    this.timeRemaining--;
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
