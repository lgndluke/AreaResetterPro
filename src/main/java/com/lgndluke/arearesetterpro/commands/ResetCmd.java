package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.tasks.ResetTask;
import com.lgndluke.lgndware.commands.AbstractCommandExecutor;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This Class handles the 'arp_reset' command.
 * @author lgndluke
 **/
public class ResetCmd extends AbstractCommandExecutor implements CommandExecutor {

    protected final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    protected final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = messageHandler.getMessageAsComponent("NoPermission");

    public ResetCmd() {
        super(AreaResetterPro.getPlugin(AreaResetterPro.class));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length != 1) {
            return false;
        }
        if(!(sender instanceof Player)) {
            //super.getAsyncExecutor().executeFuture(super.getPlugin().getLogger(), new ResetTask(sender, args[0]).execute(), 15, TimeUnit.SECONDS);
            super.getPlugin().getServer().getScheduler().runTaskAsynchronously(super.getPlugin(), new ResetTask(sender, args[0]).execute());
            return true;
        }
        if(!sender.hasPermission("arearesetterpro.reset")) {
            sender.sendMessage(prefix.append(noPermission));
            return true;
        }
        //super.getAsyncExecutor().executeFuture(super.getPlugin().getLogger(), new ResetTask(sender, args[0]).execute(), 15, TimeUnit.SECONDS);
        super.getPlugin().getServer().getScheduler().runTaskAsynchronously(super.getPlugin(), new ResetTask(sender, args[0]).execute());
        return true;
    }

}
