package com.lgndluke.arearesetterpro.tasks;


import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.commands.GetPosCmd;
import com.lgndluke.arearesetterpro.data.PositionsHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * This process asynchronously executes the 'arp_getpos' commands logic.
 * @author lgndluke
 **/
public class GetPosTask extends GetPosCmd {

    private final PositionsHandler positionsHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getPositionsHandler();
    private final Component noPos1 = messageHandler.getMessageAsComponent("Pos1NotSet");
    private final Component noPos2 = messageHandler.getMessageAsComponent("Pos2NotSet");
    private final CommandSender sender;

    public GetPosTask(CommandSender sender) {
        this.sender = sender;
    }

    public RunnableFuture<Boolean> execute() {
        return new FutureTask<>(() -> {
            if(positionsHandler.getPosition(PositionsHandler.Position.POS1) == null) {
                sender.sendMessage(prefix.append(noPos1));
                return false;
            }
            if(positionsHandler.getPosition(PositionsHandler.Position.POS2) == null) {
                sender.sendMessage(prefix.append(noPos2));
                return false;
            }
            Component pos1 = createPosMessage(PositionsHandler.Position.POS1);
            Component pos2 = createPosMessage(PositionsHandler.Position.POS2);
            sender.sendMessage(prefix.append(pos1));
            sender.sendMessage(prefix.append(pos2));
            return true;
        });
    }

    private Component createPosMessage(PositionsHandler.Position position) {
        return MiniMessage.miniMessage().deserialize("<blue> Position 1:</blue> \n" +
                "<light_purple>x: " + positionsHandler.getPosition(position).getX() + "\n" +
                "y: " + positionsHandler.getPosition(position).getY() + "\n" +
                "z: " + positionsHandler.getPosition(position).getZ() + "</light_purple>");
    }

}