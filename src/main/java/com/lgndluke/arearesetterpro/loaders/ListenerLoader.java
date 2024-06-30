package com.lgndluke.arearesetterpro.loaders;

import com.lgndluke.arearesetterpro.commands.Menu;
import com.lgndluke.arearesetterpro.commands.Tool;
import com.lgndluke.lgndware.loaders.AbstractListenerLoader;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Static Class used to register Plugin listeners during server startup.
 * @author lgndluke
 **/
public class ListenerLoader extends AbstractListenerLoader {

    private static final Menu.AreaInvListener invListener = new Menu.AreaInvListener();
    private static final Menu.ConfirmationMenuListener confirmationListener = new Menu.ConfirmationMenuListener();
    private static final Menu.SettingsMenuListener settingsListener = new Menu.SettingsMenuListener();
    private static final Menu.TimerMenuListener timerListener = new Menu.TimerMenuListener();
    private static final Tool.SetPosToolListener toolListener = new Tool.SetPosToolListener();

    public ListenerLoader(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public void load() {

        super.getPlugin().getServer().getPluginManager().registerEvents(invListener, super.getPlugin());
        super.getPlugin().getServer().getPluginManager().registerEvents(confirmationListener, super.getPlugin());
        super.getPlugin().getServer().getPluginManager().registerEvents(settingsListener, super.getPlugin());
        super.getPlugin().getServer().getPluginManager().registerEvents(timerListener, super.getPlugin());
        super.getPlugin().getServer().getPluginManager().registerEvents(toolListener, super.getPlugin());

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
