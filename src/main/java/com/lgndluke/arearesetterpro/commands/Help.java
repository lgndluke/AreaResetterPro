package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.MessageHandler;
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
    private final Component prefix = MessageHandler.getMessageAsComponent("Prefix");

    //CommandExecutor
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player) { //Sender is player.
            if(sender.hasPermission("arearesetterpro.help")) {
                sender.sendMessage(this.prefix.append(PlainTextComponentSerializer.plainText().deserialize("-----------------------------------")));
                sender.sendMessage(this.prefix.append(MessageHandler.getMessageAsComponent("HelpCreate")));
                sender.sendMessage(this.prefix.append(MessageHandler.getMessageAsComponent("HelpGetPos")));
                sender.sendMessage(this.prefix.append(MessageHandler.getMessageAsComponent("HelpMenu")));
                sender.sendMessage(this.prefix.append(MessageHandler.getMessageAsComponent("HelpReload")));
                sender.sendMessage(this.prefix.append(MessageHandler.getMessageAsComponent("HelpRemove")));
                sender.sendMessage(this.prefix.append(MessageHandler.getMessageAsComponent("HelpReset")));
                sender.sendMessage(this.prefix.append(MessageHandler.getMessageAsComponent("HelpTool")));
                sender.sendMessage(this.prefix.append(PlainTextComponentSerializer.plainText().deserialize("-----------------------------------")));
            } else {
                sender.sendMessage(this.prefix.append(MessageHandler.getMessageAsComponent("NoPermission")));
            }
        } else { //Sender is console.
            areaPlugin.getLogger().log(Level.INFO, "-----------------------------------");
            areaPlugin.getLogger().log(Level.INFO, MessageHandler.getMessageAsString("HelpCreate"));
            areaPlugin.getLogger().log(Level.INFO, MessageHandler.getMessageAsString("HelpGetPos"));
            areaPlugin.getLogger().log(Level.INFO, MessageHandler.getMessageAsString("HelpMenu"));
            areaPlugin.getLogger().log(Level.INFO, MessageHandler.getMessageAsString("HelpReload"));
            areaPlugin.getLogger().log(Level.INFO, MessageHandler.getMessageAsString("HelpRemove"));
            areaPlugin.getLogger().log(Level.INFO, MessageHandler.getMessageAsString("HelpReset"));
            areaPlugin.getLogger().log(Level.INFO, MessageHandler.getMessageAsString("HelpTool"));
            areaPlugin.getLogger().log(Level.INFO, "-----------------------------------");
        }
        return true;
    }

}
