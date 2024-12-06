package com.lgndluke.arearesetterpro.ui;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.autoresets.AutoResetHandler;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.lgndware.data.ConfigHandler;
import com.lgndluke.lgndware.data.MessageHandler;
import com.lgndluke.lgndware.ui.AbstractInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This Class represents a part of the plugins GUI.
 * @author lgndluke
 **/
public class TimerMenu extends AbstractInventory {

    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
    private final String areaName;
    private ItemStack smallIncreaseItem, mediumIncreaseItem, largeIncreaseItem, displayTimerItem, smallDecreaseItem, mediumDecreaseItem, largeDecreaseItem, backItem;

    public TimerMenu(String areaName) {
        super(AreaResetterPro.getPlugin(AreaResetterPro.class), 27,  MiniMessage.miniMessage().deserialize("<blue>Area</blue><gold>Resetter</gold><red>Pro</red>"));
        this.areaName = areaName;
        initialize();
        super.getPlugin().getServer().getPluginManager().registerEvents(new TimerMenuListener(), super.getPlugin());
    }

    @Override
    protected boolean initialize() {
        smallIncreaseItem = constructItem(Material.GREEN_STAINED_GLASS_PANE, messageHandler.getMessageAsComponent("SmallIncreaseItemName"), messageHandler.getMessagesAsComponentList("SmallIncreaseItemLore"));
        mediumIncreaseItem = constructItem(Material.GREEN_STAINED_GLASS_PANE, messageHandler.getMessageAsComponent("MediumIncreaseItemName"), messageHandler.getMessagesAsComponentList("MediumIncreaseItemLore"));
        largeIncreaseItem = constructItem(Material.GREEN_STAINED_GLASS_PANE, messageHandler.getMessageAsComponent("LargeIncreaseItemName"), messageHandler.getMessagesAsComponentList("LargeIncreaseItemLore"));
        smallDecreaseItem = constructItem(Material.RED_STAINED_GLASS_PANE, messageHandler.getMessageAsComponent("SmallDecreaseItemName"), messageHandler.getMessagesAsComponentList("SmallDecreaseItemLore"));
        mediumDecreaseItem = constructItem(Material.RED_STAINED_GLASS_PANE, messageHandler.getMessageAsComponent("MediumDecreaseItemName"), messageHandler.getMessagesAsComponentList("MediumDecreaseItemLore"));
        largeDecreaseItem = constructItem(Material.RED_STAINED_GLASS_PANE, messageHandler.getMessageAsComponent("LargeDecreaseItemName"), messageHandler.getMessagesAsComponentList("LargeDecreaseItemLore"));
        displayTimerItem = constructItem(Material.NETHER_STAR, messageHandler.getMessageAsComponent("DisplayTimerItemName"), getDisplayTimerItemLore());
        backItem = constructItem(Material.BARRIER, messageHandler.getMessageAsComponent("BackItemName"), messageHandler.getMessagesAsComponentList("BackItemLore"));
        fillContents();
        return true;
    }

    private void fillContents() {
        super.getInventory().clear();
        super.getInventory().setItem(10, smallIncreaseItem);
        super.getInventory().setItem(11, mediumIncreaseItem);
        super.getInventory().setItem(12, largeIncreaseItem);
        super.getInventory().setItem(13, displayTimerItem);
        super.getInventory().setItem(14, smallDecreaseItem);
        super.getInventory().setItem(15, mediumDecreaseItem);
        super.getInventory().setItem(16, largeDecreaseItem);
        super.getInventory().setItem(super.getInventory().getSize()-1, backItem);
    }

