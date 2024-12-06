package com.lgndluke.lgndware.loaders;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Abstract base class for listener loaders.
 * @author lgndluke
 **/
public abstract class AbstractListenerLoader extends AbstractLoader {

    /**
     * @param plugin The JavaPlugin instance associated with this AbstractListenerLoader.
     **/
    public AbstractListenerLoader(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Abstract method to be implemented by subclasses for listener loading logic.
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
