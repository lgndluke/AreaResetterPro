package com.lgndluke.lgndware.data;

import com.lgndluke.lgndware.concurrent.DefaultAsyncExecutor;
import com.lgndluke.lgndware.concurrent.ScheduledAsyncExecutor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Abstract base class for handling data operations.
 * @author lgndluke
 **/
public abstract class AbstractHandler {

    private final JavaPlugin plugin;
    private final DefaultAsyncExecutor defaultAsyncExecutor = new DefaultAsyncExecutor();
    private final ScheduledAsyncExecutor scheduledAsyncExecutor = new ScheduledAsyncExecutor();

    /**
     * @param plugin The JavaPlugin instance associated with this AbstractHandler.
     **/
    protected AbstractHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Abstract method to be implemented by subclasses for initialization logic.
     * @return True, if the initialization was successful. Otherwise, false.
     **/
    public abstract boolean initialize();

    /**
     * Abstract method to be implemented by subclasses for termination logic.
     * @return True, if the termination was successful. Otherwise, false.
     **/
    public abstract boolean terminate();

    /**
     * @return The JavaPlugin instance.
     **/
    protected JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * @return The DefaultAsyncExecutor instance.
     **/
    protected DefaultAsyncExecutor getDefaultAsyncExecutor() {
        return this.defaultAsyncExecutor;
    }

    /**
     * @return The ScheduledAsyncExecutor instance.
     **/
    protected ScheduledAsyncExecutor getScheduledAsyncExecutor() {
        return this.scheduledAsyncExecutor;
    }

}
