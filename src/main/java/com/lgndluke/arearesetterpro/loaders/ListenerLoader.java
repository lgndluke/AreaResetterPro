package com.lgndluke.arearesetterpro.loaders;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.commands.Menu;
import com.lgndluke.arearesetterpro.commands.Tool;
import org.bukkit.plugin.Plugin;

/**
 * Static Class used to register Plugin listeners during server startup.
 * @author lgndluke
 **/
public class ListenerLoader {

    //Static Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static final Menu.AreaInvListener invListener = new Menu.AreaInvListener();
    private static final Menu.ConfirmationMenuListener confirmationListener = new Menu.ConfirmationMenuListener();
    private static final Menu.SettingsMenuListener settingsListener = new Menu.SettingsMenuListener();
    private static final Menu.TimerMenuListener timerListener = new Menu.TimerMenuListener();
    private static final Tool.SetPosToolListener toolListener = new Tool.SetPosToolListener();

    //Static Methods
    /**
     * Initialize Listener objects here.
     **/
    public static void load() {

        areaPlugin.getServer().getPluginManager().registerEvents(invListener, areaPlugin);
        areaPlugin.getServer().getPluginManager().registerEvents(confirmationListener, areaPlugin);
        areaPlugin.getServer().getPluginManager().registerEvents(settingsListener, areaPlugin);
        areaPlugin.getServer().getPluginManager().registerEvents(timerListener, areaPlugin);
        areaPlugin.getServer().getPluginManager().registerEvents(toolListener, areaPlugin);

    }

    public static Menu.AreaInvListener getAreaInvListener() {
        return invListener;
    }
    public static Menu.ConfirmationMenuListener getConfirmationInvListener() {
        return confirmationListener;
    }
    public static Menu.SettingsMenuListener getSettingsInvListener() {
        return settingsListener;
    }
    public static Menu.TimerMenuListener getTimerListener()  {
        return timerListener;
    }


}
