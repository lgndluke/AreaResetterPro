package com.lgndluke.arearesetterpro.placeholders;

import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.data.AutoResetHandler;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * PlaceholderAPI implementation of AreaResetterPro.
 * @author lgndluke
 **/
public class AreaResetterProExpansion extends PlaceholderExpansion {

    //Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
    private final AutoResetHandler autoResetHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getAutoResetHandler();
    private final String identifier = areaPlugin.getPluginMeta().getName();
    private final String author = areaPlugin.getPluginMeta().getAuthors().get(0);
    private final String version = areaPlugin.getPluginMeta().getVersion();
    private static List<String> areaData = new ArrayList<>();

    //Methods
    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull String getAuthor() {
        return author;
    }

    @Override
    public @NotNull String getVersion() {
        return version;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String areaName) {
        if(areaData.contains(areaName)) {
            long timerValue = autoResetHandler.getTimeRemaining(areaName);
            long hours = timerValue / 3600;
            long minutes = (timerValue % 3600) / 60;
            long seconds = timerValue % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return null;
    }

    public static void updateValues() {
        try {
            ResultSet results = databaseHandler.getAreaData();
            if(results != null) {
                while (results.next()) {
                    areaData.add(results.getString("areaName"));
                }
                results.close();
            }
        } catch (SQLException se) {
            areaPlugin.getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
        }
    }

}
