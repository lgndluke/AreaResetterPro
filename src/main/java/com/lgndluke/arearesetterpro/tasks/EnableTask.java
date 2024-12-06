package com.lgndluke.arearesetterpro.tasks;

import com.lgndluke.arearesetterpro.commands.EnableCmd;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * This process asynchronously executes the 'arp_enable' commands logic.
 * @author lgndluke
 **/
public class EnableTask extends EnableCmd {

    //TODO Add attributes of EnableTask.

    public EnableTask() {
        //TODO construct EnableTask.
    }

    public RunnableFuture<Boolean> execute() {
        return new FutureTask<>(() -> {
            //TODO Implement enable logic.
            return true;
        });
    }

}
