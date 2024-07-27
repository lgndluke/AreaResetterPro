package com.lgndluke.arearesetterpro.tasks;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.commands.ToolCmd;
import com.lgndluke.arearesetterpro.data.PositionsHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * //TODO
 * @author lgndluke
 **/
public class SetPosTask extends ToolCmd {

    private final PositionsHandler positionsHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getPositionsHandler();
    private final Component setPos1 = messageHandler.getMessageAsComponent("SetPos1Message");
    private final Component setPos2 = messageHandler.getMessageAsComponent("SetPos2Message");
    private final Player player;
    private final PositionsHandler.Position position;
    private final Location location;

    public SetPosTask(Player player, PositionsHandler.Position position, Location location) {
        this.player = player;
        this.position = position;
        this.location = location;
    }

    public RunnableFuture<Boolean> execute() {
        return new FutureTask<>(() -> {
            if(player == null) {
                positionsHandler.setPosition(PositionsHandler.Position.POS1, null);
                positionsHandler.setPosition(PositionsHandler.Position.POS2, null);
                positionsHandler.save();
                positionsHandler.reload();
                return true;
            }
            positionsHandler.setPosition(position, location);
            if(position == PositionsHandler.Position.POS1) {
                player.sendMessage(prefix.append(setPos1));
            }
            if(position == PositionsHandler.Position.POS2) {
                player.sendMessage(prefix.append(setPos2));
            }
            positionsHandler.save();
            positionsHandler.reload();
            return true;
        });
    }

}