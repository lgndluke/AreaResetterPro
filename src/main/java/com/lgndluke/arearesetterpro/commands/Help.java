package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class Help implements CommandExecutor {

    //Attributes
    private final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final Component prefix = messageHandler.getMessageAsComponent("Prefix");

    //CommandExecutor
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player) { //Sender is player.
            if(sender.hasPermission("arearesetterpro.help")) {
                sender.sendMessage(this.prefix.append(PlainTextComponentSerializer.plainText().deserialize("-----------------------------------")));
                sender.sendMessage(this.prefix.append(messageHandler.getMessageAsComponent("HelpCreate")));
                sender.sendMessage(this.prefix.append(messageHandler.getMessageAsComponent("HelpGetPos")));
                sender.sendMessage(this.prefix.append(messageHandler.getMessageAsComponent("HelpGetSpawn")));
                sender.sendMessage(this.prefix.append(messageHandler.getMessageAsComponent("HelpSetSpawn")));
                sender.sendMessage(this.prefix.append(messageHandler.getMessageAsComponent("HelpMenu")));
                sender.sendMessage(this.prefix.append(messageHandler.getMessageAsComponent("HelpReload")));
                sender.sendMessage(this.prefix.append(messageHandler.getMessageAsComponent("HelpRemove")));
                sender.sendMessage(this.prefix.append(messageHandler.getMessageAsComponent("HelpReset")));
                sender.sendMessage(this.prefix.append(messageHandler.getMessageAsComponent("HelpTool")));
                sender.sendMessage(this.prefix.append(PlainTextComponentSerializer.plainText().deserialize("-----------------------------------")));
            } else {
                sender.sendMessage(this.prefix.append(messageHandler.getMessageAsComponent("NoPermission")));
            }
        } else { //Sender is console.
            areaPlugin.getLogger().log(Level.INFO, "-----------------------------------");
            areaPlugin.getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpCreate"));
            areaPlugin.getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpGetPos"));
            areaPlugin.getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpGetSpawn"));
            areaPlugin.getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpSetSpawn"));
            areaPlugin.getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpMenu"));
            areaPlugin.getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpReload"));
            areaPlugin.getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpRemove"));
            areaPlugin.getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpReset"));
            areaPlugin.getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpTool"));
            areaPlugin.getLogger().log(Level.INFO, "-----------------------------------");
        }
        return true;
    }

}
