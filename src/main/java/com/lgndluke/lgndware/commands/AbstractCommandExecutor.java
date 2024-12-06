package com.lgndluke.lgndware.commands;

import com.lgndluke.lgndware.concurrent.DefaultAsyncExecutor;
import com.lgndluke.lgndware.concurrent.ScheduledAsyncExecutor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Abstract base class for command executors.
 * @author lgndluke
 **/
public abstract class AbstractCommandExecutor {

    private final JavaPlugin plugin;
    private final DefaultAsyncExecutor defaultAsyncExecutor = new DefaultAsyncExecutor();
    private final ScheduledAsyncExecutor scheduledAsyncExecutor = new ScheduledAsyncExecutor();

    /**
     * @param plugin The JavaPlugin instance associated with this command executor.
     **/
    protected AbstractCommandExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Terminates the AbstractCommandHandler.
     * @return True, if the termination was successful. Otherwise, false.
     **/
    protected boolean terminate() {
        defaultAsyncExecutor.shutdown();
        scheduledAsyncExecutor.shutdown();
        return true;
    }

    /**
     * @return The JavaPlugin instance.
     **/
    protected JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * @return The DefaultAsyncExecutor instance.
     **/
    protected DefaultAsyncExecutor getAsyncExecutor() {
        return this.defaultAsyncExecutor;
    }

    /**
     * @return The ScheduledAsyncExecutor instance.
     **/
    protected ScheduledAsyncExecutor getScheduledAsyncExecutor() {
        return this.scheduledAsyncExecutor;
    }

}
