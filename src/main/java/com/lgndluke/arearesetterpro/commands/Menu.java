package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.AutoResetHandler;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.arearesetterpro.loaders.ListenerLoader;
import com.lgndluke.lgndware.data.ConfigHandler;
import com.lgndluke.lgndware.data.MessageHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * This Class handles the 'arp_menu' command.
 * @author lgndluke
 **/
public class Menu implements CommandExecutor {

    //Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private final ConfigHandler configHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getConfigHandler();
    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
    private final ListenerLoader listenerLoader = AreaResetterPro.getPlugin(AreaResetterPro.class).getListenerLoader();
    private final AutoResetHandler autoResetHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getAutoResetHandler();
    private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = messageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = messageHandler.getMessageAsString("ExecutedByConsole");

    //CommandExecutor
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if(sender.hasPermission("arearesetterpro.menu")) {
                AreaInventory areaInv = new AreaInventory();
                ((Player) sender).openInventory(areaInv.getInvPage(0));
            } else {
                sender.sendMessage(prefix.append(noPermission));
            }
        } else {
            areaPlugin.getLogger().log(Level.SEVERE, executedByConsole);
        }
        return true;
    }

    //Methods
    /**
     * Method that provides Inventory objects with empty item objects.
     **/
    private ItemStack getEmptyItem() {
        ItemStack emptyItem = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
        //-----------------------------------------------------------
        //Initialize emptyItem.

        //Create the metadata object.
        ItemMeta emptyItemMeta = emptyItem.getItemMeta();

        //Change the object name.
        emptyItemMeta.displayName(messageHandler.getMessageAsComponent("EmptyItemName"));

        //Change the object lore.
        emptyItemMeta.lore(messageHandler.getMessagesAsComponentList("EmptyItemLore"));

        //Set NamespacedKey.
        NamespacedKey emptyKey = new NamespacedKey(areaPlugin, UUID.randomUUID().toString());
        emptyItemMeta.getPersistentDataContainer().set(emptyKey, PersistentDataType.STRING, emptyKey.value());

        //Assemble metadata back to item.
        emptyItem.setItemMeta(emptyItemMeta);
        //-----------------------------------------------------------
        return emptyItem;
    }

    //Inner Classes
    /**
     * Private Inner Top-Level Class AreaPluginInventory.
     * -> Represents the graphical in-game menu of the Plugin.
     * @author lgndluke
     **/
    private class AreaInventory {

        //Attributes
        private final List<ItemStack> contents = new ArrayList<>();
        private final ItemStack previousPageItem;
        private final ItemStack nextPageItem;
        private final ItemStack resetAllItem;
        private final Inventory inv = areaPlugin.getServer().createInventory(null, 54, MiniMessage.miniMessage().deserialize("<blue>Area</blue><gold>Resetter</gold><red>Pro</red>"));
        private final int pages;

        //Constructor
        private AreaInventory() {
            //-----------------------------------------------------------------------------------------------------
            //Database-Operations.

            int count = 0;
            ArrayList<String> names = new ArrayList<>();

            try {
                ResultSet results = databaseHandler.getAreaData();
                while(results.next()) {
                    count++;
                    names.add(results.getString("areaName"));
                }
                results.close();
            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            }

            //-----------------------------------------------------------------------------------------------------
            //Contents-Operations.

            //Determine how many pages the inventory will have.
            pages = (int) Math.ceil((double) count / 45);

            //-----------------------------------------------------------
            //Fill list 'content'.
            for(int i=0; i<count; i++) {

                //Create the item object.
                ItemStack addItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);

                //Create the metadata object.
                ItemMeta addMeta = addItem.getItemMeta();

                //Change the object name.
                Component addName = Component.text(names.get(i));
                addMeta.displayName(addName);

                //Change the object lore.
                addMeta.lore(messageHandler.getMessagesAsComponentList("AreaItemLore"));

                //Assemble metadata back to item.
                addItem.setItemMeta(addMeta);

                //Add to content-list.
                contents.add(addItem);

            }

            //-----------------------------------------------------------
            //Initialize this.previousPageItem.
            previousPageItem = new ItemStack(Material.REDSTONE, 1);

            //Create the metadata object.
            ItemMeta previousPageItemMeta = previousPageItem.getItemMeta();

            //Change the object name.
            Component previousItemName = messageHandler.getMessageAsComponent("PreviousPageItemName");
            previousPageItemMeta.displayName(previousItemName);

            //Change the object lore.
            previousPageItemMeta.lore(messageHandler.getMessagesAsComponentList("PreviousPageItemLore"));

            //Assemble metadata back to item.
            previousPageItem.setItemMeta(previousPageItemMeta);
            //-----------------------------------------------------------

            //-----------------------------------------------------------
            //Initialize this.nextPageItem.
            nextPageItem = new ItemStack(Material.GLOWSTONE_DUST, 1);

            //Create the metadata object.
            ItemMeta nextPageItemMeta = nextPageItem.getItemMeta();

            //Change the object name.
            Component nextPageItemName = messageHandler.getMessageAsComponent("NextPageItemName");
            nextPageItemMeta.displayName(nextPageItemName);

            //Change the object lore.
            nextPageItemMeta.lore(messageHandler.getMessagesAsComponentList("NextPageItemLore"));

            //Assemble metadata back to item.
            nextPageItem.setItemMeta(nextPageItemMeta);
            //-----------------------------------------------------------

            //-----------------------------------------------------------
            //Initialize this.resetAllItem.
            resetAllItem = new ItemStack(Material.LAPIS_LAZULI, 1);

            //Create the metadata object.
            ItemMeta resetAllItemMeta = resetAllItem.getItemMeta();

            //Change the object name.
            Component resetItemName = messageHandler.getMessageAsComponent("ResetItemName");
            resetAllItemMeta.displayName(resetItemName);

            //Change the object lore.
            resetAllItemMeta.lore(messageHandler.getMessagesAsComponentList("ResetItemLore"));

            //Assemble metadata back to item.
            resetAllItem.setItemMeta(resetAllItemMeta);
            //-----------------------------------------------------------

            //-----------------------------------------------------------
            //Make the registered listener to listen to this inventory.
            listenerLoader.getAreaInvListener().setMainInv(this);
            //-----------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------
        }

        //Methods
        private ItemStack getPreviousPageItem() {
            return this.previousPageItem;
        }
        private ItemStack getNextPageItem() {
            return this.nextPageItem;
        }
        private ItemStack getResetAllItem() {
            return this.resetAllItem;
        }
        private Inventory getInv() {
            return this.inv;
        }
        private List<ItemStack> getContents() {
            return this.contents;
        }
        private int getPages() {
            return this.pages;
        }
        private Inventory getInvPage(int index) {

            this.inv.clear();

            int invSize = this.inv.getSize()*(index+1);
            int startVal = this.inv.getSize()*index;
            //-----------------------------------------------------------
            changeInvPage(startVal, invSize, index);
            //-----------------------------------------------------------
            return this.inv;
        }
        private void changeInvPage(int startVal, int invSize, int index) {
            for(int i=startVal; i<invSize; i++) {
                if(i<(invSize-9)) {
                    if((i-(index*9))<contents.size()) {
                        this.inv.addItem(contents.get(i-(index*9)));
                    } else {
                        this.inv.addItem(getEmptyItem());
                    }
                } else if (i == (invSize - 9)) {
                    this.inv.addItem(getPreviousPageItem());
                } else if (i > (invSize-9) && i < (invSize - 5)) {
                    this.inv.addItem(getEmptyItem());
                } else if (i == invSize - 5) {
                    this.inv.addItem(getResetAllItem());
                } else if (i > invSize-5 && i < invSize - 1) {
                    this.inv.addItem(getEmptyItem());
                } else {
                    this.inv.addItem(getNextPageItem());
                }
            }
        }

    }

    /**
     * Public Inner Top-Level Class AreaPluginInvListener.
     * -> Listens to events from the GuiInventory.
     * @author lgndluke
     **/
    public class AreaInvListener implements Listener {

        //Attributes
        private final Component noPrevPage = messageHandler.getMessageAsComponent("NoPreviousPage");
        private final Component noNextPage = messageHandler.getMessageAsComponent("NoNextPage");
        private Menu.AreaInventory inv;
        private int index = 0;

        //Listener
        @EventHandler
        public void onAreaInvClickEvent(InventoryClickEvent event) {

            if(this.inv != null) {

                //Ignore possible NullPointerException -> Is covered by if() above!
                if(event.getClickedInventory() == inv.getInv() && event.getCurrentItem() != null) {

                    Player player = (Player) event.getWhoClicked();

                    //Check, if the clicked Item was an Area.
                    //-----------------------------------------------------------
                    if(inv.getContents().contains(event.getCurrentItem())) {
                        String edgedNameString = PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().displayName());
                        String itemName = edgedNameString.substring(1, edgedNameString.length()-1);
                        if(event.getClick().isLeftClick()) {
                            SettingsMenu settings = new SettingsMenu(itemName);
                            listenerLoader.getSettingsInvListener().setSettingsMenu(settings);
                            listenerLoader.getSettingsInvListener().setItemName(itemName);
                            player.openInventory(settings.getInv());
                        } else if (event.getClick().isRightClick()) {
                            ConfirmationMenu confirmation = new ConfirmationMenu();
                            listenerLoader.getConfirmationInvListener().setConfirmationMenu(confirmation);
                            listenerLoader.getConfirmationInvListener().setItemName(itemName);
                            player.openInventory(confirmation.getInv());
                        }
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked Item was next-page.
                    //-----------------------------------------------------------
                    if(inv.getNextPageItem().equals(event.getCurrentItem())) {
                        if(this.index == (inv.getPages()-1)) {
                            this.index = (inv.getPages()-1);
                            player.sendMessage(prefix.append(noNextPage));
                            event.setCancelled(true);
                            return;
                        }
                        inv.getInvPage(++index);
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked Item was reset-all.
                    //-----------------------------------------------------------
                    if(inv.getResetAllItem().equals(event.getCurrentItem())) {
                        areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new Reset.ResetAllAreas(player));
                        inv.getInv().close();
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked Item was previous-page.
                    //-----------------------------------------------------------
                    if(inv.getPreviousPageItem().equals(event.getCurrentItem())) {
                        if(this.index <= 0) {
                            this.index = 0;
                            player.sendMessage(prefix.append(noPrevPage));
                            event.setCancelled(true);
                            return;
                        }
                        inv.getInvPage(--index);
                    }
                    //-----------------------------------------------------------
                    event.setCancelled(true);
                }

            }

        }

        //Method
        private void setMainInv(Menu.AreaInventory inv) {
            this.inv = inv;
        }

        private int getIndex() {
            return this.index;
        }

    }

    /**
     * Private Inner Top-Level Class ConfirmationMenu.
     * -> Represents the graphical in-game menu to accept the removal of an area.
     * @author lgndluke
     **/
    private class ConfirmationMenu {

        //Attributes
        private final ItemStack confirmationItem;
        private final ItemStack cancelItem;
        private final Inventory inv = areaPlugin.getServer().createInventory(null, 9, MiniMessage.miniMessage().deserialize("<blue>Area</blue><gold>Resetter</gold><red>Pro</red>"));

        //Constructor
        private ConfirmationMenu() {
            //-----------------------------------------------------------
            //Initialize confirmationItem.
            confirmationItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta confirmationItemMeta = confirmationItem.getItemMeta();

            //Change the object name.
            confirmationItemMeta.displayName(messageHandler.getMessageAsComponent("ConfirmationItemName"));

            //Change the object lore.
            confirmationItemMeta.lore(messageHandler.getMessagesAsComponentList("ConfirmationItemLore"));

            //Assemble metadata back to item.
            confirmationItem.setItemMeta(confirmationItemMeta);
            //-----------------------------------------------------------

            //-----------------------------------------------------------
            //Initialize cancelItem.
            cancelItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta cancelItemMeta = cancelItem.getItemMeta();

            //Change the object name.
            cancelItemMeta.displayName(messageHandler.getMessageAsComponent("CancelItemName"));

            //Change the object lore.
            cancelItemMeta.lore(messageHandler.getMessagesAsComponentList("CancelItemLore"));

            //Assemble metadata back to item.
            cancelItem.setItemMeta(cancelItemMeta);
            //-----------------------------------------------------------

        }

        //Methods
        private ItemStack getConfirmationItem() {
            return this.confirmationItem;
        }
        private ItemStack getCancelItem()  {
            return this.cancelItem;
        }
        private Inventory getInv() {

            this.inv.clear();
            for(int i=0; i<this.inv.getSize(); i++) {
                if(i == 0) { this.inv.addItem(getConfirmationItem()); }
                else if (i == this.inv.getSize()-1) { this.inv.addItem(getCancelItem()); }
                else { this.inv.addItem(getEmptyItem()); }
            }
            return this.inv;
        }

    }

    /**
     * Public Inner Top-Level Class ConfirmationMenuListener.
     * -> Listens to events from the ConfirmationMenu.
     * @author lgndluke
     **/
    public class ConfirmationMenuListener implements Listener {

        //Attributes
        private ConfirmationMenu confirmationMenu;
        private String itemName;

        //Listener
        @EventHandler
        public void onConfirmationInvClickEvent(InventoryClickEvent event) {

            if(confirmationMenu != null && event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                //Ignore possible NullPointerException -> Is covered by if() above!
                if(event.getClickedInventory() == confirmationMenu.getInv()) {
                    //Check, if the clicked Item was confirmation item.
                    //-----------------------------------------------------------
                    if(confirmationMenu.getConfirmationItem().equals(event.getCurrentItem())) {
                        areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new Remove.Remover(player, itemName));
                        AreaInventory areaInv = new AreaInventory();
                        player.openInventory(areaInv.getInvPage(listenerLoader.getAreaInvListener().getIndex()));
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked Item was cancel item.
                    //-----------------------------------------------------------
                    if(confirmationMenu.getCancelItem().equals(event.getCurrentItem())) {
                        AreaInventory areaInv = new AreaInventory();
                        player.openInventory(areaInv.getInvPage(listenerLoader.getAreaInvListener().getIndex()));
                    }
                    //-----------------------------------------------------------
                    event.setCancelled(true);
                }

            }

        }

        private void setConfirmationMenu(ConfirmationMenu confirmationMenu) {
            this.confirmationMenu = confirmationMenu;
        }

        private void setItemName(String itemName) {
            this.itemName = itemName;
        }

    }

    /**
     * Private Inner Top-Level Class SettingsMenu.
     * -> Represents the graphical in-game menu to edit an Area objects properties.
     * @author lgndluke
     **/
    private class SettingsMenu {

        //Attributes
        private final ItemStack instantResetItem;
        private final ItemStack timerItem;
        private final ItemStack teleportItem;
        private final ItemStack statsItem;
        private final ItemStack backItem;
        private final Inventory inv = areaPlugin.getServer().createInventory(null, 27, MiniMessage.miniMessage().deserialize("<blue>Area</blue><gold>Resetter</gold><red>Pro</red>"));

        //Constructor
        /**
         * Initializes the ItemStack objects of the SettingsMenu.
         **/
        private SettingsMenu(String areaName) {
            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize instantResetItem.
            instantResetItem = new ItemStack(Material.DIAMOND, 1);

            //Create the metadata object.
            ItemMeta instantResetItemMeta = instantResetItem.getItemMeta();

            //Change the object name.
            instantResetItemMeta.displayName(messageHandler.getMessageAsComponent("InstantResetItemName"));

            //Change the object lore.
            instantResetItemMeta.lore(messageHandler.getMessagesAsComponentList("InstantResetItemLore"));

            //Assemble metadata back to item.
            instantResetItem.setItemMeta(instantResetItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize timerItem.
            timerItem = new ItemStack(Material.CLOCK, 1);

            //Create the metadata object.
            ItemMeta timerItemMeta = timerItem.getItemMeta();

            //Change the object name.
            timerItemMeta.displayName(messageHandler.getMessageAsComponent("TimerItemName"));

            //Get necessary database information.
            String sqlAreaUUID = "";
            int sqlTimerValue = 0;
            try {
                ResultSet areaData = databaseHandler.getAreaData(areaName);
                ResultSet areaTimer = databaseHandler.getAreaTimer(UUID.fromString(areaData.getString("uuid")));
                sqlAreaUUID = areaData.getString("uuid");
                sqlTimerValue = areaTimer.getInt("timerValue");
                areaData.close();
                areaTimer.close();
            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            }

            //Change the object lore.
            List<Component> timerItemLoreList = messageHandler.getMessagesAsComponentList("TimerItemLore");
            int timerVal = sqlTimerValue;
            int hours = timerVal / 3600;
            int minutes = (timerVal % 3600) / 60;
            int seconds = timerVal % 60;
            timerItemLoreList.set(2, timerItemLoreList.get(2).appendSpace().append(Component.text(String.format("%02d:%02d:%02d", hours, minutes, seconds))));
            timerItemMeta.lore(timerItemLoreList);

            //Assemble metadata back to item.
            timerItem.setItemMeta(timerItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize teleportItem.
            teleportItem = new ItemStack(Material.ENDER_PEARL, 1);

            //Create the metadata object.
            ItemMeta teleportItemMeta = teleportItem.getItemMeta();

            //Change the object name.
            teleportItemMeta.displayName(messageHandler.getMessageAsComponent("TeleportItemName"));

            //Change the object lore.
            teleportItemMeta.lore(messageHandler.getMessagesAsComponentList("TeleportItemLore"));

            //Assemble metadata back to item.
            teleportItem.setItemMeta(teleportItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize statsItem.
            statsItem = new ItemStack(Material.EMERALD, 1);

            //Create the metadata object.
            ItemMeta statsItemMeta = statsItem.getItemMeta();

            //Change the object name.
            statsItemMeta.displayName(messageHandler.getMessageAsComponent("StatsItemName"));

            //SQL Statements to get necessary information.
            String sqlTimesReset = "";
            String sqlOverallBlocks = "";
            String sqlSavedEntites = "";
            String sqlCreatedOn = "";
            try {
                ResultSet areaStats = databaseHandler.getAreaStats(UUID.fromString(sqlAreaUUID));
                sqlTimesReset = String.valueOf(areaStats.getInt("timesReset"));
                sqlOverallBlocks = String.valueOf(areaStats.getLong("overallBlocks"));
                sqlSavedEntites = String.valueOf(areaStats.getBoolean("entitiesSaved"));
                sqlCreatedOn = String.valueOf(areaStats.getObject("createdOn"));
                areaStats.close();
            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaStats!", se);
            }

            //Change the object lore.
            List<Component> statsItemLoreList = messageHandler.getMessagesAsComponentList("StatsItemLore");
            statsItemLoreList.set(2, statsItemLoreList.get(2).appendSpace().append(Component.text(sqlTimesReset)));
            statsItemLoreList.set(3, statsItemLoreList.get(3).appendSpace().append(Component.text(sqlOverallBlocks)));
            statsItemLoreList.set(4, statsItemLoreList.get(4).appendSpace().append(Component.text(sqlSavedEntites)));
            statsItemLoreList.set(5, statsItemLoreList.get(5).appendSpace().append(Component.text(sqlCreatedOn)));
            statsItemMeta.lore(statsItemLoreList);

            //Assemble metadata back to item.
            statsItem.setItemMeta(statsItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize backItem.
            backItem = new ItemStack(Material.BARRIER, 1);

            //Create the metadata object.
            ItemMeta backItemMeta = backItem.getItemMeta();

            //Change the object name.
            backItemMeta.displayName(messageHandler.getMessageAsComponent("BackItemName"));

            //Change the object lore.
            backItemMeta.lore(messageHandler.getMessagesAsComponentList("BackItemLore"));

            //Assemble metadata back to item.
            backItem.setItemMeta(backItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------
        }

        //Methods
        private ItemStack getInstantResetItem() {
            return instantResetItem;
        }
        private ItemStack getTimerItem() {
            return timerItem;
        }
        private ItemStack getTeleportItem() {
            return teleportItem;
        }
        private ItemStack getStatsItem() {
            return statsItem;
        }
        private ItemStack getBackItem()  {
            return backItem;
        }
        private Inventory getInv() {
            this.inv.clear();
            for(int i=0; i<this.inv.getSize(); i++) {
                if(i==11) { this.inv.addItem(getInstantResetItem()); }
                else if(i==12) { this.inv.addItem(getTimerItem()); }
                else if(i==13) { this.inv.addItem(getTeleportItem()); }
                else if(i==14) { this.inv.addItem(getStatsItem()); }
                else if(i==15) { this.inv.addItem(getBackItem()); }
                else { this.inv.addItem(getEmptyItem()); }
            }
            return this.inv;
        }
        private void updateStatsItem(String areaName) {
            //-----------------------------------------------------------
            //Create the metadata object.
            ItemMeta statsItemMeta = statsItem.getItemMeta();

            //SQL Statements to get necessary information.
            String sqlTimesReset = "";
            String sqlOverallBlocks = "";
            String sqlSavedEntites = "";
            String sqlCreatedOn = "";
            try {
                ResultSet areaData = databaseHandler.getAreaData(areaName);
                ResultSet areaStats = databaseHandler.getAreaStats(UUID.fromString(areaData.getString("uuid")));
                sqlTimesReset = String.valueOf(areaStats.getInt("timesReset"));
                sqlOverallBlocks = String.valueOf(areaStats.getLong("overallBlocks"));
                sqlSavedEntites = String.valueOf(areaStats.getBoolean("entitiesSaved"));
                sqlCreatedOn = String.valueOf(areaStats.getObject("createdOn"));
                areaData.close();
                areaStats.close();
            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaStats!", se);
            }

            //Change the object lore.
            List<Component> statsItemLoreList = messageHandler.getMessagesAsComponentList("StatsItemLore");
            statsItemLoreList.set(2, statsItemLoreList.get(2).appendSpace().append(Component.text(sqlTimesReset)));
            statsItemLoreList.set(3, statsItemLoreList.get(3).appendSpace().append(Component.text(sqlOverallBlocks)));
            statsItemLoreList.set(4, statsItemLoreList.get(4).appendSpace().append(Component.text(sqlSavedEntites)));
            statsItemLoreList.set(5, statsItemLoreList.get(5).appendSpace().append(Component.text(sqlCreatedOn)));
            statsItemMeta.lore(statsItemLoreList);

            //Assemble metadata back to item.
            statsItem.setItemMeta(statsItemMeta);
            //-----------------------------------------------------------
            this.inv.setItem(14, getStatsItem());
        }

    }

    /**
     * Public Inner Top-Level Class SettingsMenuListener.
     * -> Listens to events from SettingsMenu objects.
     * @author lgndluke
     **/
    public class SettingsMenuListener implements Listener {

        //Attributes
        private SettingsMenu settingsMenu;
        private String itemName;

        //Listener
        @EventHandler
        public void onSettingsInvClickEvent(InventoryClickEvent event) {
            if(this.settingsMenu != null && event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                //Ignore possible NullPointerException -> Is covered by if() above!
                if(event.getClickedInventory() == settingsMenu.getInv()) {
                    //Check, if the clicked item was instant reset item.
                    //-----------------------------------------------------------
                    if(settingsMenu.getInstantResetItem().equals(event.getCurrentItem())) {
                        areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, new Reset.ResetArea(player, itemName));
                        this.settingsMenu.updateStatsItem(itemName);
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was timer item.
                    //-----------------------------------------------------------
                    if(settingsMenu.getTimerItem().equals(event.getCurrentItem())) {
                        TimerMenu timerMenu = new TimerMenu(itemName);
                        listenerLoader.getTimerListener().setTimerMenu(timerMenu);
                        listenerLoader.getTimerListener().setItemName(itemName);
                        player.openInventory(timerMenu.getInv());
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was teleport item.
                    //-----------------------------------------------------------
                    if(settingsMenu.getTeleportItem().equals(event.getCurrentItem())) {
                        //Teleport player to saved coordinates and close inventory.
                        //-----------------------------------------------------------
                        try {
                            ResultSet areaData = databaseHandler.getAreaData(itemName);
                            World world = WorldCreator.name(areaData.getString("world")).createWorld();
                            int xVal = areaData.getInt("xValSpawn");
                            int yVal = areaData.getInt("yValSpawn");
                            int zVal = areaData.getInt("zValSpawn");
                            areaData.close();
                            player.teleportAsync(new Location(world, xVal, yVal, zVal));
                            event.setCancelled(true);
                        } catch (SQLException se) {
                            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                        }
                        //-----------------------------------------------------------
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was back item.
                    //-----------------------------------------------------------
                    if(settingsMenu.getBackItem().equals(event.getCurrentItem())) {
                        AreaInventory areaInv = new AreaInventory();
                        player.openInventory(areaInv.getInvPage(listenerLoader.getAreaInvListener().getIndex()));
                    }
                    //-----------------------------------------------------------
                    event.setCancelled(true);
                }

            }

        }

        //Methods
        private void setSettingsMenu(SettingsMenu settingsMenu) {
            this.settingsMenu = settingsMenu;
        }

        private void setItemName(String itemName) {
            this.itemName = itemName;
        }

    }

    /**
     * Private Inner Top-Level class TimerMenu.
     * -> Represents the graphical in-game menu to edit an Area objects reset timer.
     * @author lgndluke
     **/
    private class TimerMenu {

        //Attributes
        private final ItemStack smallIncreaseItem;
        private final ItemStack mediumIncreaseItem;
        private final ItemStack largeIncreaseItem;
        private final ItemStack displayTimerItem;
        private final ItemStack smallDecreaseItem;
        private final ItemStack mediumDecreaseItem;
        private final ItemStack largeDecreaseItem;
        private final ItemStack backItem;
        private final Inventory inv = areaPlugin.getServer().createInventory(null, 27, MiniMessage.miniMessage().deserialize("<blue>Area</blue><gold>Resetter</gold><red>Pro</red>"));


        //Constructor
        private TimerMenu(String areaName) {
            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize smallIncreaseItem.
            smallIncreaseItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta smallIncreaseItemMeta = smallIncreaseItem.getItemMeta();

            //Change the object name.
            smallIncreaseItemMeta.displayName(messageHandler.getMessageAsComponent("SmallIncreaseItemName"));

            //Change the object lore.
            smallIncreaseItemMeta.lore(messageHandler.getMessagesAsComponentList("SmallIncreaseItemLore"));

            //Assemble metadata back to item.
            smallIncreaseItem.setItemMeta(smallIncreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize mediumIncreaseItem.
            mediumIncreaseItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta mediumIncreaseItemMeta = mediumIncreaseItem.getItemMeta();

            //Change the object name.
            mediumIncreaseItemMeta.displayName(messageHandler.getMessageAsComponent("MediumIncreaseItemName"));

            //Change the object lore.
            mediumIncreaseItemMeta.lore(messageHandler.getMessagesAsComponentList("MediumIncreaseItemLore"));

            //Assemble metadata back to item.
            mediumIncreaseItem.setItemMeta(mediumIncreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize largeIncreaseItem.
            largeIncreaseItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta largeIncreaseItemMeta = largeIncreaseItem.getItemMeta();

            //Change the object name.
            largeIncreaseItemMeta.displayName(messageHandler.getMessageAsComponent("LargeIncreaseItemName"));

            //Change the object lore.
            largeIncreaseItemMeta.lore(messageHandler.getMessagesAsComponentList("LargeIncreaseItemLore"));

            //Assemble metadata back to item.
            largeIncreaseItem.setItemMeta(largeIncreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize displayTimerItem.
            displayTimerItem = new ItemStack(Material.NETHER_STAR, 1);

            //Create the metadata object.
            ItemMeta displayTimerItemMeta = displayTimerItem.getItemMeta();

            //Change the object name.
            displayTimerItemMeta.displayName(messageHandler.getMessageAsComponent("DisplayTimerItemName"));

            //SQL Statements to get necessary information.
            int timerVal = 0;
            try {
                ResultSet areaData = databaseHandler.getAreaData(areaName);
                ResultSet areaTimer = databaseHandler.getAreaTimer(UUID.fromString(areaData.getString("uuid")));
                timerVal = areaTimer.getInt("timerValue");
                areaData.close();
                areaTimer.close();
            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            }

            //Change the object lore.
            List<Component> displayTimerItemLoreList = messageHandler.getMessagesAsComponentList("DisplayTimerItemLore");
            int hours = timerVal / 3600;
            int minutes = (timerVal % 3600) / 60;
            int seconds = timerVal % 60;
            displayTimerItemLoreList.set(2, displayTimerItemLoreList.get(2).appendSpace().append(Component.text(String.format("%02d:%02d:%02d", hours, minutes, seconds))));
            displayTimerItemMeta.lore(displayTimerItemLoreList);

            //Assemble metadata back to item.
            displayTimerItem.setItemMeta(displayTimerItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize smallDecreaseItem.
            smallDecreaseItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta smallDecreaseItemMeta = smallDecreaseItem.getItemMeta();

            //Change the object name.
            smallDecreaseItemMeta.displayName(messageHandler.getMessageAsComponent("SmallDecreaseItemName"));

            //Change the object lore.
            smallDecreaseItemMeta.lore(messageHandler.getMessagesAsComponentList("SmallDecreaseItemLore"));

            //Assemble metadata back to item.
            smallDecreaseItem.setItemMeta(smallDecreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize mediumDecreaseItem.
            mediumDecreaseItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta mediumDecreaseItemMeta = mediumDecreaseItem.getItemMeta();

            //Change the object name.
            mediumDecreaseItemMeta.displayName(messageHandler.getMessageAsComponent("MediumDecreaseItemName"));

            //Change the object lore.
            mediumDecreaseItemMeta.lore(messageHandler.getMessagesAsComponentList("MediumDecreaseItemLore"));

            //Assemble metadata back to item.
            mediumDecreaseItem.setItemMeta(mediumDecreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize largeDecreaseItem.
            largeDecreaseItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta largeDecreaseItemMeta = largeDecreaseItem.getItemMeta();

            //Change the object name.
            largeDecreaseItemMeta.displayName(messageHandler.getMessageAsComponent("LargeDecreaseItemName"));

            //Change the object lore.
            largeDecreaseItemMeta.lore(messageHandler.getMessagesAsComponentList("LargeDecreaseItemLore"));

            //Assemble metadata back to item.
            largeDecreaseItem.setItemMeta(largeDecreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize backItem.
            backItem = new ItemStack(Material.BARRIER, 1);

            //Create the metadata object.
            ItemMeta backItemMeta = backItem.getItemMeta();

            //Change the object name.
            backItemMeta.displayName(messageHandler.getMessageAsComponent("BackItemName"));

            //Change the object lore.
            backItemMeta.lore(messageHandler.getMessagesAsComponentList("BackItemLore"));

            //Assemble metadata back to item.
            backItem.setItemMeta(backItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

        }

        //Methods
        private ItemStack getSmallIncreaseItem() { return smallIncreaseItem; }
        private ItemStack getMediumIncreaseItem() { return mediumIncreaseItem; }
        private ItemStack getLargeIncreaseItem() {
            return largeIncreaseItem;
        }
        private ItemStack getDisplayTimerItem() {
            return displayTimerItem;
        }
        private ItemStack getSmallDecreaseItem() { return smallDecreaseItem; }
        private ItemStack getMediumDecreaseItem() { return mediumDecreaseItem; }
        private ItemStack getLargeDecreaseItem() { return largeDecreaseItem; }
        private ItemStack getBackItem()  {
            return backItem;
        }
        private Inventory getInv() {
            this.inv.clear();
            for(int i=0; i<this.inv.getSize(); i++) {
                if(i==10) {this.inv.addItem(getSmallIncreaseItem());}
                else if(i==11) {this.inv.addItem(getMediumIncreaseItem());}
                else if(i==12) {this.inv.addItem(getLargeIncreaseItem());}
                else if(i==13) {this.inv.addItem(getDisplayTimerItem());}
                else if(i==14) {this.inv.addItem(getSmallDecreaseItem());}
                else if(i==15) {this.inv.addItem(getMediumDecreaseItem());}
                else if(i==16) {this.inv.addItem(getLargeDecreaseItem());}
                else if(i==this.inv.getSize()-1) {this.inv.addItem(getBackItem());}
                else {this.inv.addItem(getEmptyItem());}
            }
            return this.inv;
        }
        private void updateDisplayItem(UUID uuid) {
            //-----------------------------------------------------------
            //Create the metadata object.
            ItemMeta displayTimerItemMeta = displayTimerItem.getItemMeta();

            //SQL Statements to get necessary information.
            int timerVal = 0;
            try {
                ResultSet areaTimer = databaseHandler.getAreaTimer(uuid);
                timerVal = areaTimer.getInt("timerValue");
                areaTimer.close();
            } catch (SQLException se) {
                areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
            }


            //Change the object lore.
            List<Component> displayTimerItemLoreList = messageHandler.getMessagesAsComponentList("DisplayTimerItemLore");
            int hours = timerVal / 3600;
            int minutes = (timerVal % 3600) / 60;
            int seconds = timerVal % 60;
            displayTimerItemLoreList.set(2, displayTimerItemLoreList.get(2).appendSpace().append(Component.text(String.format("%02d:%02d:%02d", hours, minutes, seconds))));
            displayTimerItemMeta.lore(displayTimerItemLoreList);

            //Assemble metadata back to item.
            displayTimerItem.setItemMeta(displayTimerItemMeta);
            //-----------------------------------------------------------
            this.inv.setItem(13, getDisplayTimerItem());
        }

    }

    /**
     * Public Inner Top-Level Class TimerMenuListener.
     * -> Listens to events from TimerMenu objects.
     * @author lgndluke
     **/
    public class TimerMenuListener implements Listener {

        //Attributes
        private TimerMenu timerMenu;
        private String itemName;

        //Listener
        @EventHandler
        public void onTimerInvClickEvent(InventoryClickEvent event) {

            if(this.timerMenu != null && event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                //Ignore possible NullPointerException -> Is covered by if() above!
                if(event.getClickedInventory() == timerMenu.getInv()) {
                    //Check, if the clicked item was small increase item.
                    //-----------------------------------------------------------
                    if(timerMenu.getSmallIncreaseItem().equals(event.getCurrentItem())) {
                        try {
                            ResultSet areaData = databaseHandler.getAreaData(itemName);
                            ResultSet areaTimer = databaseHandler.getAreaTimer(UUID.fromString(areaData.getString("uuid")));
                            int currTimerVal = areaTimer.getInt("timerValue");
                            int smallIncVal = (int) configHandler.get("SmallIncreaseValue");
                            int updateVal = currTimerVal+smallIncVal;
                            databaseHandler.updateAreaTimerTimerValue(UUID.fromString(areaData.getString("uuid")), updateVal);
                            autoResetHandler.updateAreaResetInterval(itemName, updateVal);
                            this.timerMenu.updateDisplayItem(UUID.fromString(areaData.getString("uuid")));
                            areaData.close();
                            areaTimer.close();
                        } catch (SQLException se) {
                            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                        }
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was medium increase item.
                    //-----------------------------------------------------------
                    if(timerMenu.getMediumIncreaseItem().equals(event.getCurrentItem())) {
                        try {
                            ResultSet areaData = databaseHandler.getAreaData(itemName);
                            ResultSet areaTimer = databaseHandler.getAreaTimer(UUID.fromString(areaData.getString("uuid")));
                            int currTimerVal = areaTimer.getInt("timerValue");
                            int mediumIncVal = (int) configHandler.get("MediumIncreaseValue");
                            int updateVal = currTimerVal+mediumIncVal;
                            databaseHandler.updateAreaTimerTimerValue(UUID.fromString(areaData.getString("uuid")), updateVal);
                            autoResetHandler.updateAreaResetInterval(itemName, updateVal);
                            this.timerMenu.updateDisplayItem(UUID.fromString(areaData.getString("uuid")));
                            areaData.close();
                            areaTimer.close();
                        } catch (SQLException se) {
                            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                        }
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was large increase item.
                    //-----------------------------------------------------------
                    if(timerMenu.getLargeIncreaseItem().equals(event.getCurrentItem())) {
                        try {
                            ResultSet areaData = databaseHandler.getAreaData(itemName);
                            ResultSet areaTimer = databaseHandler.getAreaTimer(UUID.fromString(areaData.getString("uuid")));
                            int currTimerVal = areaTimer.getInt("timerValue");
                            int largeIncVal = (int) configHandler.get("LargeIncreaseValue");
                            int updateVal = currTimerVal+largeIncVal;
                            databaseHandler.updateAreaTimerTimerValue(UUID.fromString(areaData.getString("uuid")), updateVal);
                            autoResetHandler.updateAreaResetInterval(itemName, updateVal);
                            this.timerMenu.updateDisplayItem(UUID.fromString(areaData.getString("uuid")));
                            areaData.close();
                            areaTimer.close();
                        } catch (SQLException se) {
                            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                        }
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was small decrease item.
                    //-----------------------------------------------------------
                    if(timerMenu.getSmallDecreaseItem().equals(event.getCurrentItem())) {
                        try {
                            ResultSet areaData = databaseHandler.getAreaData(itemName);
                            ResultSet areaTimer = databaseHandler.getAreaTimer(UUID.fromString(areaData.getString("uuid")));
                            int currTimerVal = areaTimer.getInt("timerValue");
                            int smallDecVal = (int) configHandler.get("SmallDecreaseValue");
                            int updateVal = currTimerVal-smallDecVal;
                            databaseHandler.updateAreaTimerTimerValue(UUID.fromString(areaData.getString("uuid")), updateVal);
                            autoResetHandler.updateAreaResetInterval(itemName, updateVal);
                            this.timerMenu.updateDisplayItem(UUID.fromString(areaData.getString("uuid")));
                            areaData.close();
                            areaTimer.close();
                        } catch (SQLException se) {
                            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                        }
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was medium decrease item.
                    //-----------------------------------------------------------
                    if(timerMenu.getMediumDecreaseItem().equals(event.getCurrentItem())) {
                        try {
                            ResultSet areaData = databaseHandler.getAreaData(itemName);
                            ResultSet areaTimer = databaseHandler.getAreaTimer(UUID.fromString(areaData.getString("uuid")));
                            int currTimerVal = areaTimer.getInt("timerValue");
                            int mediumDecVal = (int) configHandler.get("MediumDecreaseValue");
                            int updateVal = currTimerVal-mediumDecVal;
                            databaseHandler.updateAreaTimerTimerValue(UUID.fromString(areaData.getString("uuid")), updateVal);
                            autoResetHandler.updateAreaResetInterval(itemName, updateVal);
                            this.timerMenu.updateDisplayItem(UUID.fromString(areaData.getString("uuid")));
                            areaData.close();
                            areaTimer.close();
                        } catch (SQLException se) {
                            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                        }
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was large decrease item.
                    //-----------------------------------------------------------
                    if(timerMenu.getLargeDecreaseItem().equals(event.getCurrentItem())) {
                        try {
                            ResultSet areaData = databaseHandler.getAreaData(itemName);
                            ResultSet areaTimer = databaseHandler.getAreaTimer(UUID.fromString(areaData.getString("uuid")));
                            int currTimerVal = areaTimer.getInt("timerValue");
                            int largeDecVal = (int) configHandler.get("LargeDecreaseValue");
                            int updateVal = currTimerVal-largeDecVal;
                            databaseHandler.updateAreaTimerTimerValue(UUID.fromString(areaData.getString("uuid")), updateVal);
                            autoResetHandler.updateAreaResetInterval(itemName, updateVal);
                            this.timerMenu.updateDisplayItem(UUID.fromString(areaData.getString("uuid")));
                            areaData.close();
                            areaTimer.close();
                        } catch (SQLException se) {
                            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                        }
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was back item.
                    //-----------------------------------------------------------
                    if(timerMenu.getBackItem().equals(event.getCurrentItem())) {
                        SettingsMenu settings = new SettingsMenu(itemName);
                        listenerLoader.getSettingsInvListener().setSettingsMenu(settings);
                        listenerLoader.getSettingsInvListener().setItemName(itemName);
                        player.openInventory(settings.getInv());
                    }
                    //-----------------------------------------------------------
                    event.setCancelled(true);
                }

            }

        }

        //Methods
        private void setTimerMenu(TimerMenu timerMenu) {
            this.timerMenu = timerMenu;
        }
        private void setItemName(String itemName) {
            this.itemName = itemName;
        }

    }

}
