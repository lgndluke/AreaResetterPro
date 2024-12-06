package com.lgndluke.lgndware.loaders;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Abstract base class for abstract loaders.
 * @author lgndluke
 **/
public abstract class AbstractLoader {

    private final JavaPlugin plugin;

    /**
     * @param plugin The JavaPlugin instance associated with this AbstractLoader.
     **/
    protected AbstractLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Abstract method to be implemented by subclasses for loading logic.
     **/
    public abstract void load();

    /**
     * @return The JavaPlugin instance.
     **/
    protected JavaPlugin getPlugin() {
        return this.plugin;
    }

}
