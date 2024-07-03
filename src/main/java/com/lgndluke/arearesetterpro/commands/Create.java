package com.lgndluke.arearesetterpro.commands;

import com.fastasyncworldedit.core.FaweAPI;
import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.AutoResetHandler;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.arearesetterpro.data.PositionsHandler;
import com.lgndluke.arearesetterpro.data.SpawnPointHandler;
import com.lgndluke.lgndware.data.ConfigHandler;
import com.lgndluke.lgndware.data.MessageHandler;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
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
public class Create implements CommandExecutor { //TODO Completely rework this process!

    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = messageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = messageHandler.getMessageAsString("ExecutedByConsole");

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
    private class CreateThread implements Runnable {

        private final PositionsHandler positionsHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getPositionsHandler();
        private final SpawnPointHandler spawnPointHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getSpawnPointHandler();
        private final ConfigHandler configHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getConfigHandler();
        private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
        private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
        private final AutoResetHandler autoResetHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getAutoResetHandler();
        private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
        private final Component success = messageHandler.getMessageAsComponent("SuccessfullySavedArea");
        private final Component failed = messageHandler.getMessageAsComponent("CreationFailedMessage");
        private final Component noPos1 = messageHandler.getMessageAsComponent("Pos1NotSet");
        private final Component noPos2 = messageHandler.getMessageAsComponent("Pos2NotSet");
        private final Component areaAlreadyExists = messageHandler.getMessageAsComponent("AreaAlreadyExists");
        private final Component creationStarted = messageHandler.getMessageAsComponent("AreaCreationStartedMessage");
        private final Component creationFinished = messageHandler.getMessageAsComponent("CreationSucceededMessage");
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
                Location spawnPoint = getSpawnpoint();

                if(positions == null || !validAreaName()) {
                    return;
                }

                //Start of area creation process.
                //----------------------------------------------------------------------------------------------------

                player.sendMessage(prefix.append(creationStarted));

                createAreaDataFolder();

                UUID uuid = UUID.randomUUID();

                if(spawnPoint != null) {
                    databaseHandler.insertAreaData(uuid, areaName, positions[0].getWorld().getName(), positions[0], positions[1], spawnPoint);
                } else {
                    databaseHandler.insertAreaData(uuid, areaName, positions[0].getWorld().getName(), positions[0], positions[1], positions[0]);
                }

                saveAreaDataSchematics(uuid, positions);
                databaseHandler.insertAreaStats(uuid, ((long) (Math.abs(positions[0].getBlockX() - positions[1].getBlockX()) + 1) *
                                                              (Math.abs(positions[0].getBlockY() - positions[1].getBlockY()) + 1) *
                                                              (Math.abs(positions[0].getBlockZ() - positions[1].getBlockZ()) + 1)));

                int configTimerValue = (int) configHandler.get("DefaultTimerValue");
                databaseHandler.insertAreaTimer(uuid, configTimerValue);
                autoResetHandler.addNewAutoResetter(areaName, configTimerValue);

                AreaResetterPro.getPlugin(AreaResetterPro.class).getAreaResetterProExpansion().updateValues();

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

            Location[] positions = new Location[] { positionsHandler.getPosition(POS1), positionsHandler.getPosition(POS2) };

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
            return spawnPointHandler.getSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT);
        }

        private boolean validAreaName() {

            try {
                ResultSet areaData = databaseHandler.getAreaData();
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

            EditSession editSession = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(positions[0].getWorld().getName()));
            editSession.lazyCopy(region).save(data, BuiltInClipboardFormat.FAST);
            editSession.close();
        }

    }

}
