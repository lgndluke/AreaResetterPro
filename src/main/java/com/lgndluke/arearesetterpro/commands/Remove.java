package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.AutoResetHandler;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.arearesetterpro.placeholders.AreaResetterProExpansion;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This Class handles the 'arp_remove' command.
 * @author lgndluke
 **/
//Maybe implement console removal? -> on request maybe?
public class Remove implements CommandExecutor {

    //Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = messageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = messageHandler.getMessageAsString("ExecutedByConsole");

    //CommandExecutor
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("arearesetterpro.remove")) {
                if(args.length ==  1) {
                    areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new Remover(((Player) sender).getPlayer(), args[0]));
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
     * This Class is a Runnable, which asynchronously removes all data from an Area object.
     * @author lgndluke
     **/
    protected static class Remover implements Runnable {

        //Attributes
        private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
        private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
        private final AutoResetHandler autoResetHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getAutoResetHandler();
        private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
        private final Component success = messageHandler.getMessageAsComponent("RemoveSucceededMessage");
        private final Component failed = messageHandler.getMessageAsComponent("RemoveFailedMessage");
        private final Component nonExist = messageHandler.getMessageAsComponent("AreaNonExistent");
        private final Player player;
        private final String areaName;

        //Constructor
        protected Remover(Player player, String areaName) {
            this.player = player;
            this.areaName = areaName;
        }

        //Runnable
        @Override
        public void run() {

            try { //TODO RE_WRITE!
                ResultSet results = databaseHandler.getAreaData();
                while(results.next()) {
                    if (results.getString("areaName").equals(this.areaName)) {
                        UUID uuid = UUID.fromString(results.getString("uuid"));
                        databaseHandler.deleteAreaData(this.areaName);
                        databaseHandler.deleteAreaStats(uuid);
                        databaseHandler.deleteAreaTimer(uuid);

                        String filePath = "/AreaData/" + uuid + ".schem";
                        File worldData = new File(areaPlugin.getDataFolder().getAbsolutePath(), filePath);
                        boolean worldDataDeleted = worldData.delete();
                        if (worldDataDeleted) {
                            this.player.sendMessage(prefix.append(this.success));
                        } else {
                            areaPlugin.getLogger().log(Level.SEVERE, "Deletion of world data located at: " + worldData.getAbsoluteFile() + " failed.\n" +
                                                                          "Please manually delete this file to free up the occupied disk space!\n" +
                                                                          "This ISSUE does not affect AreaResetterPro's operationality!" +
                                                                          "The ISSUE is known and will be fixed in an upcoming version.");
                            this.player.sendMessage(prefix.append(this.failed));
                        }
                        return;
                    }
                }
                results.close();
                autoResetHandler.removeAreaResetter(this.areaName);
                AreaResetterProExpansion.updateValues();
                this.player.sendMessage(prefix.append(this.nonExist));
            } catch (SecurityException securityException) {
                this.player.sendMessage(prefix.append(this.failed));
                areaPlugin.getLogger().log(Level.SEVERE, "A SecurityException occurred whilst trying to remove the '.schem' file!", securityException);
            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            }

        }

    }

}
