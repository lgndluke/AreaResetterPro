package com.lgndluke.arearesetterpro.ui;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.arearesetterpro.tasks.ResetAllTask;
import com.lgndluke.lgndware.data.MessageHandler;
import com.lgndluke.lgndware.ui.AbstractInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This Class represents the main part of the plugins GUI.
 * @author lgndluke
 **/
public class AreaMenu extends AbstractInventory {

    private final MessageHandler messageHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getMessageHandler();
    private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
    private final Component prefix = messageHandler.getMessageAsComponent("Prefix");
    private final List<ItemStack> contents = new ArrayList<>();
    private ItemStack previousPageItem, nextPageItem, resetAllItem, emptyItem;
    private int pages;

    public AreaMenu() {
        super(AreaResetterPro.getPlugin(AreaResetterPro.class), 54, MiniMessage.miniMessage().deserialize("<blue>Area</blue><gold>Resetter</gold><red>Pro</red>"));
        initialize();
        super.getPlugin().getServer().getPluginManager().registerEvents(new AreaMenuListener(), super.getPlugin());
    }

    @Override
    protected boolean initialize() {
        List<String> names = databaseHandler.getAreaDataNames();
        pages = calculatePages();
        for(String name : names) {
            contents.add(constructItem(Material.LIME_STAINED_GLASS_PANE, Component.text(name), messageHandler.getMessagesAsComponentList("AreaItemLore")));
        }
        previousPageItem = constructItem(Material.REDSTONE, messageHandler.getMessageAsComponent("PreviousPageItemName"), messageHandler.getMessagesAsComponentList("PreviousPageItemLore"));
        nextPageItem = constructItem(Material.GLOWSTONE_DUST, messageHandler.getMessageAsComponent("NextPageItemName"), messageHandler.getMessagesAsComponentList("NextPageItemLore"));
        resetAllItem = constructItem(Material.LAPIS_LAZULI, messageHandler.getMessageAsComponent("ResetItemName"), messageHandler.getMessagesAsComponentList("ResetItemLore"));
        emptyItem = constructItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, messageHandler.getMessageAsComponent("EmptyItemName"), messageHandler.getMessagesAsComponentList("EmptyItemLore"));
        getInvPage(0);
        return true;
    }

    private int calculatePages() {
        return Math.floorDiv(databaseHandler.getAreaDataSize(), (super.getInventory().getSize()-9));
    }

    private ItemStack getEmptyItem() {
        ItemMeta emptyItemMeta = emptyItem.getItemMeta();
        NamespacedKey emptyKey = new NamespacedKey(super.getPlugin(), UUID.randomUUID().toString());
        emptyItemMeta.getPersistentDataContainer().set(emptyKey, PersistentDataType.STRING, emptyKey.value());
        emptyItem.setItemMeta(emptyItemMeta);
        return emptyItem;
    }

    private void getInvPage(final int index) {
        super.getInventory().clear();
        int start = (super.getInventory().getSize()-9)*index;
        for(int i=0; i<super.getInventory().getSize()-9; i++) {
            int contentIndex = start+i;
            if(contentIndex < contents.size()) {
                super.getInventory().setItem(i, contents.get(contentIndex));
            } else {
                super.getInventory().setItem(i, getEmptyItem());
            }
        }
        for(int i=45; i<54; i++) {
            super.getInventory().setItem(i, getEmptyItem());
        }
        super.getInventory().setItem(super.getInventory().getSize()-9, previousPageItem);
        super.getInventory().setItem(super.getInventory().getSize()-5, resetAllItem);
        super.getInventory().setItem(super.getInventory().getSize()-1, nextPageItem);
    }

    public Inventory getInventory() {
        return super.getInventory();
    }

    /**
     * This Class listens and executes events from the AreaMenu.
     * @author lgndluke
     **/
    private class AreaMenuListener implements Listener {

        private final Component noPrevPage = messageHandler.getMessageAsComponent("NoPreviousPage");
        private final Component noNextPage = messageHandler.getMessageAsComponent("NoNextPage");
        private int index = 0;

        @EventHandler
        public void onAreaMenuClickEvent(InventoryClickEvent event) {
            if(event.getInventory() == AreaMenu.this.getInventory() && event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                if(contents.contains(event.getCurrentItem())) {
                    String itemName = PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().displayName()).substring(1, PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().displayName()).length()-1);
                    if(event.getClick().isLeftClick()) {
                        player.openInventory(new SettingsMenu(itemName).getInventory());
                    }
                    if(event.getClick().isRightClick()) {
                        player.openInventory(new ConfirmationMenu(itemName).getInventory());
                    }
                }
                if(nextPageItem.equals(event.getCurrentItem())) {
                    if(index == pages) {
                        player.sendMessage(prefix.append(noNextPage));
                        event.setCancelled(true);
                        return;
                    }
                    AreaMenu.this.getInvPage(++index);
                }
                if(resetAllItem.equals(event.getCurrentItem())) {
                    AreaMenu.this.getPlugin().getServer().getScheduler().runTaskAsynchronously(AreaMenu.this.getPlugin(), new ResetAllTask(player).execute());
                    AreaMenu.this.getInventory().close();
                }
                if(previousPageItem.equals(event.getCurrentItem())) {
                    if(index == 0) {
                        player.sendMessage(prefix.append(noPrevPage));
                        event.setCancelled(true);
                        return;
                    }
                    AreaMenu.this.getInvPage(--index);
                }
                event.setCancelled(true);
            }
        }

    }

}
