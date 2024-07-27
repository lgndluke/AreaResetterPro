package com.lgndluke.arearesetterpro.tasks;


import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.commands.GetSpawnPointCmd;
import com.lgndluke.arearesetterpro.data.SpawnPointHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * This process asynchronously executes the 'arp_getspawnpoint' commands logic.
 * @author lgndluke
 **/
public class GetSpawnTask extends GetSpawnPointCmd {

    private final SpawnPointHandler spawnPointHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getSpawnPointHandler();
    private final Component noSpawn = messageHandler.getMessageAsComponent("SpawnPointNotSet");
    private final CommandSender sender;

    public GetSpawnTask(CommandSender sender) {
        this.sender = sender;
    }

    public RunnableFuture<Boolean> execute() {
        return new FutureTask<>(() -> {
            if(spawnPointHandler.getSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT) == null) {
                sender.sendMessage(prefix.append(noSpawn));
                return false;
            }
            Component spawn = createSpawnMessage();
            sender.sendMessage(prefix.append(spawn));
            return true;
        });
    }

    private Component createSpawnMessage() {
        return MiniMessage.miniMessage().deserialize("<blue> Spawnpoint:</blue> \n" +
                "<light_purple>x: " + spawnPointHandler.getSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT).getX() + "\n" +
                "y: " + spawnPointHandler.getSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT).getY() + "\n" +
                "z: " + spawnPointHandler.getSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT).getZ() + "</light_purple>");
    }

}
