package com.lgndluke.arearesetterpro.tasks;

import com.fastasyncworldedit.core.FaweAPI;
import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.commands.ResetCmd;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;

/**
 * This process asynchronously executes an area reset.
 * @author lgndluke
 **/
public class ResetTask extends ResetCmd {

    private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
    private final Component success = messageHandler.getMessageAsComponent("AreaResetSuccessful");
    private final Component nonExist = messageHandler.getMessageAsComponent("AreaNonExistent");
    private final Component resetMsgPlayer = messageHandler.getMessageAsComponent("ResetMessagePlayer");
    private final CommandSender sender;
    private final String areaName;

    public ResetTask(CommandSender sender, String areaName) {
        this.sender = sender;
        this.areaName = areaName;
    }

    public RunnableFuture<Boolean> execute() {
        return new FutureTask<>(() -> {
            try {
                UUID uuid = getUUID();
                if (uuid == null) {
                    if (sender instanceof Player) {
                        sender.sendMessage(prefix.append(this.nonExist));
                    } else {
                        String plainNonExist = PlainTextComponentSerializer.plainText().serialize(nonExist);
                        super.getPlugin().getLogger().log(Level.INFO, plainNonExist);
                    }
                    return false;
                }
                String worldName = databaseHandler.getAreaData(areaName).getString("world");
                List<int[]> positions = getPositions();
                File worldData = getSchematics(uuid);
                teleportPlayers(positions, worldName);
                pasteSchematics(worldData, worldName, positions);
                updateAreaStats(uuid);
                if (sender instanceof Player) {
                    sender.sendMessage(prefix.append(this.success));
                } else {
                    String plainSuccess = PlainTextComponentSerializer.plainText().serialize(success);
                    super.getPlugin().getLogger().log(Level.INFO, plainSuccess);
                }
                return true;
            } catch (IOException io) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Resetting the Area failed!", io);
                return false;
            }
        });
    }

    private UUID getUUID() {
        try {
            String uuidAsString = databaseHandler.getAreaData(areaName).getString("uuid");
            if(uuidAsString == null) {
                sender.sendMessage(prefix.append(nonExist));
                return null;
            }
            return UUID.fromString(uuidAsString);
        } catch (SQLException se) {
            return null;
        }
    }

    private List<int[]> getPositions() {
        try {
            ArrayList<int[]> posList = new ArrayList<>();
            int[] pos1 = new int[] { databaseHandler.getAreaData(areaName).getInt("xValPos1"),
                                     databaseHandler.getAreaData(areaName).getInt("yValPos1"),
                                     databaseHandler.getAreaData(areaName).getInt("zValPos1") };

            int[] pos2 = new int[] { databaseHandler.getAreaData(areaName).getInt("xValPos2"),
                                     databaseHandler.getAreaData(areaName).getInt("yValPos2"),
                                     databaseHandler.getAreaData(areaName).getInt("zValPos2") };

            int[] spawn = new int[] { databaseHandler.getAreaData(areaName).getInt("xValSpawn"),
                                      databaseHandler.getAreaData(areaName).getInt("yValSpawn"),
                                      databaseHandler.getAreaData(areaName).getInt("zValSpawn") };
            posList.add(pos1);
            posList.add(pos2);
            posList.add(spawn);
            return posList;
        } catch (SQLException se) {
            return null;
        }
    }

    private File getSchematics(UUID uuid) {
        return new File(super.getPlugin().getDataFolder().getAbsolutePath(), "/AreaData/" + uuid + ".schem");
    }

    private void teleportPlayers(List<int[]> positions, String worldName) {
        if(!(positions.get(0)[0] != positions.get(2)[0] && positions.get(0)[1] != positions.get(2)[1] && positions.get(0)[2] != positions.get(2)[2])) {
            return;
        }
        Location pos1 = new Location(WorldCreator.name(worldName).createWorld(), positions.get(0)[0], positions.get(0)[1], positions.get(0)[2]);
        Location pos2 = new Location(WorldCreator.name(worldName).createWorld(), positions.get(1)[0], positions.get(1)[1], positions.get(1)[2]);
        Location spawn = new Location(WorldCreator.name(worldName).createWorld(), positions.get(2)[0], positions.get(2)[1], positions.get(2)[2]);
        for(Player player : super.getPlugin().getServer().getOnlinePlayers()) {
            if(isInsideArea(player, pos1, pos2)) {
                player.sendMessage(prefix.append(resetMsgPlayer));
                player.teleportAsync(spawn);
            }
        }
    }

    private void pasteSchematics(File worldData, String worldName, List<int[]> positions) throws IOException {
        ClipboardFormat format = ClipboardFormats.findByFile(worldData);
        Clipboard clip;
        try(FileInputStream fis = new FileInputStream(worldData);
            ClipboardReader reader = format.getReader(fis)) {
            clip = reader.read();
        }
        if(clip == null) {
            throw new IOException("Failed to load Schematics from disk!");
        }
        try(EditSession edit = WorldEdit.getInstance().newEditSession(FaweAPI.getWorld(worldName))) {
            Operation op = new ClipboardHolder(clip).createPaste(edit).to(BlockVector3.at(
                    Math.min(positions.get(0)[0], positions.get(1)[0]),
                    Math.min(positions.get(0)[1], positions.get(1)[1]),
                    Math.min(positions.get(0)[2], positions.get(1)[2]))).build();
            Operations.complete(op);
        }
    }

    private void updateAreaStats(UUID uuid) {
        try {
            int timesReset = databaseHandler.getAreaStats(uuid).getInt("timesReset");
            databaseHandler.updateAreaStatsTimesReset(uuid, timesReset);
        } catch (SQLException se) {
            super.getPlugin().getLogger().log(Level.SEVERE, "Updating AreaStats failed! Couldn't fetch AreaStats!", se);
        }
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
