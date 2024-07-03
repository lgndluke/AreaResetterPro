package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.PositionsHandler;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * This Class handles the 'arp_tool' command.
 * @author lgndluke
 **/
public class Tool implements CommandExecutor { //TODO Completely rework this process!

    //Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = messageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = messageHandler.getMessageAsString("ExecutedByConsole");

    //CommandExecutor
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("arearesetterpro.tool")) {
                ((Player) sender).getInventory().addItem(SetPosTool.getSetPosTool());
            } else {
                sender.sendMessage(prefix.append(noPermission));
            }
        } else {
            areaPlugin.getLogger().log(Level.SEVERE, executedByConsole);
        }
        return true;
    }

    /**
     * This class represents the SetPosTool as ItemStack object.
     * @author lgndluke
     **/
    private static class SetPosTool {

        //Static Attributes
        private static final NamespacedKey setPosToolKey = new NamespacedKey(areaPlugin, "AreaResetterPro_PosTool");
        private static ItemStack setPosTool;

        //Static Methods
        private static void initializePosTool() {

            setPosTool = new ItemStack(Material.IRON_SHOVEL, 1);

            ItemMeta setPosToolMeta = setPosTool.getItemMeta();

            //Change ItemStack name.
            Component name = MiniMessage.miniMessage().deserialize("<light_purple>PosTool</light_purple>");
            setPosToolMeta.displayName(name);

            //Change ItemStack lore.
            List<Component> loreData = new ArrayList<>();
            loreData.add(MiniMessage.miniMessage().deserialize("<blue>Area</blue><gold>Resetter</gold><red>Pro</red>"));
            loreData.add(MiniMessage.miniMessage().deserialize("<grey>--------------------------------------</grey>"));
            loreData.add(MiniMessage.miniMessage().deserialize("<gold>Left-Click to set Position 1.</gold>"));
            loreData.add(MiniMessage.miniMessage().deserialize("<gold>Right-Click to set Position 2.</gold>"));
            loreData.add(MiniMessage.miniMessage().deserialize("<grey>--------------------------------------</grey>"));
            setPosToolMeta.lore(loreData);

            //Make SetPosTool unbreakable.
            setPosToolMeta.setUnbreakable(true);

            //Add persistent data.
            setPosToolMeta.getPersistentDataContainer().set(setPosToolKey, PersistentDataType.STRING, setPosToolKey.value());

            //Add Meta-Data back to ItemStack.
            setPosTool.setItemMeta(setPosToolMeta);

        }

        private static ItemStack getSetPosTool() {
            if(setPosTool == null) {
                initializePosTool();
            }
            return setPosTool;
        }

        private static NamespacedKey getSetPosToolKey() {
            return setPosToolKey;
        }

    }

    /**
     * Public Inner Top-Level Class SetPosToolListener
     * -> Listens to events from the SetPosTool.
     * @author lgndluke
     **/
    public static class SetPosToolListener implements Listener {

        //Listener
        @EventHandler
        public void onToolClick(PlayerInteractEvent event) {

            Player player = event.getPlayer();
            if(player.hasPermission("arearesetterpro.tool")) {

                //event.hasItem needed to prevent NullPointerException and console spam.
                //-> Won't produce NullPointerException!
                if(event.hasItem() && event.getItem().getItemMeta().getPersistentDataContainer().has(Tool.SetPosTool.getSetPosToolKey())) {

                    //To prevent console spam with nullPointerException.
                    if(event.getClickedBlock() != null) {

                        Location location = event.getClickedBlock().getLocation();

                        if(event.getAction().isLeftClick()) {
                            areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new Tool.SavePosThread(player, PositionsHandler.Position.POS1, location));
                            event.setCancelled(true);
                        }

                        if(event.getAction().isRightClick()) {
                            areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new Tool.SavePosThread(player, PositionsHandler.Position.POS2, location));
                            event.setCancelled(true);
                        }

                    }

                }

            }

        }

    }

    /**
     * This class is used to do read/write operations to the "Positions.yml" file.
     * @author lgndluke
     **/
    public static class SavePosThread implements Runnable {

        //Attributes
        private final PositionsHandler positionsHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getPositionsHandler();
        private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
        private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
        private final Component setPos1 = messageHandler.getMessageAsComponent("SetPos1Message");
        private final Component setPos2 = messageHandler.getMessageAsComponent("SetPos2Message");
        private final Player player;
        private final PositionsHandler.Position pos;
        private final Location location;

        //Constructor
        protected SavePosThread(Player player, PositionsHandler.Position pos, Location location) {
            this.player = player;
            this.pos = pos;
            this.location = location;
        }

        //Runnable
        @Override
        public void run() {

            //This if-Statement is required to reset positions on area creation.
            if(this.player == null) {

                positionsHandler.setPosition(PositionsHandler.Position.POS1, null);
                positionsHandler.setPosition(PositionsHandler.Position.POS2, null);
                positionsHandler.save();
                positionsHandler.reload();
                return;

            } else {

                positionsHandler.setPosition(this.pos, this.location);
                positionsHandler.save();

                if(this.pos == (PositionsHandler.Position.POS1)) {
                    player.sendMessage(prefix.append(this.setPos1));
                } else {
                    player.sendMessage(prefix.append(this.setPos2));
                }

            }

            positionsHandler.reload();

        }

    }

}
