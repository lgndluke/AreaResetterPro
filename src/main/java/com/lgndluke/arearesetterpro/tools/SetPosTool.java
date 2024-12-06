package com.lgndluke.arearesetterpro.tools;

import com.lgndluke.arearesetterpro.commands.ToolCmd;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * Represents the ItemStack object used to set the plugins positions.
 * @author lgndluke
 **/
public class SetPosTool extends ToolCmd {

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

    public ItemStack getPosTool() {
        return setPosTool;
    }

    public NamespacedKey getPosToolKey() {
        return setPosToolKey;
    }

}
