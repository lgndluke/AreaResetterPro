package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.PositionsHandler;
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
 *  This Class handles the 'arp_getPos' command.
 *  @author lgndluke
 **/
public class GetPos implements CommandExecutor {

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
            if(sender.hasPermission("arearesetterpro.getpos")) {
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
     * Makes operations of command 'arp_getpos' async.
     * @author lgndluke
     **/
    private static class GetPosThread implements Runnable {

        //Attribute
        private final PositionsHandler positionsHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getPositionsHandler();
        private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
        private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
        private final Component noPos1 = messageHandler.getMessageAsComponent("Pos1NotSet");
        private final Component noPos2 = messageHandler.getMessageAsComponent("Pos2NotSet");
        private final CommandSender sender;

        //Constructor
        private GetPosThread(CommandSender sender) {
            this.sender = sender;
        }

        @Override
        public void run() {

            if(positionsHandler.getPosition(PositionsHandler.Position.POS1) != null && positionsHandler.getPosition(PositionsHandler.Position.POS2) != null){

                Component pos1 = MiniMessage.miniMessage().deserialize("<blue> Position 1:</blue> \n" +
                        "<light_purple>x: " + positionsHandler.getPosition(PositionsHandler.Position.POS1).getX() + "\n" +
                        "y: " + positionsHandler.getPosition(PositionsHandler.Position.POS1).getY() + "\n" +
                        "z: " + positionsHandler.getPosition(PositionsHandler.Position.POS1).getZ() + "</light_purple>");

                Component pos2 = MiniMessage.miniMessage().deserialize("<blue> Position 2:</blue> \n" +
                        "<light_purple>x: " + positionsHandler.getPosition(PositionsHandler.Position.POS2).getX() + "\n" +
                        "y: " + positionsHandler.getPosition(PositionsHandler.Position.POS2).getY() + "\n" +
                        "z: " + positionsHandler.getPosition(PositionsHandler.Position.POS2).getZ() + "</light_purple>");

                sender.sendMessage(prefix.append(pos1));
                sender.sendMessage(prefix.append(pos2));

            } else if(positionsHandler.getPosition(PositionsHandler.Position.POS1) == null) {
                sender.sendMessage(prefix.append(noPos1));
            } else if (positionsHandler.getPosition(PositionsHandler.Position.POS2) == null) {
                sender.sendMessage(prefix.append(noPos2));
            }

        }

    }

}
