package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.tasks.EnableTask;
import com.lgndluke.lgndware.commands.AbstractCommandExecutor;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles the 'arp_enable' command.
 * @author lgndluke
 **/
public class EnableCmd extends AbstractCommandExecutor implements CommandExecutor {

    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = messageHandler.getMessageAsComponent("NoPermission");

    public EnableCmd() {
        super(AreaResetterPro.getPlugin(AreaResetterPro.class));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length != 1) {
            return false;
        }
        if(!(sender instanceof Player)) {
            //TODO Ensure console specific logging in async task.
            super.getPlugin().getServer().getScheduler().runTaskAsynchronously(super.getPlugin(), new EnableTask().execute());
            return true;
        }
        if(!(sender.hasPermission("arearesetterpro.enable"))) {
            sender.sendMessage(prefix.append(noPermission));
            return true;
        }
        //TODO Ensure player specific logging in async task.
        super.getPlugin().getServer().getScheduler().runTaskAsynchronously(super.getPlugin(), new EnableTask().execute());
        return true;
    }

}
