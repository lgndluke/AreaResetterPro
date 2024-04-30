package com.lgndluke.arearesetterpro.commands;

import com.fastasyncworldedit.core.FaweAPI;
import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.arearesetterpro.data.MessageHandler;
import com.sk89q.worldedit.math.BlockVector3;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

/**
 * This Class handles the 'arp_reset' command.
 * @author lgndluke
 **/
public class Reset implements CommandExecutor {

    //Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static final Component prefix = MessageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = MessageHandler.getMessageAsComponent("NoPermission");

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
        private final Component success = MessageHandler.getMessageAsComponent("AreaResetSuccessful");
        private final Component nonExist = MessageHandler.getMessageAsComponent("AreaNonExistent");
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
            String sqlAreaCheck = "SELECT areaName FROM AreaData;";
            String sqlUuid = "SELECT uuid FROM AreaData WHERE areaName = '" + areaName + "';";
            String sqlWorld = "SELECT world FROM AreaData WHERE areaName = '" + areaName + "';";

            List<String> resultNames = DatabaseHandler.executeQuery(sqlAreaCheck);
            if(resultNames.contains(areaName)) {

                //Get Data from Database.
                String uuid = DatabaseHandler.executeQuery(sqlUuid).get(0);
                String worldName = DatabaseHandler.executeQuery(sqlWorld).get(0);

                String sqlCoordX = "SELECT x FROM AreaData WHERE uuid = '" + uuid + "'";
                String sqlCoordY = "SELECT y FROM AreaData WHERE uuid = '" + uuid + "'";
                String sqlCoordZ = "SELECT z FROM AreaData WHERE uuid = '" + uuid + "'";
                String sqlTimesReset = "SELECT timesReset FROM AreaStats WHERE uuid = '" + uuid + "'";

                int xCoord = Integer.parseInt(DatabaseHandler.executeQuery(sqlCoordX).get(0));
                int yCoord = Integer.parseInt(DatabaseHandler.executeQuery(sqlCoordY).get(0));
                int zCoord = Integer.parseInt(DatabaseHandler.executeQuery(sqlCoordZ).get(0));
                int timesReset = Integer.parseInt(DatabaseHandler.executeQuery(sqlTimesReset).get(0));

                //Set file-path and get StructureManager.
                String filePath = "AreaData/" + uuid + ".schem";
                File worldData = new File(areaPlugin.getDataFolder().getAbsolutePath(), filePath);

                try {
                    FaweAPI.load(worldData).paste(FaweAPI.getWorld(worldName), BlockVector3.at(xCoord, yCoord, zCoord)).close();
                    //Update reset statistics.
                    String sqlUpdateResetCounter = "UPDATE AreaStats SET timesReset = " + Math.addExact(timesReset, 1) + " WHERE uuid = '" + uuid + "';";
                    DatabaseHandler.execute(sqlUpdateResetCounter);
                } catch (Exception e) {
                    areaPlugin.getLogger().log(Level.SEVERE, "Could not reset area: " + areaName);
                    if(sender instanceof Player) {
                        sender.sendMessage(prefix.append(Component.text("Couldn't reset area. Check console for more information!")));
                    }
                    areaPlugin.getLogger().log(Level.SEVERE, "An Error occurred whilst trying to reset the area.", e);
                    return;
                }

                if(sender instanceof Player) {
                    sender.sendMessage(prefix.append(this.success));
                } else {
                    String plainSuccess = PlainTextComponentSerializer.plainText().serialize(success);
                    areaPlugin.getLogger().log(Level.INFO, plainSuccess);
                }

            } else {

                if(sender instanceof Player) {
                    sender.sendMessage(prefix.append(this.nonExist));
                } else {
                    String plainNonExist = PlainTextComponentSerializer.plainText().serialize(nonExist);
                    areaPlugin.getLogger().log(Level.INFO, plainNonExist);
                }

            }
            //-----------------------------------------------------------
        }

    }

    protected static class ResetAllAreas implements Runnable {

        //Attributes
        private final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
        private final CommandSender sender;

        //Constructor
        protected ResetAllAreas(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public void run() {
            String sqlAreaCheck = "SELECT areaName FROM AreaData;";
            List<String> resultNames = DatabaseHandler.executeQuery(sqlAreaCheck);
            for (String resultName : resultNames) {
                areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new ResetArea(sender, resultName));
            }
        }

    }

}
