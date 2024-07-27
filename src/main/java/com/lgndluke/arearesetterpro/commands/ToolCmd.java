package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.lgndware.commands.AbstractCommandExecutor;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

/**
 * This Class handles the 'arp_tool' command.
 * @author lgndluke
 **/
public class ToolCmd extends AbstractCommandExecutor implements CommandExecutor {

    protected final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    protected final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    protected final Component noPermission = messageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = messageHandler.getMessageAsString("ExecutedByConsole");

    public ToolCmd() {
        super(AreaResetterPro.getPlugin(AreaResetterPro.class));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player)) {
            super.getPlugin().getLogger().log(Level.SEVERE, executedByConsole);
            return true;
        }
        if(!sender.hasPermission("arearesetterpro.tool")) {
            sender.sendMessage(prefix.append(noPermission));
            return true;
        }
        ((Player) sender).getInventory().addItem(new SetPosTool().getPosTool());
        return true;
    }

    /**
     * Represents the ItemStack object used to set this plugin's positions.
     * @author lgndluke
     **/
    protected class SetPosTool extends ToolCmd {

        private final NamespacedKey setPosToolKey = new NamespacedKey(super.getPlugin(), "AreaResetterPro_PosTool");
        private final ItemStack setPosTool;

        public SetPosTool() {
            setPosTool = new ItemStack(Material.IRON_SHOVEL, 1);
            ItemMeta toolMeta = setPosTool.getItemMeta();
            toolMeta.displayName(messageHandler.getMessageAsComponent("SetPosToolName"));
            toolMeta.lore(messageHandler.getMessagesAsComponentList("SetPosToolLore"));
            toolMeta.setUnbreakable(true);
            toolMeta.getPersistentDataContainer().set(setPosToolKey, PersistentDataType.STRING, setPosToolKey.value());
            setPosTool.setItemMeta(toolMeta);
        }

        private ItemStack getPosTool() {
            return setPosTool;
        }

        public NamespacedKey getPosToolKey() {
            return setPosToolKey;
        }

    }

}
