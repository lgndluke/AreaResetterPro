package com.lgndluke.lgndware.loaders;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Abstract base class for command loaders.
 * @author lgndluke
 **/
public abstract class AbstractCommandLoader extends AbstractLoader {

    /**
     * @param plugin The JavaPlugin instance associated with this AbstractCommandLoader.
     **/
    public AbstractCommandLoader(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Abstract method to be implemented by subclasses for command loading logic.
     **/
    @Override
    public abstract void load();

    /**
     * @return The JavaPlugin instance.
     **/
    protected JavaPlugin getPlugin() {
        return super.getPlugin();
    }

}
