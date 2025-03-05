package org.example.BusinessLogic;




import org.example.Model.Server;
import org.example.Model.Task;

import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> servers=new ArrayList<>();
    private int maxNoServers;
    private int maxTasksPerServer;
    private Strategy strategy;

    public Scheduler(int maxNoServers, int maxTasksPerServer){
        for(int i = 0; i < maxNoServers; i++){
            {
                Server server = new Server();
                servers.add(server);
                Thread serverThread=new Thread(server);

            }

        }
    }

    public void changeStrategy(SelectionPolicy policy){
        if(policy == SelectionPolicy.SHORTEST_QUEUE){
            this.strategy = new ConcreteStrategyQueue();
        }
        if(policy == SelectionPolicy.SHORTEST_TIME){
            this.strategy = new ConcreteStrategyTime();
        }
    }

    public void dispatchTask(Task t){
        if(strategy instanceof ConcreteStrategyQueue)
        {
            ConcreteStrategyQueue SQ=(ConcreteStrategyQueue) strategy;
            SQ.addTask(servers,t);
        }
        else if(strategy instanceof ConcreteStrategyTime)
        {
            ConcreteStrategyTime ST=(ConcreteStrategyTime) strategy;
            ST.addTask(servers,t);
        }


    }

    public List<Server> getServers(){
        return servers;
    }
}