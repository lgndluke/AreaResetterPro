package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.arearesetterpro.data.MessageHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
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
    private static final Component prefix = MessageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = MessageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = MessageHandler.getMessageAsString("ExecutedByConsole");

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
        private final Component success = MessageHandler.getMessageAsComponent("RemoveSucceededMessage");
        private final Component failed = MessageHandler.getMessageAsComponent("RemoveFailedMessage");
        private final Component nonExist = MessageHandler.getMessageAsComponent("AreaNonExistent");
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

            //Define SQL-Strings
            String sql = "SELECT areaName FROM AreaData;";
            String sqlUuid = "SELECT uuid FROM AreaData WHERE areaName = '" + this.areaName + "';";
            String sqlRemoveData = "DELETE FROM AreaData WHERE areaName = '" + this.areaName + "';";

            List<String> resultNames = DatabaseHandler.executeQuery(sql);
            if(resultNames.contains(this.areaName)) {
                //Get UUID for removal of Files.
                UUID uuid = UUID.fromString(DatabaseHandler.executeQuery(sqlUuid).get(0));
                //Delete Database-Information of Area-Object.
                String sqlRemoveStats = "DELETE FROM AreaStats WHERE uuid = '" + uuid + "';";
                String sqlRemoveTimer = "DELETE FROM AreaTimer WHERE uuid = '" + uuid + "';";
                DatabaseHandler.execute(sqlRemoveData);
                DatabaseHandler.execute(sqlRemoveStats);
                DatabaseHandler.execute(sqlRemoveTimer);
                //Set file-path and delete .nbt-File.
                try {
                    String filePath = "AreaData/" + uuid + ".nbt";
                    File worldData = new File(areaPlugin.getDataFolder().getAbsolutePath(), filePath);
                    boolean worldDataDeleted = worldData.delete();
                    if(worldDataDeleted) {
                        this.player.sendMessage(prefix.append(this.success));
                    }
                } catch (Exception e) {
                    this.player.sendMessage(prefix.append(this.failed));
                    areaPlugin.getLogger().log(Level.SEVERE, "A SecurityException occurred whilst trying to remove the '.schem' file!", e);
                }
            } else {
                this.player.sendMessage(prefix.append(this.nonExist));
            }

        }

    }

}
