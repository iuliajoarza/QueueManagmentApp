package org.example.BusinessLogic;

import org.example.Model.Server;
import org.example.Model.Task;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcreteStrategyTime implements Strategy{
    public void addTask(List<Server> servers, Task task) {
        int minTasks = Integer.MAX_VALUE;
        Server minServer = null;

        // Iterate through servers to find the one with the minimum number of tasks
        for (Server server : servers) {
            int numTasks = server.getWaitingPeriod().intValue();
            if (numTasks < minTasks) {
                minTasks = numTasks;
                minServer = server;
            }
        }

        if (minServer != null) {
            minServer.addTask(task);
        }
    }
}
