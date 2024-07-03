package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.PositionsHandler;
import com.lgndluke.arearesetterpro.data.SpawnPointHandler;
import com.lgndluke.lgndware.data.ConfigHandler;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * This Class handles the 'arp_reload' command.
 * Reloads ONLY config files.
 * @author lgndluke
 **/
public class Reload implements CommandExecutor { //TODO Completely rework this process!

    //Attributes
    private final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private final PositionsHandler positionsHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getPositionsHandler();
    private final SpawnPointHandler spawnPointHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getSpawnPointHandler();
    private final ConfigHandler configHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getConfigHandler();
    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    private final Component reloadMsg = messageHandler.getMessageAsComponent("ReloadConfigsMessage");
    private final Component noPermission = messageHandler.getMessageAsComponent("NoPermission");

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
        configHandler.reload();
        messageHandler.reload();
        positionsHandler.reload();
        spawnPointHandler.reload();
        AreaResetterPro.getPlugin(AreaResetterPro.class).getAreaResetterProExpansion().updateValues();
        sender.sendMessage(prefix.append(reloadMsg));
    }

    private void reloadByConsole() {
        configHandler.reload();
        messageHandler.reload();
        positionsHandler.reload();
        spawnPointHandler.reload();
        AreaResetterPro.getPlugin(AreaResetterPro.class).getAreaResetterProExpansion().updateValues();
        areaPlugin.getLogger().log(Level.INFO, messageHandler.getMessageAsString("ReloadConfigsMessage"));
    }

}
