package com.lgndluke.arearesetterpro.commands;

import com.fastasyncworldedit.core.FaweAPI;
import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.lgndware.data.MessageHandler;
import com.sk89q.worldedit.math.BlockVector3;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This Class handles the 'arp_reset' command.
 * @author lgndluke
 **/
public class Reset implements CommandExecutor { //TODO Completely rework this process!

    //Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = messageHandler.getMessageAsComponent("NoPermission");

    //CommandExecutor
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("arearesetterpro.reset")) {
                if(args.length == 1) {
                    areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new ResetArea(sender, args[0]));
                    return true;
                } else {
                    return false;
                }
            } else {
                sender.sendMessage(prefix.append(noPermission));
                return true;
            }
        } else {
            if(args.length == 1) {
                areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new ResetArea(sender, args[0]));
                return true;
            } else {
                return false;
            }
        }

    }

    /**
     * This Class is used to reset area-objects.
     * Can only be done synchronously using StructureManager.
     * @author lgndluke
     **/
    protected static class ResetArea implements Runnable {

        //Attributes
        private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
        private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
        private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
        private final Component success = messageHandler.getMessageAsComponent("AreaResetSuccessful");
        private final Component nonExist = messageHandler.getMessageAsComponent("AreaNonExistent");
        private final Component resetMsgPlayer = messageHandler.getMessageAsComponent("ResetMessagePlayer");
        private final CommandSender sender;
        private final String areaName;

        protected ResetArea(CommandSender sender, String areaName) {
            this.sender = sender;
            this.areaName = areaName;
        }

        //Methods
        @Override
        public void run() {
            //-----------------------------------------------------------
            //Define SQL-Statements and UUID as String.
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
                            List<Player> activePlayers = (List<Player>) Bukkit.getServer().getOnlinePlayers();
                            for (Player player : activePlayers) {
                                if (isInsideArea(player, new Location(WorldCreator.name(worldName).createWorld(), xVal1, yVal1, zVal1),
                                        new Location(WorldCreator.name(worldName).createWorld(), xVal2, yVal2, zVal2))) {
                                    player.sendMessage(prefix.append(this.resetMsgPlayer));
                                    player.teleportAsync(new Location(WorldCreator.name(worldName).createWorld(), xValSpawn, yValSpawn, zValSpawn));
                                }
                            }
                        }

                        FaweAPI.load(worldData).paste(FaweAPI.getWorld(worldName), BlockVector3.at(Math.min(xVal1, xVal2), Math.min(yVal1, yVal2), Math.min(zVal1, zVal2)));
                        databaseHandler.updateAreaStatsTimesReset(uuid, timesReset);

                        if(sender instanceof Player) {
                            sender.sendMessage(prefix.append(this.success));
                        } else {
                            String plainSuccess = PlainTextComponentSerializer.plainText().serialize(success);
                            areaPlugin.getLogger().log(Level.INFO, plainSuccess);
                        }

                        return;

                    }

                }

                results.close();
                if(sender instanceof Player) {
                    sender.sendMessage(prefix.append(this.nonExist));
                } else {
                    String plainNonExist = PlainTextComponentSerializer.plainText().serialize(nonExist);
                    areaPlugin.getLogger().log(Level.INFO, plainNonExist);
                }

            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            } catch (IOException e) {
                areaPlugin.getLogger().log(Level.SEVERE, "Could not reset area: " + areaName);
                if(sender instanceof Player) {
                    sender.sendMessage(prefix.append(Component.text("Couldn't reset area. Check console for more information!")));
                }
                areaPlugin.getLogger().log(Level.SEVERE, "An Error occurred whilst trying to reset the area.", e);
            }
            //-----------------------------------------------------------
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

    }

    protected static class ResetAllAreas implements Runnable {

        private final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
        private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
        private final CommandSender sender;

        //Constructor
        protected ResetAllAreas(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public void run() {
            try {
                ResultSet results = databaseHandler.getAreaData();
                while (results.next()) {
                    areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new ResetArea(sender, results.getString("areaName")));
                }
                results.close();
            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            }
        }

    }

}
