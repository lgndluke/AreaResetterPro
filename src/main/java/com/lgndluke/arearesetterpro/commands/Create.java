package com.lgndluke.arearesetterpro.commands;

import com.fastasyncworldedit.core.FaweAPI;
import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.*;
import com.lgndluke.arearesetterpro.placeholders.AreaResetterProExpansion;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static com.lgndluke.arearesetterpro.data.PositionsHandler.Position.POS1;
import static com.lgndluke.arearesetterpro.data.PositionsHandler.Position.POS2;

/**
 * This Class handles the 'arp_create' command.
 * @author lgndluke
 **/
public class Create implements CommandExecutor {

    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static final Component prefix = MessageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = MessageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = MessageHandler.getMessageAsString("ExecutedByConsole");

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

        private final Component success = MessageHandler.getMessageAsComponent("SuccessfullySavedArea");
        private final Component failed = MessageHandler.getMessageAsComponent("CreationFailedMessage");
        private final Component noPos1 = MessageHandler.getMessageAsComponent("Pos1NotSet");
        private final Component noPos2 = MessageHandler.getMessageAsComponent("Pos2NotSet");
        private final Component noSpawn = MessageHandler.getMessageAsComponent("SpawnPointNotSet");
        private final Component areaAlreadyExists = MessageHandler.getMessageAsComponent("AreaAlreadyExists");
        private final Component creationStarted = MessageHandler.getMessageAsComponent("AreaCreationStartedMessage");
        private final Component creationFinished = MessageHandler.getMessageAsComponent("CreationSucceededMessage");
        private final Player player;
        private final String areaName;

        private CreateThread(Player player, String areaName) {
            this.player = player;
            this.areaName = areaName;
        }

        @Override
        public void run() {

            try {

                //Start of validation process.
                //----------------------------------------------------------------------------------------------------

                Location[] positions = getLocations();
                Location spawnpoint = getSpawnpoint();

                if(positions == null || spawnpoint == null || !validAreaName()) {
                    return;
                }

                //Start of area creation process.
                //----------------------------------------------------------------------------------------------------

                player.sendMessage(prefix.append(creationStarted));

                createAreaDataFolder();

                UUID uuid = UUID.randomUUID();
                DatabaseHandler.insertAreaData(uuid, areaName, positions[0].getWorld().getName(), positions[0], positions[1], spawnpoint);
                saveAreaDataSchematics(uuid, positions);
                DatabaseHandler.insertAreaStats(uuid, ((long) (Math.abs(positions[0].getBlockX() - positions[1].getBlockX()) + 1) *
                                                              (Math.abs(positions[0].getBlockY() - positions[1].getBlockY()) + 1) *
                                                              (Math.abs(positions[0].getBlockZ() - positions[1].getBlockZ()) + 1)));

                int configTimerValue = (int) ConfigHandler.get("DefaultTimerValue");
                DatabaseHandler.insertAreaTimer(uuid, configTimerValue);
                AutoResetHandler.addNewAutoResetter(areaName, configTimerValue);
                AreaResetterProExpansion.updateValues();

                player.sendMessage(prefix.append(success));
                areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new Tool.SavePosThread(null, null, null));
                areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new SetSpawnPoint.SaveSpawnThread(null, null));
                player.sendMessage(prefix.append(creationFinished));

            } catch (IOException io) {

                player.sendMessage(prefix.append(failed));
                areaPlugin.getLogger().log(Level.SEVERE, "An I/O Error occurred during creation process.", io);

            }

        }

        private Location[] getLocations() {

            Location[] positions = new Location[] { PositionsHandler.getPosition(POS1), PositionsHandler.getPosition(POS2) };

            if(positions[0] == null) {
                player.sendMessage(prefix.append(noPos1));
                return null;
            } else if (positions[1] == null) {
                player.sendMessage(prefix.append(noPos2));
                return null;
            }

            return positions;

        }

        private Location getSpawnpoint() {

            Location spawnpoint = SpawnPointHandler.getSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT);

            if(spawnpoint == null) {
                player.sendMessage(prefix.append(noSpawn));
                return null;
            }

            return spawnpoint;

        }

        private boolean validAreaName() {

            try {
                ResultSet areaData = DatabaseHandler.getAreaData();
                while(areaData.next()) {
                    if(Objects.equals(areaData.getString("areaName"), areaName)) {
                        player.sendMessage(prefix.append(areaAlreadyExists));
                        return false;
                    }
                }
                areaData.close();
                return true;
            } catch (SQLException se) {
                return false;
            }

        }

        private void createAreaDataFolder() {

            File dataFolder = new File(areaPlugin.getDataFolder().getAbsolutePath(), "AreaData");
            if(!dataFolder.exists()) {
                boolean folderCreated = dataFolder.mkdir();
                if(folderCreated) {
                    areaPlugin.getLogger().log(Level.INFO, "Successfully created 'AreaData' folder inside 'AreaResetterPro' folder.");
                }
            }

        }

        private void saveAreaDataSchematics(UUID uuid, Location[] positions) throws IOException {

            //Generate schematic file inside the AreaData folder.
            File dataFolder = new File(areaPlugin.getDataFolder().getAbsolutePath(), "AreaData");
            File data = new File(dataFolder.getAbsolutePath(), uuid + ".schem");

            //Save structure via FastAsyncWorldEdit.
            Region region = new CuboidRegion(BlockVector3.at(positions[0].getBlockX(), positions[0].getBlockY(), positions[0].getBlockZ()),
                                             BlockVector3.at(positions[1].getBlockX(), positions[1].getBlockY(), positions[1].getBlockZ()));

            region.setWorld(FaweAPI.getWorld(positions[0].getWorld().getName()));
            Clipboard clip = new BlockArrayClipboard(region);
            EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(positions[0].getWorld().getName()));
            ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(editSession, region, clip, region.getMinimumPoint());
            forwardExtentCopy.setCopyingEntities(false);
            Operations.complete(forwardExtentCopy);
            editSession.close();
            clip.save(data, BuiltInClipboardFormat.FAST);
            clip.close();

        }

    }

}