    private List<Component> getDisplayTimerItemLore() {
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

    @Override
    public Inventory getInventory() {
        return super.getInventory();
    }

    private void updateDisplayItem() {
        displayTimerItem = constructItem(Material.NETHER_STAR, messageHandler.getMessageAsComponent("DisplayTimerItemName"), getDisplayTimerItemLore());
        super.getInventory().setItem(13, displayTimerItem);
    }

    /**
     * This Class listens for and executes events from the TimerMenu.
     * @author lgndluke
     **/
    private class TimerMenuListener implements Listener {

        private final ConfigHandler configHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getConfigHandler();
        private final AutoResetHandler autoResetHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getAutoResetHandler();

        @EventHandler
        public void onTimerMenuClickEvent(InventoryClickEvent event) {
            if(event.getClickedInventory() == null || event.getClickedInventory() != TimerMenu.this.getInventory() || event.getCurrentItem() == null) {
                return;
            }
            Player player = (Player) event.getWhoClicked();
            if(smallIncreaseItem.equals(event.getCurrentItem())) {
                try {
                    int currTimerVal = databaseHandler.getAreaTimer(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid"))).getInt("timerValue");
                    int increaseVal = (int) configHandler.get("SmallIncreaseValue");
                    databaseHandler.updateAreaTimerTimerValue(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid")), currTimerVal+increaseVal);
                    autoResetHandler.updateAreaResetInterval(areaName, currTimerVal+increaseVal);
                    TimerMenu.this.updateDisplayItem();
                } catch (SQLException se) {
                    TimerMenu.this.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                }
            }
            if(mediumIncreaseItem.equals(event.getCurrentItem())) {
                try {
                    int currTimerVal = databaseHandler.getAreaTimer(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid"))).getInt("timerValue");
                    int increaseVal = (int) configHandler.get("MediumIncreaseValue");
                    databaseHandler.updateAreaTimerTimerValue(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid")), currTimerVal+increaseVal);
                    autoResetHandler.updateAreaResetInterval(areaName, currTimerVal+increaseVal);
                    TimerMenu.this.updateDisplayItem();
                } catch (SQLException se) {
                    TimerMenu.this.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                }
            }
            if(largeIncreaseItem.equals(event.getCurrentItem())) {
                try {
                    int currTimerVal = databaseHandler.getAreaTimer(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid"))).getInt("timerValue");
                    int increaseVal = (int) configHandler.get("LargeIncreaseValue");
                    databaseHandler.updateAreaTimerTimerValue(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid")), currTimerVal+increaseVal);
                    autoResetHandler.updateAreaResetInterval(areaName, currTimerVal+increaseVal);
                    TimerMenu.this.updateDisplayItem();
                } catch (SQLException se) {
                    TimerMenu.this.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                }
            }
            if(smallDecreaseItem.equals(event.getCurrentItem())) {
                try {
                    int currTimerVal = databaseHandler.getAreaTimer(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid"))).getInt("timerValue");
                    int increaseVal = (int) configHandler.get("SmallDecreaseValue");
                    databaseHandler.updateAreaTimerTimerValue(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid")), currTimerVal-increaseVal);
                    autoResetHandler.updateAreaResetInterval(areaName, currTimerVal-increaseVal);
                    TimerMenu.this.updateDisplayItem();
                } catch (SQLException se) {
                    TimerMenu.this.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                }
            }
            if(mediumDecreaseItem.equals(event.getCurrentItem())) {
                try {
                    int currTimerVal = databaseHandler.getAreaTimer(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid"))).getInt("timerValue");
                    int increaseVal = (int) configHandler.get("MediumDecreaseValue");
                    databaseHandler.updateAreaTimerTimerValue(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid")), currTimerVal-increaseVal);
                    autoResetHandler.updateAreaResetInterval(areaName, currTimerVal-increaseVal);
                    TimerMenu.this.updateDisplayItem();
                } catch (SQLException se) {
                    TimerMenu.this.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                }
            }
            if(largeDecreaseItem.equals(event.getCurrentItem())) {
                try {
                    int currTimerVal = databaseHandler.getAreaTimer(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid"))).getInt("timerValue");
                    int increaseVal = (int) configHandler.get("LargeDecreaseValue");
                    databaseHandler.updateAreaTimerTimerValue(UUID.fromString(databaseHandler.getAreaData(areaName).getString("uuid")), currTimerVal-increaseVal);
                    autoResetHandler.updateAreaResetInterval(areaName, currTimerVal-increaseVal);
                    TimerMenu.this.updateDisplayItem();
                } catch (SQLException se) {
                    TimerMenu.this.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                }
            }
            if(backItem.equals(event.getCurrentItem())) {
                player.openInventory(new SettingsMenu(areaName).getInventory());
            }
            event.setCancelled(true);
        }

    }

}
