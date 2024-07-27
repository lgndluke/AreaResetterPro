package com.lgndluke.arearesetterpro.tasks;


import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.commands.RemoveCmd;
import com.lgndluke.arearesetterpro.data.AutoResetHandler;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import com.lgndluke.lgndware.data.ConfigHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;

/**
 * This process asynchronously executes the 'arp_remove' commands logic.
 * @author lgndluke
 **/
public class RemoveTask extends RemoveCmd {

    private final ConfigHandler configHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getConfigHandler();
    private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
    private final AutoResetHandler autoResetHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getAutoResetHandler();
    private final Component success = messageHandler.getMessageAsComponent("RemoveSucceededMessage");
    private final Component failed = messageHandler.getMessageAsComponent("RemoveFailedMessage");
    private final Component nonExist = messageHandler.getMessageAsComponent("AreaNonExistent");
    private final Player player;
    private final String areaName;

    public RemoveTask(Player player, String areaName) {
        this.player = player;
        this.areaName = areaName;
    }

    public RunnableFuture<Boolean> execute() {
        return new FutureTask<>(() -> {
            UUID uuid = getUUID();
            if(uuid == null) {
                return false;
            }
            if(deleteSchematics(uuid)) {
                deleteDatabaseEntry(uuid);
                deleteAutoResetHandler();
                updatePlaceholders();
                return true;
            }
            return false;
        });
    }

    private UUID getUUID() {
        try {
            String uuidAsString = databaseHandler.getAreaData(areaName).getString("uuid");
            if(uuidAsString == null) {
                player.sendMessage(prefix.append(nonExist));
                return null;
            }
            return UUID.fromString(uuidAsString);
        } catch (SQLException se) {
            return null;
        }
    }

    private void deleteDatabaseEntry(UUID uuid) {
        databaseHandler.deleteAreaData(areaName);
        databaseHandler.deleteAreaStats(uuid);
        databaseHandler.deleteAreaTimer(uuid);
    }

    private boolean deleteSchematics(UUID uuid) {
        String filePath = "/AreaData/" + uuid + ".schem";
        File schematics = new File(super.getPlugin().getDataFolder().getAbsolutePath(), filePath);
        try {
            Files.delete(schematics.toPath());
        } catch (IOException io) {
            player.sendMessage(prefix.append(failed));
            super.getPlugin().getLogger().log(Level.SEVERE, "Removal of Area: '" + areaName + "' has been cancelled!");
            super.getPlugin().getLogger().log(Level.SEVERE, "Cause: Deletion of file '" + uuid + ".schem' inside the 'AreaData' folder failed!");
            super.getPlugin().getLogger().log(Level.SEVERE, "Exception Message: ", io);
            return false;
        }
        player.sendMessage(prefix.append(success));
        return true;
    }

    private void deleteAutoResetHandler() {
        autoResetHandler.removeAreaResetter(areaName);
    }

    private void updatePlaceholders() {
        if(((Boolean) configHandler.get("EnableAutoResets")) && autoResetHandler.isInitialized()) {
            AreaResetterPro.getPlugin(AreaResetterPro.class).getAreaResetterProExpansion().updateValues();
        }
    }

}
