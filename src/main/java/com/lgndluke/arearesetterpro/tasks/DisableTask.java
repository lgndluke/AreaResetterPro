package com.lgndluke.arearesetterpro.tasks;

import com.lgndluke.arearesetterpro.commands.DisableCmd;

import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

/**
 * This process asynchronously executes the 'arp_disable' commands logic.
 * @author lgndluke
 **/
public class DisableTask extends DisableCmd {

    //TODO Add attributes of DisableTask

    public DisableTask() {
        //TODO construct DisableTask
    }

    public RunnableFuture<Boolean> execute() {
        return new FutureTask<>(() -> {
            //TODO implement disable logic.
            return true;
        });
    }

}
