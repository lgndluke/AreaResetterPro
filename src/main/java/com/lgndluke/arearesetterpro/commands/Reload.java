package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.ConfigHandler;
import com.lgndluke.arearesetterpro.data.MessageHandler;
import com.lgndluke.arearesetterpro.data.PositionsHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;

/**
 * This Class handles the 'arp_reload' command.
 * Reloads ONLY config files.
 * @author lgndluke
 **/
public class Reload implements CommandExecutor {

    //Attributes
    private final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private final Component prefix = MessageHandler.getMessageAsComponent("Prefix");
    private final Component reloadMsg = MessageHandler.getMessageAsComponent("ReloadConfigsMessage");
    private final Component reloadFailedMsg = MessageHandler.getMessageAsComponent("ReloadConfigsFailedMessage");
    private final Component noPermission = MessageHandler.getMessageAsComponent("NoPermission");

    //CommandExecutor
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("arearesetterpro.reload")) {
                reloadByPlayer(sender);
            } else {
                sender.sendMessage(prefix.append(noPermission));
            }
        } else {
            reloadByConsole();
        }
        return true;
    }

    //Methods
    private void reloadByPlayer(CommandSender sender) {
        try {
            ConfigHandler.reload();
            MessageHandler.reload();
            PositionsHandler.reload();
            sender.sendMessage(prefix.append(reloadMsg));
        } catch (IOException io) {
            sender.sendMessage(prefix.append(reloadFailedMsg));
        }
    }

    private void reloadByConsole() {
        try {
            ConfigHandler.reload();
            MessageHandler.reload();
            PositionsHandler.reload();
            areaPlugin.getLogger().log(Level.INFO, MessageHandler.getMessageAsString("ReloadConfigsMessage"));
        } catch (IOException io) {
            areaPlugin.getLogger().log(Level.SEVERE, MessageHandler.getMessageAsString("ReloadConfigsFailedMessage"));
        }
    }

}
