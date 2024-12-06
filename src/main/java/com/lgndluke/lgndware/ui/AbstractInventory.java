package com.lgndluke.lgndware.ui;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Abstract base class for Inventory generation.
 * @author lgndluke
 **/
public abstract class AbstractInventory {

    private final JavaPlugin plugin;
    private final Inventory inventory;

    /**
     * @param plugin The JavaPlugin instance associated with this AbstractInventory.
     * @param size The size of the inventory.
     * @param title The title of the inventory.
     **/
    protected AbstractInventory(JavaPlugin plugin, int size, Component title) {
        this.plugin = plugin;
        this.inventory = plugin.getServer().createInventory(null, size, title);
    }

    /**
     * Abstract method to be implemented by subclasses for initialization logic.
     * @return True, if the initialization was successful. Otherwise, false.
     **/
    protected abstract boolean initialize();

    /**
     * @return The constructed ItemStack object.
     **/
    protected ItemStack constructItem(Material material, Component itemName, List<Component> loreList) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(itemName);
        meta.lore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * @return The JavaPlugin instance.
     **/
    protected JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * @return The Inventory instance.
     **/
    protected Inventory getInventory() {
        return this.inventory;
    }

}
