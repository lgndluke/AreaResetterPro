package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.MessageHandler;
import com.lgndluke.arearesetterpro.data.SpawnPointHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

public class SetSpawnPoint implements CommandExecutor {

    //Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static final Component prefix = MessageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = MessageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = MessageHandler.getMessageAsString("ExecutedByConsole");

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
        private final Component setSpawn = MessageHandler.getMessageAsComponent("SetSpawnPointMessage");
        private final Component setSpawnFailed = MessageHandler.getMessageAsComponent("SetSpawnPointFailed");
        private final Player player;
        private final Location location;

        protected SaveSpawnThread(Player player, Location location) {
            this.player = player;
            this.location = location;
        }

        //Runnable
        @Override
        public void run() {

            try {

                //This if-Statement is required to reset the spawnpoint on area creation.
                if(this.player == null) {
                    SpawnPointHandler.setSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT, null);
                    SpawnPointHandler.save();
                    SpawnPointHandler.reload();
                    return;
                } else {
                    SpawnPointHandler.setSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT, this.location);
                    SpawnPointHandler.save();
                    player.sendMessage(prefix.append(this.setSpawn));
                }

                SpawnPointHandler.reload();

            } catch (IOException io) {

                if(this.player != null) {
                    this.player.sendMessage(prefix.append(this.setSpawnFailed));
                }
                String plainSetPosFailed = PlainTextComponentSerializer.plainText().serialize(setSpawnFailed);
                areaPlugin.getLogger().log(Level.SEVERE, plainSetPosFailed, io);

            }

        }

    }

}
