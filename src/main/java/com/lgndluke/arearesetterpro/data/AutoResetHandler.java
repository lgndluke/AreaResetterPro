package com.lgndluke.arearesetterpro.data;

import com.fastasyncworldedit.core.FaweAPI;
import com.lgndluke.arearesetterpro.AreaResetterPro;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * This Class handles automatic resets.
 * @author lgndluke
 **/
public class AutoResetHandler {

    //Static Attributes
    private static final Plugin areaPlugin = AreaResetterPro.getPlugin(AreaResetterPro.class);
    private static final List<AutoResetter> autoResetterList = new ArrayList<>();

    //Static Methods
    /**
     * Initialization inside DatabaseHandler!
     * Otherwise, DbCon might not be ready when this is trying to init.
     **/
    public static void initialize() {
        //Start auto-resetter for every area.
        String sqlAreaNames = "SELECT areaName FROM AreaData";
        String sqlAreaTimers = "SELECT timerValue FROM AreaTimer";

        List<String> areaNamesList = DatabaseHandler.executeQuery(sqlAreaNames);
        List<String> areaTimersList = DatabaseHandler.executeQuery(sqlAreaTimers);

        try {
            for(int i=0; i<areaNamesList.size(); i++) {
                addNewAutoResetter(areaNamesList.get(i), Long.parseLong(areaTimersList.get(i))*20);
            }
        } catch(NumberFormatException nfe) {
            areaPlugin.getLogger().log(Level.SEVERE, "Failed to enable auto-resetter", nfe);
        }

    }

    public static void addNewAutoResetter(String areaName, long resetInterval) {
        autoResetterList.add(new AutoResetter(areaName, resetInterval));
    }

    public static void updateAreaResetInterval(String areaName, long resetInterval) {
        for(AutoResetter autoResetter : autoResetterList) {
            if(autoResetter.getAreaName().equals(areaName)) {
                autoResetter.setResetInterval(resetInterval*20);
            }
        }
    }

    //Inner Classes
    private static class AutoResetter implements Runnable {

        //Attributes
        private final String areaName;
        private long resetInterval;

        //Constructor
        private AutoResetter(String areaName, Long resetInterval) {
            this.areaName = areaName;
            this.resetInterval = resetInterval;
            //runTaskTimerAsynchronously(@NotNull Plugin, @NotNull Runnable, Interval to wait before exec)
            areaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(areaPlugin, this, this.resetInterval);
        }

        //Runnable
        @Override
        public void run() {
            //-----------------------------------------------------------
            //Define SQL-Statements and UUID as String.
            String sqlAreaCheck = "SELECT areaName FROM AreaData;";
            String sqlUuid = "SELECT uuid FROM AreaData WHERE areaName = '" + areaName + "';";
            String sqlWorld = "SELECT world FROM AreaData WHERE areaName = '" + areaName + "';";

            List<String> resultNames = DatabaseHandler.executeQuery(sqlAreaCheck);
            if(resultNames.contains(areaName)) {

                //Get Data from Database.
                String uuid = DatabaseHandler.executeQuery(sqlUuid).get(0);
                String worldName = DatabaseHandler.executeQuery(sqlWorld).get(0);

                String sqlCoordX = "SELECT x FROM AreaData WHERE uuid = '" + uuid + "'";
                String sqlCoordY = "SELECT y FROM AreaData WHERE uuid = '" + uuid + "'";
                String sqlCoordZ = "SELECT z FROM AreaData WHERE uuid = '" + uuid + "'";
                String sqlTimesReset = "SELECT timesReset FROM AreaStats WHERE uuid = '" + uuid + "'";

                int xCoord = Integer.parseInt(DatabaseHandler.executeQuery(sqlCoordX).get(0));
                int yCoord = Integer.parseInt(DatabaseHandler.executeQuery(sqlCoordY).get(0));
                int zCoord = Integer.parseInt(DatabaseHandler.executeQuery(sqlCoordZ).get(0));
                int timesReset = Integer.parseInt(DatabaseHandler.executeQuery(sqlTimesReset).get(0));

                //Set file-path and get StructureManager.
                String filePath = "AreaData/" + uuid + ".schem";
                File worldData = new File(areaPlugin.getDataFolder().getAbsolutePath(), filePath);

                try {
                    FaweAPI.load(worldData).paste(FaweAPI.getWorld(worldName), BlockVector3.at(xCoord, yCoord, zCoord)).close();
                    //Update reset statistics.
                    String sqlUpdateResetCounter = "UPDATE AreaStats SET timesReset = " + Math.addExact(timesReset, 1) + " WHERE uuid = '" + uuid + "';";
                    DatabaseHandler.execute(sqlUpdateResetCounter);
                } catch (Exception e) {
                    areaPlugin.getLogger().log(Level.SEVERE, "AutoResetter: Could not reset area: " + areaName);
                    areaPlugin.getLogger().log(Level.SEVERE, "AutoResetter: An Error occurred whilst trying to reset the area.", e);
                }
                //Resets the timer with a new value.
                areaPlugin.getServer().getScheduler().runTaskLaterAsynchronously(areaPlugin, this, this.resetInterval);
            }
            //-----------------------------------------------------------
        }

        //Methods
        private void setResetInterval(long resetInterval) {
            this.resetInterval = resetInterval;
        }

        private String getAreaName() {
            return this.areaName;
        }

    }

}
