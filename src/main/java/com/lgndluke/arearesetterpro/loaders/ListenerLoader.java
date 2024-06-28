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

    private Menu.AreaInvListener invListener;
    private Menu.ConfirmationMenuListener confirmationListener;
    private Menu.SettingsMenuListener settingsListener;
    private Menu.TimerMenuListener timerListener;
    private Tool.SetPosToolListener toolListener;

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

    public Menu.AreaInvListener getAreaInvListener() {
        return invListener;
    }
    public Menu.ConfirmationMenuListener getConfirmationInvListener() {
        return confirmationListener;
    }
    public Menu.SettingsMenuListener getSettingsInvListener() {
        return settingsListener;
    }
    public Menu.TimerMenuListener getTimerListener()  {
        return timerListener;
    }

}
