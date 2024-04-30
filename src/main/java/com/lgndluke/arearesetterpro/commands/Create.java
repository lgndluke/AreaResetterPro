package com.lgndluke.arearesetterpro.commands;

import com.fastasyncworldedit.core.FaweAPI;
import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.*;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static com.lgndluke.arearesetterpro.data.PositionsHandler.Position.POS1;
import static com.lgndluke.arearesetterpro.data.PositionsHandler.Position.POS2;

/**
 * This Class handles the 'arp_create' command.
 * @author lgndluke
 **/
public class Create implements CommandExecutor {

    //Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static final Component prefix = MessageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = MessageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = MessageHandler.getMessageAsString("ExecutedByConsole");

    //CommandExecutor
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("arearesetterpro.create")) {
                if(args.length == 1) {
                    areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new CreateThread(((Player) sender).getPlayer(), args[0]));
                    return true;
                } else {
                    return false;
                }
            } else {
                sender.sendMessage(prefix.append(noPermission));
                return true;
            }
        } else {
            areaPlugin.getLogger().log(Level.SEVERE, executedByConsole);
            return true;
        }

    }

    /**
     * This Class handles the area-creation process.
     * @author lgndluke
     **/
    private static class CreateThread implements Runnable {

        //Attributes
        private final Component success = MessageHandler.getMessageAsComponent("SuccessfullySavedArea");
        private final Component failed = MessageHandler.getMessageAsComponent("CreationFailedMessage");
        private final Component noPos1 = MessageHandler.getMessageAsComponent("Pos1NotSet");
        private final Component noPos2 = MessageHandler.getMessageAsComponent("Pos2NotSet");
        private final Component areaAlreadyExists = MessageHandler.getMessageAsComponent("AreaAlreadyExists");
        private final Component creationStarted = MessageHandler.getMessageAsComponent("AreaCreationStartedMessage");
        private final Component creationFinished = MessageHandler.getMessageAsComponent("CreationSucceededMessage");
        private final Player player;
        private final String areaName;

        //Constructor
        private CreateThread(Player player, String areaName) {
            this.player = player;
            this.areaName = areaName;
        }

        //Runnable
        @Override
        public void run() {

            Location[] positions;

            try {
                positions = getLocations();
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }

            if(!validAreaName()) {
                return;
            }

            try {

                player.sendMessage(prefix.append(creationStarted));

                //TODO Rework Creation process from here.

                UUID struct_uuid = UUID.randomUUID();
                String worldName = positions[0].getWorld().getName();

                File dataFolder = new File(areaPlugin.getDataFolder().getAbsolutePath(), "AreaData");
                if(!dataFolder.exists()) {
                    boolean folderCreated = dataFolder.mkdir();
                    if(folderCreated) {
                        areaPlugin.getLogger().log(Level.INFO, "Successfully created 'AreaData' folder inside 'AreaResetterPro' folder.");
                    }
                }

                //Save position of area to database. Using pos1.
                int xSaveVal, ySaveVal, zSaveVal;
                int x1 = positions[0].getBlockX(); int y1 = positions[0].getBlockY(); int z1 = positions[0].getBlockZ();
                int x2 = positions[1].getBlockX(); int y2 = positions[1].getBlockY(); int z2 = positions[1].getBlockZ();

                //Determine ySaveVal.
                ySaveVal = Math.min(y1, y2);

                //Determine xSaveVal and zSaveVal.
                if(x1>x2 && z1<z2) {
                    xSaveVal = x2;
                    zSaveVal = z1;
                } else if (x1<x2 && z1<z2) {
                    xSaveVal = x1;
                    zSaveVal = z1;
                } else if (x1>x2 && z1>z2) {
                    xSaveVal = x2;
                    zSaveVal = z2;
                } else {
                    xSaveVal = x1;
                    zSaveVal = z2;
                }

                String sqlAreaData = "INSERT INTO AreaData (uuid, areaName, world, x, y, z) VALUES ('" + struct_uuid + "', '" + areaName + "', '" + worldName + "', " + xSaveVal + ", " + ySaveVal + ", " + zSaveVal + ")";
                DatabaseHandler.execute(sqlAreaData);

                //Save the structure to Area folder.
                File data = new File(dataFolder.getAbsolutePath(), struct_uuid + ".schem");

                //Save structure via FastAsyncWorldEdit.
                Region region = new CuboidRegion(BlockVector3.at(positions[0].getBlockX(), positions[0].getBlockY(), positions[0].getBlockZ()), BlockVector3.at(positions[1].getBlockX(), positions[1].getBlockY(), positions[1].getBlockZ()));
                region.setWorld(FaweAPI.getWorld(worldName));
                Clipboard clip = new BlockArrayClipboard(region);
                EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(worldName));
                ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clip, region.getMinimumPoint());
                forwardExtentCopy.setCopyingEntities(false);
                Operations.complete(forwardExtentCopy);
                editSession.close();
                clip.save(data, BuiltInClipboardFormat.FAST);
                String createdOn = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
                long overallBlocks = (long) clip.getHeight() * clip.getWidth() * clip.getLength();
                clip.close();

                String sqlAreaStats = "INSERT INTO AreaStats (uuid, timesReset, overallBlocks, createdOn) VALUES ('" + struct_uuid + "', 0, " + overallBlocks + ", '" + createdOn + "');";
                DatabaseHandler.execute(sqlAreaStats);

                int configTimerVal = (int) ConfigHandler.get("DefaultTimerValue");
                String sqlAreaTimer = "INSERT INTO AreaTimer (uuid, timerValue) VALUES ('" + struct_uuid + "', " + configTimerVal  + ");";
                DatabaseHandler.execute(sqlAreaTimer);

                AutoResetHandler.addNewAutoResetter(areaName, configTimerVal);

            } catch (IOException io) {
                player.sendMessage(prefix.append(failed));
                areaPlugin.getLogger().log(Level.SEVERE, "An I/O Error occurred during creation process.", io);
            }

            player.sendMessage(prefix.append(success));
            areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new Tool.SavePosThread(null, null, null));
            player.sendMessage(prefix.append(creationFinished));

        }


        private Location[] getLocations() throws ArrayIndexOutOfBoundsException {

            Location[] positions = new Location[] { PositionsHandler.getPosition(POS1), PositionsHandler.getPosition(POS2) };

            if(positions[0] == null) {
                player.sendMessage(prefix.append(noPos1));
                throw new ArrayIndexOutOfBoundsException("ERROR: Position 1 is invalid!");
            } else if (positions[1] == null) {
                player.sendMessage(prefix.append(noPos2));
                throw new ArrayIndexOutOfBoundsException("ERROR: Position 2 is invalid!");
            }

            return positions;

        }
        private boolean validAreaName() {

            //TODO -> Rework

            String sql = "SELECT areaName FROM AreaData;";
            List<String> results = DatabaseHandler.executeQuery(sql);

            if(results.contains(areaName)) {
                player.sendMessage(prefix.append(areaAlreadyExists));
                return false;
            }

            return true;

        }


    }

}
