package com.lgndluke.arearesetterpro.tasks;


import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.lgndluke.arearesetterpro.commands.ResetCmd;
import com.lgndluke.arearesetterpro.data.DatabaseHandler;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.logging.Level;

/**
 * This process asynchronously executes the 'Reset all item`s' logic.
 * @author lgndluke
 **/
public class ResetAllTask extends ResetCmd {

    private final DatabaseHandler databaseHandler = AreaResetterPro.getPlugin(AreaResetterPro.class).getDatabaseHandler();
    private final CommandSender sender;

    public ResetAllTask(CommandSender sender) {
        this.sender = sender;
    }

    public RunnableFuture<Boolean> execute() {
        return new FutureTask<>(() -> {
            try {
                ResultSet results = databaseHandler.getAreaData();
                while (results.next()) {
                    //super.getPlugin().getServer().getScheduler().runTaskAsynchronously(super.getPlugin(), new ResetTask(sender, results.getString("areaName")).execute());
                    super.getPlugin().getServer().getScheduler().runTaskAsynchronously(super.getPlugin(), new ResetTask(sender, results.getString("areaName")).execute());
                }
                results.close();
                return true;
            } catch (SQLException se) {
                super.getPlugin().getLogger().log(Level.SEVERE, "Couldn't fetch AreaData!", se);
                return false;
            }
        });
    }

}
