package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.tasks.SetSpawnTask;
import com.lgndluke.lgndware.commands.AbstractCommandExecutor;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * This Class handles the 'arp_setspawnpoint' command.
 * @author lgndluke
 **/
public class SetSpawnPointCmd extends AbstractCommandExecutor implements CommandExecutor {

    protected final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    protected final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = messageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = messageHandler.getMessageAsString("ExecutedByConsole");

    public SetSpawnPointCmd() {
        super(AreaResetterPro.getPlugin(AreaResetterPro.class));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            super.getPlugin().getLogger().log(Level.SEVERE, executedByConsole);
        }
        if(!sender.hasPermission("arearesetterpro.setspawnpoint")) {
            sender.sendMessage(prefix.append(noPermission));
        }
        //super.getAsyncExecutor().executeFuture(super.getPlugin().getLogger(), new SaveSpawnTask((Player) sender, ((Player) sender).getLocation()).execute(), 15, TimeUnit.SECONDS);
        super.getPlugin().getServer().getScheduler().runTaskAsynchronously(super.getPlugin(), new SetSpawnTask((Player) sender, ((Player) sender).getLocation()).execute());
        return true;
    }

}
