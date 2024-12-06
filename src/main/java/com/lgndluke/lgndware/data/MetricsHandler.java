package com.lgndluke.lgndware.data;

import com.lgndluke.lgndware.metrics.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for handling Metrics related operations.
 * @author lgndluke
 **/
public class MetricsHandler extends AbstractHandler {

    private final int pluginID;
    private Metrics metrics;

    /**
     * @param plugin The JavaPlugin instance associated with this MetricsHandler.
     * @param pluginID The unique plugin ID for Metrics.
     **/
    public MetricsHandler(JavaPlugin plugin, int pluginID) {
        super(plugin);
        this.pluginID = pluginID;
    }

    /**
     * Initializes the MetricsHandler on server startup.
     * @return True, if the initialization was successful. Otherwise, false.
     **/
    @Override
    public boolean initialize() {
        FutureTask<Boolean> initMetricsHandler = new FutureTask<>(() -> {
            this.metrics = new Metrics(super.getPlugin(), pluginID);
            return true;
        });
        return super.getDefaultAsyncExecutor().executeFuture(super.getPlugin().getLogger(), initMetricsHandler, 10, TimeUnit.SECONDS);
    }

    /**
     * Terminates the MetricsHandler.
     * @return True, if the termination was successful. Otherwise, false.
     **/
    @Override
    public boolean terminate() {
        if(this.metrics != null) {
            this.metrics.shutdown();
        }
        super.getDefaultAsyncExecutor().shutdown();
        super.getScheduledAsyncExecutor().shutdown();
        return true;
    }

}
