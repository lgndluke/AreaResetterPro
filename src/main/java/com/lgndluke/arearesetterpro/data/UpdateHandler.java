package com.lgndluke.arearesetterpro.data;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * UpdateHandler.
 * @author lgndluke
 **/
public class UpdateHandler {

    //Static Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static final int resourceID = 109372;

    //Static Methods
    public static void check() {
        String versionString = areaPlugin.getPluginMeta().getVersion();
        checkForUpdates(version -> {
            if(!versionString.equals(version)) {
                areaPlugin.getLogger().log(Level.WARNING, "A new Version of AreaResetterPro is available. Please update!");
            }
        });
    }

    private static void checkForUpdates(Consumer<String> consumer) {
        areaPlugin.getServer().getScheduler().runTaskAsynchronously(areaPlugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceID + "/~").openStream();
                 Scanner scan = new Scanner(inputStream)) {
                if(scan.hasNext()) {
                    consumer.accept(scan.next());
                }
            } catch (IOException io) {
                areaPlugin.getLogger().log(Level.WARNING, "Could not check for updates!", io.getMessage());
            }
        });
    }

}
