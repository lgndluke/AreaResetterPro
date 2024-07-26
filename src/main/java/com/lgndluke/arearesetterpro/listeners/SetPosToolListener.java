package com.lgndluke.arearesetterpro.listeners;

import com.lgndluke.arearesetterpro.commands.ToolCmd;
import com.lgndluke.arearesetterpro.data.PositionsHandler;
import com.lgndluke.arearesetterpro.tasks.SetPosTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Listener for events from SetPosTool.
 * @author lgndluke
 **/
public class SetPosToolListener extends ToolCmd implements Listener {

    private final SetPosTool tool = new SetPosTool();

    @EventHandler
    public void onToolClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!player.hasPermission("arearesetterpro.tool")) {
            player.sendMessage(prefix.append(noPermission));
            return;
        }
        if(event.hasItem() && event.getItem().getItemMeta().getPersistentDataContainer().has(tool.getPosToolKey())) {
            if(event.getClickedBlock() != null) {
                Location location = event.getClickedBlock().getLocation();
                if(event.getAction().isLeftClick()) {
                    execute(player, PositionsHandler.Position.POS1, location);
                    event.setCancelled(true);
                }
                if(event.getAction().isRightClick()) {
                    execute(player, PositionsHandler.Position.POS2, location);
                    event.setCancelled(true);
                }
            }
        }

    }

    private void execute(Player player, PositionsHandler.Position position, Location location) {
        //super.getAsyncExecutor().executeFuture(super.getPlugin().getLogger(), new SetPosTask(player, position, location).execute(), 15, TimeUnit.SECONDS);
        super.getPlugin().getServer().getScheduler().runTaskAsynchronously(super.getPlugin(), new SetPosTask(player, position, location).execute());
    }

}
