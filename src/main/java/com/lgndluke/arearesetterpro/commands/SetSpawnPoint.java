package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.SpawnPointHandler;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class SetSpawnPoint implements CommandExecutor { //TODO Completely rework this process!

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
            if(sender.hasPermission("arearesetterpro.setspawnpoint")) {
                areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new SaveSpawnThread((Player) sender, ((Player) sender).getLocation()));
            } else {
                sender.sendMessage(prefix.append(noPermission));
            }
        } else {
            areaPlugin.getLogger().log(Level.SEVERE, executedByConsole);
        }
        return true;
    }

    /**
     * This class is used to do read/write operations to the "SpawnPoint.yml" file.
     * @author lgndluke
     **/
    protected static class SaveSpawnThread implements Runnable {

        //Attributes
        private final SpawnPointHandler spawnPointHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getSpawnPointHandler();
        private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
        private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
        private final Component setSpawn = messageHandler.getMessageAsComponent("SetSpawnPointMessage");
        private final Player player;
        private final Location location;

        protected SaveSpawnThread(Player player, Location location) {
            this.player = player;
            this.location = location;
        }

        //Runnable
        @Override
        public void run() {

            //This if-Statement is required to reset the spawnpoint on area creation.
            if(this.player == null) {
                spawnPointHandler.setSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT, null);
                spawnPointHandler.save();
                spawnPointHandler.reload();
                return;
            } else {
                spawnPointHandler.setSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT, this.location);
                spawnPointHandler.save();
                player.sendMessage(prefix.append(this.setSpawn));
            }

            spawnPointHandler.reload();

        }

    }

}
