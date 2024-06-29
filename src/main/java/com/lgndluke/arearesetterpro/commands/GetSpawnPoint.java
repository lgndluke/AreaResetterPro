package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.SpawnPointHandler;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 *  This Class handles the 'arp_getSpawnPoint' command.
 *  @author lgndluke
 **/
public class GetSpawnPoint implements CommandExecutor {

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
            if(sender.hasPermission("arearesetterpro.getspawnpoint")) {
                areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new GetPosThread(sender));
            } else {
                sender.sendMessage(prefix.append(noPermission));
            }
        } else {
            areaPlugin.getLogger().log(Level.SEVERE, executedByConsole);
        }
        return true;
    }

    /**
     * Private Inner Top-Level Class GetPosAsync.
     * Operation of command 'arp_getSpawnPoint'.
     * @author lgndluke
     **/
    private static class GetPosThread implements Runnable {

        //Attributes
        private final SpawnPointHandler spawnPointHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getSpawnPointHandler();
        private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
        private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
        private final Component noSpawn = messageHandler.getMessageAsComponent("SpawnPointNotSet");
        private final CommandSender sender;

        //Constructor
        private GetPosThread(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public void run() {

            if(spawnPointHandler.getSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT) != null){

                Component spawn = MiniMessage.miniMessage().deserialize("<blue> Spawnpoint:</blue> \n" +
                        "<light_purple>x: " + spawnPointHandler.getSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT).getX() + "\n" +
                        "y: " + spawnPointHandler.getSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT).getY() + "\n" +
                        "z: " + spawnPointHandler.getSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT).getZ() + "</light_purple>");

                sender.sendMessage(prefix.append(spawn));

            } else if(spawnPointHandler.getSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT) == null) {
                sender.sendMessage(prefix.append(noSpawn));
            }

        }

    }

}
