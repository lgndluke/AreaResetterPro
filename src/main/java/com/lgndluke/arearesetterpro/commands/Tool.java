package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.MessageHandler;
import com.lgndluke.arearesetterpro.data.PositionsHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * This Class handles the 'arp_tool' command.
 * @author lgndluke
 **/
public class Tool implements CommandExecutor {

    //Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static final Component prefix = MessageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = MessageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = MessageHandler.getMessageAsString("ExecutedByConsole");

    //CommandExecutor
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("areareseterpro.tool")) {
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
     * Public Inner Top-Level Class SetPosTool
     * -> Represents the SetPosTool as ItemStack object.
     * @author lgndluke
     **/
    private static class SetPosTool {

        //Static Attributes
        private static final NamespacedKey setPosToolKey = new NamespacedKey(areaPlugin, "AreaReseterPro_PosTool");
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
            loreData.add(MiniMessage.miniMessage().deserialize("<blue>Area</blue><gold>Reseter</gold><red>Pro</red>"));
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
     * Public Inner Top-Level Class SavePosThread
     * -> This class is used to registered Positions to the "Positions.yml" file.
     * @author lgndluke
     **/
    protected static class SavePosThread implements Runnable {

        //Attributes
        private final Component setPos1 = MessageHandler.getMessageAsComponent("SetPos1Message");
        private final Component setPos2 = MessageHandler.getMessageAsComponent("SetPos2Message");
        private final Component setPosFailed = MessageHandler.getMessageAsComponent("SetPosFailed");
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

            //This if-Statement is required to reset positions on Area-Creation.
            if(this.player == null) {
                PositionsHandler.setPosition(PositionsHandler.Position.POS1, null);
                PositionsHandler.setPosition(PositionsHandler.Position.POS2, null);
                return;
            } else {
                PositionsHandler.setPosition(this.pos, this.location);
                PositionsHandler.save();

                if(this.pos == (PositionsHandler.Position.POS1)) {
                    player.sendMessage(prefix.append(this.setPos1));
                } else {
                    player.sendMessage(prefix.append(this.setPos2));
                }
            }

            //Reload the 'Positions.yml' file.
            try {
                PositionsHandler.reload();
            } catch (IOException io) {
                this.player.sendMessage(prefix.append(this.setPosFailed));
                String plainSetPosFailed = PlainTextComponentSerializer.plainText().serialize(setPosFailed);
                areaPlugin.getLogger().log(Level.SEVERE, plainSetPosFailed, io);
            }

        }

    }

}
