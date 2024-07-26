package com.lgndluke.arearesetterpro.tasks;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.commands.SetSpawnPointCmd;
import com.lgndluke.arearesetterpro.data.SpawnPointHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * This process asynchronously executes the 'arp_setspawnpoint' commands logic.
 * @author lgndluke
 **/
public class SetSpawnTask extends SetSpawnPointCmd {

    private final SpawnPointHandler spawnPointHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getSpawnPointHandler();
    private final Component setSpawn = messageHandler.getMessageAsComponent("SetSpawnPointMessage");
    private final Player player;
    private final Location location;

    public SetSpawnTask(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

    public RunnableFuture<Boolean> execute() {
        return new FutureTask<>(() -> {
            if(player == null) {
                spawnPointHandler.setSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT, null);
                spawnPointHandler.save();
                spawnPointHandler.reload();
                return true;
            }
            spawnPointHandler.setSpawnPoint(SpawnPointHandler.SpawnPoint.SPAWNPOINT, location);
            spawnPointHandler.save();
            spawnPointHandler.reload();
            player.sendMessage(prefix.append(setSpawn));
            return true;
        });
    }

}
