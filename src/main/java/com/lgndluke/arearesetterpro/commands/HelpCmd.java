package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.lgndware.commands.AbstractCommandExecutor;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * This Class handles the 'arp_help' command.
 * @author lgndluke
 **/
public class HelpCmd extends AbstractCommandExecutor implements CommandExecutor {

    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = messageHandler.getMessageAsComponent("NoPermission");

    public HelpCmd() {
        super(AreaResetterPro.getPlugin(AreaResetterPro.class));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("arearesetterpro.help")) {
            sender.sendMessage(prefix.append(noPermission));
            return true;
        }
        if(!(sender instanceof Player)) {
            super.getPlugin().getLogger().log(Level.INFO, "-----------------------------------");
            super.getPlugin().getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpCreate"));
            super.getPlugin().getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpGetPos"));
            super.getPlugin().getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpGetSpawn"));
            super.getPlugin().getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpSetSpawn"));
            super.getPlugin().getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpMenu"));
            super.getPlugin().getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpReload"));
            super.getPlugin().getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpRemove"));
            super.getPlugin().getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpReset"));
            super.getPlugin().getLogger().log(Level.INFO, messageHandler.getMessageAsString("HelpTool"));
            super.getPlugin().getLogger().log(Level.INFO, "-----------------------------------");
            return true;
        }
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
        return true;
    }

}
