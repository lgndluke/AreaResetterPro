package com.lgndluke.arearesetterpro.commands;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.AutoResetHandler;
import com.lgndluke.arearesetterpro.data.ConfigHandler;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.arearesetterpro.data.MessageHandler;
import com.lgndluke.arearesetterpro.loaders.ListenerLoader;
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
    private static final Component prefix = MessageHandler.getMessageAsComponent("Prefix");
    private final Component noPermission = MessageHandler.getMessageAsComponent("NoPermission");
    private final String executedByConsole = MessageHandler.getMessageAsString("ExecutedByConsole");

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
    private static ItemStack getEmptyItem() {
        ItemStack emptyItem = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
        //-----------------------------------------------------------
        //Initialize emptyItem.

        //Create the metadata object.
        ItemMeta emptyItemMeta = emptyItem.getItemMeta();

        //Change the object name.
        emptyItemMeta.displayName(MessageHandler.getMessageAsComponent("EmptyItemName"));

        //Change the object lore.
        emptyItemMeta.lore(MessageHandler.getMessagesAsComponentList("EmptyItemLore"));

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
    private static class AreaInventory {

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

            //Declare SQL-Statements
            String sqlCount = "SELECT COUNT(uuid) FROM AreaData;";
            String sqlNames = "SELECT areaName FROM AreaData;";

            //Get results via DatabaseHandler.executeQuery();
            int count = Integer.parseInt(DatabaseHandler.executeQuery(sqlCount).get(0));
            List<String> names = DatabaseHandler.executeQuery(sqlNames);

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
                addMeta.lore(MessageHandler.getMessagesAsComponentList("AreaItemLore"));

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
            Component previousItemName = MessageHandler.getMessageAsComponent("PreviousPageItemName");
            previousPageItemMeta.displayName(previousItemName);

            //Change the object lore.
            previousPageItemMeta.lore(MessageHandler.getMessagesAsComponentList("PreviousPageItemLore"));

            //Assemble metadata back to item.
            previousPageItem.setItemMeta(previousPageItemMeta);
            //-----------------------------------------------------------

            //-----------------------------------------------------------
            //Initialize this.nextPageItem.
            nextPageItem = new ItemStack(Material.GLOWSTONE_DUST, 1);

            //Create the metadata object.
            ItemMeta nextPageItemMeta = nextPageItem.getItemMeta();

            //Change the object name.
            Component nextPageItemName = MessageHandler.getMessageAsComponent("NextPageItemName");
            nextPageItemMeta.displayName(nextPageItemName);

            //Change the object lore.
            nextPageItemMeta.lore(MessageHandler.getMessagesAsComponentList("NextPageItemLore"));

            //Assemble metadata back to item.
            nextPageItem.setItemMeta(nextPageItemMeta);
            //-----------------------------------------------------------

            //-----------------------------------------------------------
            //Initialize this.resetAllItem.
            resetAllItem = new ItemStack(Material.LAPIS_LAZULI, 1);

            //Create the metadata object.
            ItemMeta resetAllItemMeta = resetAllItem.getItemMeta();

            //Change the object name.
            Component resetItemName = MessageHandler.getMessageAsComponent("ResetItemName");
            resetAllItemMeta.displayName(resetItemName);

            //Change the object lore.
            resetAllItemMeta.lore(MessageHandler.getMessagesAsComponentList("ResetItemLore"));

            //Assemble metadata back to item.
            resetAllItem.setItemMeta(resetAllItemMeta);
            //-----------------------------------------------------------

            //-----------------------------------------------------------
            //Make the registered listener to listen to this inventory.
            ListenerLoader.getAreaInvListener().setMainInv(this);
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
    public static class AreaInvListener implements Listener {

        //Attributes
        private final Component noPrevPage = MessageHandler.getMessageAsComponent("NoPreviousPage");
        private final Component noNextPage = MessageHandler.getMessageAsComponent("NoNextPage");
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
                            ListenerLoader.getSettingsInvListener().setSettingsMenu(settings);
                            ListenerLoader.getSettingsInvListener().setItemName(itemName);
                            player.openInventory(settings.getInv());
                        } else if (event.getClick().isRightClick()) {
                            ConfirmationMenu confirmation = new ConfirmationMenu();
                            ListenerLoader.getConfirmationInvListener().setConfirmationMenu(confirmation);
                            ListenerLoader.getConfirmationInvListener().setItemName(itemName);
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
    private static class ConfirmationMenu {

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
            confirmationItemMeta.displayName(MessageHandler.getMessageAsComponent("ConfirmationItemName"));

            //Change the object lore.
            confirmationItemMeta.lore(MessageHandler.getMessagesAsComponentList("ConfirmationItemLore"));

            //Assemble metadata back to item.
            confirmationItem.setItemMeta(confirmationItemMeta);
            //-----------------------------------------------------------

            //-----------------------------------------------------------
            //Initialize cancelItem.
            cancelItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta cancelItemMeta = cancelItem.getItemMeta();

            //Change the object name.
            cancelItemMeta.displayName(MessageHandler.getMessageAsComponent("CancelItemName"));

            //Change the object lore.
            cancelItemMeta.lore(MessageHandler.getMessagesAsComponentList("CancelItemLore"));

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
    public static class ConfirmationMenuListener implements Listener {

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
                        player.openInventory(areaInv.getInvPage(ListenerLoader.getAreaInvListener().getIndex()));
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked Item was cancel item.
                    //-----------------------------------------------------------
                    if(confirmationMenu.getCancelItem().equals(event.getCurrentItem())) {
                        AreaInventory areaInv = new AreaInventory();
                        player.openInventory(areaInv.getInvPage(ListenerLoader.getAreaInvListener().getIndex()));
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
    private static class SettingsMenu {

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
            instantResetItemMeta.displayName(MessageHandler.getMessageAsComponent("InstantResetItemName"));

            //Change the object lore.
            instantResetItemMeta.lore(MessageHandler.getMessagesAsComponentList("InstantResetItemLore"));

            //Assemble metadata back to item.
            instantResetItem.setItemMeta(instantResetItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize timerItem.
            timerItem = new ItemStack(Material.CLOCK, 1);

            //Create the metadata object.
            ItemMeta timerItemMeta = timerItem.getItemMeta();

            //Change the object name.
            timerItemMeta.displayName(MessageHandler.getMessageAsComponent("TimerItemName"));

            //SQL Statements to get necessary information.
            String sqlAreaUUID = "SELECT uuid FROM AreaData WHERE areaName = '" + areaName + "';";
            UUID uuid = UUID.fromString(DatabaseHandler.executeQuery(sqlAreaUUID).get(0));
            String sqlTimerValue = "SELECT timerValue FROM AreaTimer WHERE uuid = '" + uuid + "';";

            //Change the object lore.
            List<Component> timerItemLoreList = MessageHandler.getMessagesAsComponentList("TimerItemLore");
            int timerVal = Integer.parseInt(DatabaseHandler.executeQuery(sqlTimerValue).get(0));
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
            teleportItemMeta.displayName(MessageHandler.getMessageAsComponent("TeleportItemName"));

            //Change the object lore.
            teleportItemMeta.lore(MessageHandler.getMessagesAsComponentList("TeleportItemLore"));

            //Assemble metadata back to item.
            teleportItem.setItemMeta(teleportItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize statsItem.
            statsItem = new ItemStack(Material.EMERALD, 1);

            //Create the metadata object.
            ItemMeta statsItemMeta = statsItem.getItemMeta();

            //Change the object name.
            statsItemMeta.displayName(MessageHandler.getMessageAsComponent("StatsItemName"));

            //SQL Statements to get necessary information.
            String sqlTimesReset = "SELECT timesReset FROM AreaStats WHERE uuid = '" + uuid + "';";
            String sqlOverallBlocks = "SELECT overallBlocks FROM AreaStats WHERE uuid = '" + uuid + "';";
            String sqlCreatedOn = "SELECT createdOn FROM AreaStats WHERE uuid = '" + uuid + "';";

            //Change the object lore.
            List<Component> statsItemLoreList = MessageHandler.getMessagesAsComponentList("StatsItemLore");
            statsItemLoreList.set(2, statsItemLoreList.get(2).appendSpace().append(Component.text(DatabaseHandler.executeQuery(sqlTimesReset).get(0))));
            statsItemLoreList.set(3, statsItemLoreList.get(3).appendSpace().append(Component.text(DatabaseHandler.executeQuery(sqlOverallBlocks).get(0))));
            statsItemLoreList.set(4, statsItemLoreList.get(4).appendSpace().append(Component.text(DatabaseHandler.executeQuery(sqlCreatedOn).get(0))));
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
            backItemMeta.displayName(MessageHandler.getMessageAsComponent("BackItemName"));

            //Change the object lore.
            backItemMeta.lore(MessageHandler.getMessagesAsComponentList("BackItemLore"));

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
            String sqlAreaUUID = "SELECT uuid FROM AreaData WHERE areaName = '" + areaName + "';";
            UUID uuid = UUID.fromString(DatabaseHandler.executeQuery(sqlAreaUUID).get(0));
            String sqlTimesReset = "SELECT timesReset FROM AreaStats WHERE uuid = '" + uuid + "';";
            String sqlOverallBlocks = "SELECT overallBlocks FROM AreaStats WHERE uuid = '" + uuid + "';";
            String sqlCreatedOn = "SELECT createdOn FROM AreaStats WHERE uuid = '" + uuid + "';";

            //Change the object lore.
            List<Component> statsItemLoreList = MessageHandler.getMessagesAsComponentList("StatsItemLore");
            statsItemLoreList.set(2, statsItemLoreList.get(2).appendSpace().append(Component.text(DatabaseHandler.executeQuery(sqlTimesReset).get(0))));
            statsItemLoreList.set(3, statsItemLoreList.get(3).appendSpace().append(Component.text(DatabaseHandler.executeQuery(sqlOverallBlocks).get(0))));
            statsItemLoreList.set(4, statsItemLoreList.get(4).appendSpace().append(Component.text(DatabaseHandler.executeQuery(sqlCreatedOn).get(0))));
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
    public static class SettingsMenuListener implements Listener {

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
                        ListenerLoader.getTimerListener().setTimerMenu(timerMenu);
                        ListenerLoader.getTimerListener().setItemName(itemName);
                        player.openInventory(timerMenu.getInv());
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was teleport item.
                    //-----------------------------------------------------------
                    if(settingsMenu.getTeleportItem().equals(event.getCurrentItem())) {
                        //Teleport player to saved coordinates and close inventory.
                        //-----------------------------------------------------------
                        String sql_world = "SELECT world FROM AreaData WHERE areaName = '" + itemName + "';";
                        String sql_x = "SELECT x FROM AreaData WHERE areaName = '" + itemName + "';";
                        String sql_y = "SELECT y FROM AreaData WHERE areaName = '" + itemName + "';";
                        String sql_z = "SELECT z FROM AreaData WHERE areaName = '" + itemName + "';";
                        World world = WorldCreator.name(DatabaseHandler.executeQuery(sql_world).get(0)).createWorld();
                        int xVal = Integer.parseInt(DatabaseHandler.executeQuery(sql_x).get(0));
                        int yVal = Integer.parseInt(DatabaseHandler.executeQuery(sql_y).get(0));
                        int zVal = Integer.parseInt(DatabaseHandler.executeQuery(sql_z).get(0));
                        player.teleportAsync(new Location(world, xVal, yVal, zVal));
                        event.setCancelled(true);
                        //-----------------------------------------------------------
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was back item.
                    //-----------------------------------------------------------
                    if(settingsMenu.getBackItem().equals(event.getCurrentItem())) {
                        AreaInventory areaInv = new AreaInventory();
                        player.openInventory(areaInv.getInvPage(ListenerLoader.getAreaInvListener().getIndex()));
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
    private static class TimerMenu {

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
            smallIncreaseItemMeta.displayName(MessageHandler.getMessageAsComponent("SmallIncreaseItemName"));

            //Change the object lore.
            smallIncreaseItemMeta.lore(MessageHandler.getMessagesAsComponentList("SmallIncreaseItemLore"));

            //Assemble metadata back to item.
            smallIncreaseItem.setItemMeta(smallIncreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize mediumIncreaseItem.
            mediumIncreaseItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta mediumIncreaseItemMeta = mediumIncreaseItem.getItemMeta();

            //Change the object name.
            mediumIncreaseItemMeta.displayName(MessageHandler.getMessageAsComponent("MediumIncreaseItemName"));

            //Change the object lore.
            mediumIncreaseItemMeta.lore(MessageHandler.getMessagesAsComponentList("MediumIncreaseItemLore"));

            //Assemble metadata back to item.
            mediumIncreaseItem.setItemMeta(mediumIncreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize largeIncreaseItem.
            largeIncreaseItem = new ItemStack(Material.GREEN_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta largeIncreaseItemMeta = largeIncreaseItem.getItemMeta();

            //Change the object name.
            largeIncreaseItemMeta.displayName(MessageHandler.getMessageAsComponent("LargeIncreaseItemName"));

            //Change the object lore.
            largeIncreaseItemMeta.lore(MessageHandler.getMessagesAsComponentList("LargeIncreaseItemLore"));

            //Assemble metadata back to item.
            largeIncreaseItem.setItemMeta(largeIncreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize displayTimerItem.
            displayTimerItem = new ItemStack(Material.NETHER_STAR, 1);

            //Create the metadata object.
            ItemMeta displayTimerItemMeta = displayTimerItem.getItemMeta();

            //Change the object name.
            displayTimerItemMeta.displayName(MessageHandler.getMessageAsComponent("DisplayTimerItemName"));

            //SQL Statements to get necessary information.
            String sqlAreaUUID = "SELECT uuid FROM AreaData WHERE areaName = '" + areaName + "';";
            UUID uuid = UUID.fromString(DatabaseHandler.executeQuery(sqlAreaUUID).get(0));
            String sqlTimerValue = "SELECT timerValue FROM AreaTimer WHERE uuid = '" + uuid + "';";

            //Change the object lore.
            List<Component> displayTimerItemLoreList = MessageHandler.getMessagesAsComponentList("DisplayTimerItemLore");
            int timerVal = Integer.parseInt(DatabaseHandler.executeQuery(sqlTimerValue).get(0));
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
            smallDecreaseItemMeta.displayName(MessageHandler.getMessageAsComponent("SmallDecreaseItemName"));

            //Change the object lore.
            smallDecreaseItemMeta.lore(MessageHandler.getMessagesAsComponentList("SmallDecreaseItemLore"));

            //Assemble metadata back to item.
            smallDecreaseItem.setItemMeta(smallDecreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize mediumDecreaseItem.
            mediumDecreaseItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta mediumDecreaseItemMeta = mediumDecreaseItem.getItemMeta();

            //Change the object name.
            mediumDecreaseItemMeta.displayName(MessageHandler.getMessageAsComponent("MediumDecreaseItemName"));

            //Change the object lore.
            mediumDecreaseItemMeta.lore(MessageHandler.getMessagesAsComponentList("MediumDecreaseItemLore"));

            //Assemble metadata back to item.
            mediumDecreaseItem.setItemMeta(mediumDecreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize largeDecreaseItem.
            largeDecreaseItem = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);

            //Create the metadata object.
            ItemMeta largeDecreaseItemMeta = largeDecreaseItem.getItemMeta();

            //Change the object name.
            largeDecreaseItemMeta.displayName(MessageHandler.getMessageAsComponent("LargeDecreaseItemName"));

            //Change the object lore.
            largeDecreaseItemMeta.lore(MessageHandler.getMessagesAsComponentList("LargeDecreaseItemLore"));

            //Assemble metadata back to item.
            largeDecreaseItem.setItemMeta(largeDecreaseItemMeta);
            //-----------------------------------------------------------------------------------------------------------------------------

            //-----------------------------------------------------------------------------------------------------------------------------
            //Initialize backItem.
            backItem = new ItemStack(Material.BARRIER, 1);

            //Create the metadata object.
            ItemMeta backItemMeta = backItem.getItemMeta();

            //Change the object name.
            backItemMeta.displayName(MessageHandler.getMessageAsComponent("BackItemName"));

            //Change the object lore.
            backItemMeta.lore(MessageHandler.getMessagesAsComponentList("BackItemLore"));

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
        private void updateDisplayItem(UUID areaID) {
            //-----------------------------------------------------------
            //Create the metadata object.
            ItemMeta displayTimerItemMeta = displayTimerItem.getItemMeta();

            //SQL Statements to get necessary information.
            String sqlTimerValue = "SELECT timerValue FROM AreaTimer WHERE uuid = '" + areaID + "';";

            //Change the object lore.
            List<Component> displayTimerItemLoreList = MessageHandler.getMessagesAsComponentList("DisplayTimerItemLore");
            int timerVal = Integer.parseInt(DatabaseHandler.executeQuery(sqlTimerValue).get(0));
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
    public static class TimerMenuListener implements Listener {

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
                        String sqlSelectUUID = "SELECT uuid FROM AreaData WHERE areaName = '" + itemName + "';";
                        UUID areaID = UUID.fromString(DatabaseHandler.executeQuery(sqlSelectUUID).get(0));
                        String sqlGetCurrTimerVal = "SELECT timerValue FROM AreaTimer WHERE uuid = '" + areaID + "';";
                        int currTimerVal = Integer.parseInt(DatabaseHandler.executeQuery(sqlGetCurrTimerVal).get(0));
                        int smallIncVal = (int) ConfigHandler.get("SmallIncreaseValue");
                        int updateVal = currTimerVal+smallIncVal;
                        String sqlInsertChange = "UPDATE AreaTimer SET timerValue = " + updateVal + " WHERE uuid = '" + areaID + "';";
                        DatabaseHandler.execute(sqlInsertChange);
                        AutoResetHandler.updateAreaResetInterval(itemName, updateVal);
                        this.timerMenu.updateDisplayItem(areaID);
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was medium increase item.
                    //-----------------------------------------------------------
                    if(timerMenu.getMediumIncreaseItem().equals(event.getCurrentItem())) {
                        String sqlSelectUUID = "SELECT uuid FROM AreaData WHERE areaName = '" + itemName + "';";
                        UUID areaID = UUID.fromString(DatabaseHandler.executeQuery(sqlSelectUUID).get(0));
                        String sqlGetCurrTimerVal = "SELECT timerValue FROM AreaTimer WHERE uuid = '" + areaID + "';";
                        int currTimerVal = Integer.parseInt(DatabaseHandler.executeQuery(sqlGetCurrTimerVal).get(0));
                        int mediumIncVal = (int) ConfigHandler.get("MediumIncreaseValue");
                        int updateVal = currTimerVal+mediumIncVal;
                        String sqlInsertChange = "UPDATE AreaTimer SET timerValue = " + updateVal + " WHERE uuid = '" + areaID + "';";
                        DatabaseHandler.execute(sqlInsertChange);
                        AutoResetHandler.updateAreaResetInterval(itemName, updateVal);
                        this.timerMenu.updateDisplayItem(areaID);
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was large increase item.
                    //-----------------------------------------------------------
                    if(timerMenu.getLargeIncreaseItem().equals(event.getCurrentItem())) {
                        String sqlSelectUUID = "SELECT uuid FROM AreaData WHERE areaName = '" + itemName + "';";
                        UUID areaID = UUID.fromString(DatabaseHandler.executeQuery(sqlSelectUUID).get(0));
                        String sqlGetCurrTimerVal = "SELECT timerValue FROM AreaTimer WHERE uuid = '" + areaID + "';";
                        int currTimerVal = Integer.parseInt(DatabaseHandler.executeQuery(sqlGetCurrTimerVal).get(0));
                        int largeIncVal = (int) ConfigHandler.get("LargeIncreaseValue");
                        int updateVal = currTimerVal+largeIncVal;
                        String sqlInsertChange = "UPDATE AreaTimer SET timerValue = " + updateVal + " WHERE uuid = '" + areaID + "';";
                        DatabaseHandler.execute(sqlInsertChange);
                        AutoResetHandler.updateAreaResetInterval(itemName, updateVal);
                        this.timerMenu.updateDisplayItem(areaID);
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was small decrease item.
                    //-----------------------------------------------------------
                    if(timerMenu.getSmallDecreaseItem().equals(event.getCurrentItem())) {
                        String sqlSelectUUID = "SELECT uuid FROM AreaData WHERE areaName = '" + itemName + "';";
                        UUID areaID = UUID.fromString(DatabaseHandler.executeQuery(sqlSelectUUID).get(0));
                        String sqlGetCurrTimerVal = "SELECT timerValue FROM AreaTimer WHERE uuid = '" + areaID + "';";
                        int currTimerVal = Integer.parseInt(DatabaseHandler.executeQuery(sqlGetCurrTimerVal).get(0));
                        int smallDecVal = (int) ConfigHandler.get("SmallDecreaseValue");
                        int updateVal = currTimerVal-smallDecVal;
                        String sqlInsertChange = "UPDATE AreaTimer SET timerValue = " + updateVal + " WHERE uuid = '" + areaID + "';";
                        DatabaseHandler.execute(sqlInsertChange);
                        AutoResetHandler.updateAreaResetInterval(itemName, updateVal);
                        this.timerMenu.updateDisplayItem(areaID);
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was medium decrease item.
                    //-----------------------------------------------------------
                    if(timerMenu.getMediumDecreaseItem().equals(event.getCurrentItem())) {
                        String sqlSelectUUID = "SELECT uuid FROM AreaData WHERE areaName = '" + itemName + "';";
                        UUID areaID = UUID.fromString(DatabaseHandler.executeQuery(sqlSelectUUID).get(0));
                        String sqlGetCurrTimerVal = "SELECT timerValue FROM AreaTimer WHERE uuid = '" + areaID + "';";
                        int currTimerVal = Integer.parseInt(DatabaseHandler.executeQuery(sqlGetCurrTimerVal).get(0));
                        int mediumDecVal = (int) ConfigHandler.get("MediumDecreaseValue");
                        int updateVal = currTimerVal-mediumDecVal;
                        String sqlInsertChange = "UPDATE AreaTimer SET timerValue = " + updateVal + " WHERE uuid = '" + areaID + "';";
                        DatabaseHandler.execute(sqlInsertChange);
                        AutoResetHandler.updateAreaResetInterval(itemName, updateVal);
                        this.timerMenu.updateDisplayItem(areaID);
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was large decrease item.
                    //-----------------------------------------------------------
                    if(timerMenu.getLargeDecreaseItem().equals(event.getCurrentItem())) {
                        String sqlSelectUUID = "SELECT uuid FROM AreaData WHERE areaName = '" + itemName + "';";
                        UUID areaID = UUID.fromString(DatabaseHandler.executeQuery(sqlSelectUUID).get(0));
                        String sqlGetCurrTimerVal = "SELECT timerValue FROM AreaTimer WHERE uuid = '" + areaID + "';";
                        int currTimerVal = Integer.parseInt(DatabaseHandler.executeQuery(sqlGetCurrTimerVal).get(0));
                        int largeDecVal = (int) ConfigHandler.get("LargeDecreaseValue");
                        int updateVal = currTimerVal-largeDecVal;
                        String sqlInsertChange = "UPDATE AreaTimer SET timerValue = " + updateVal + " WHERE uuid = '" + areaID + "';";
                        DatabaseHandler.execute(sqlInsertChange);
                        AutoResetHandler.updateAreaResetInterval(itemName, updateVal);
                        this.timerMenu.updateDisplayItem(areaID);
                    }
                    //-----------------------------------------------------------

                    //Check, if the clicked item was back item.
                    //-----------------------------------------------------------
                    if(timerMenu.getBackItem().equals(event.getCurrentItem())) {
                        SettingsMenu settings = new SettingsMenu(itemName);
                        ListenerLoader.getSettingsInvListener().setSettingsMenu(settings);
                        ListenerLoader.getSettingsInvListener().setItemName(itemName);
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
