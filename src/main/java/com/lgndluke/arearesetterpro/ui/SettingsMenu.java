package com.lgndluke.arearesetterpro.ui;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.arearesetterpro.tasks.ResetTask;
import com.lgndluke.lgndware.data.MessageHandler;
import com.lgndluke.lgndware.ui.AbstractInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This Class represents a part of the plugins GUI.
 * @author lgndluke
 **/
public class SettingsMenu extends AbstractInventory {

    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
    private ItemStack instantResetItem, timerItem, teleportItem, statsItem, backItem;
    private final String areaName;

    public SettingsMenu(String areaName) {
        super(AreaResetterPro.getPlugin(AreaResetterPro.class), 27, MiniMessage.miniMessage().deserialize("<blue>Area</blue><gold>Resetter</gold><red>Pro</red>"));
        this.areaName = areaName;
        initialize();
        super.getPlugin().getServer().getPluginManager().registerEvents(new SettingsMenuListener(), super.getPlugin());
    }

    @Override
    protected boolean initialize() {
        instantResetItem = constructItem(Material.DIAMOND, messageHandler.getMessageAsComponent("InstantResetItemName"), messageHandler.getMessagesAsComponentList("InstantResetItemLore"));
        timerItem = constructItem(Material.CLOCK, messageHandler.getMessageAsComponent("TimerItemName"), getTimerItemLore());
        teleportItem = constructItem(Material.ENDER_PEARL, messageHandler.getMessageAsComponent("TeleportItemName"), messageHandler.getMessagesAsComponentList("TeleportItemLore"));
        statsItem = constructItem(Material.EMERALD, messageHandler.getMessageAsComponent("StatsItemName"), getStatsItemLore());
        backItem = constructItem(Material.BARRIER, messageHandler.getMessageAsComponent("BackItemName"), messageHandler.getMessagesAsComponentList("BackItemLore"));
        fillContents();
        return true;
    }

    private void fillContents() {
        super.getInventory().clear();
        super.getInventory().setItem(11, instantResetItem);
        super.getInventory().setItem(12, timerItem);
        super.getInventory().setItem(13, teleportItem);
        super.getInventory().setItem(14, statsItem);
        super.getInventory().setItem(15, backItem);
    }

    private void updateStatsItem() {
        statsItem = constructItem(Material.EMERALD, messageHandler.getMessageAsComponent("StatsItemName"), getStatsItemLore());
        super.getInventory().setItem(14, statsItem);
    }

    @Override
    public Inventory getInventory() {
        return super.getInventory();
    }

    private List<Component> getTimerItemLore() {
        try {
            List<Component> loreList = messageHandler.getMessagesAsComponentList("TimerItemLore");
            int timerValue = databaseHandler.getAreaTimer(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid"))).getInt("timerValue");
            loreList.set(2, loreList.get(2).appendSpace().append(Component.text(String.format("%02d:%02d:%02d", timerValue/3600, (timerValue % 3600)/60, timerValue % 60))));
            return loreList;
        } catch (SQLException se) {
            super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            return null;
        }
    }

    private List<Component> getStatsItemLore() {
        try {
            List<Component> loreList = messageHandler.getMessagesAsComponentList("StatsItemLore");
            loreList.set(2, loreList.get(2).appendSpace().append(Component.text(new DecimalFormat("#,###").format(databaseHandler.getAreaStats(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid"))).getInt("timesReset")).replace(",", "."))));
            loreList.set(3, loreList.get(3).appendSpace().append(Component.text(new DecimalFormat("#,###").format(databaseHandler.getAreaStats(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid"))).getLong("overallBlocks")).replace(",", "."))));
            loreList.set(4, loreList.get(4).appendSpace().append(Component.text(String.valueOf(databaseHandler.getAreaStats(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid"))).getBoolean("entitiesSaved")))));
            loreList.set(5, loreList.get(5).appendSpace().append(Component.text(String.valueOf(databaseHandler.getAreaStats(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid"))).getObject("createdOn")))));
            return loreList;
        } catch (SQLException se) {
            super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            return null;
        }
    }

    /**
     * This Class listens for and executes events from the SettingsMenu.
     * @author lgndluke
     **/
    private class SettingsMenuListener implements Listener {

        private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
        private final Component teleportationFailed = messageHandler.getMessageAsComponent("TeleportationFailed");

        @EventHandler
        public void onSettingsMenuClickEvent(InventoryClickEvent event) {
            if(event.getCurrentItem() == null) {
                return;
            }
            if(event.getClickedInventory() != SettingsMenu.this.getInventory()) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            if(instantResetItem.equals(event.getCurrentItem())) {
                SettingsMenu.this.getPlugin().getServer().getScheduler().runTaskAsynchronously(SettingsMenu.this.getPlugin(), new ResetTask(null, areaName).execute());
                SettingsMenu.this.updateStatsItem();
            }
            if(timerItem.equals(event.getCurrentItem())) {
                player.openInventory(new TimerMenu(areaName).getInventory());
            }
            if(teleportItem.equals(event.getCurrentItem())) {
                try {
                    World world = WorldCreator.name(databaseHandler.getAreaData(areaName).getString("world")).createWorld();
                    player.teleportAsync(new Location(world,
                            databaseHandler.getAreaData(areaName).getInt("xValSpawn"),
                            databaseHandler.getAreaData(areaName).getInt("yValSpawn"),
                            databaseHandler.getAreaData(areaName).getInt("zValSpawn")));
                    event.setCancelled(true);
                } catch (SQLException se) {
                    player.getInventory().close();
                    player.sendMessage(prefix.append(teleportationFailed));
                    SettingsMenu.this.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                }
            }
            if(backItem.equals(event.getCurrentItem())) {
                player.openInventory(new AreaMenu().getInventory());
            }
            event.setCancelled(true);
        }

    }

}
