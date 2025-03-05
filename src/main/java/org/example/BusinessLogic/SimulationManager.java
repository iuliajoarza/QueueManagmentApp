package org.example.BusinessLogic;

import org.example.Model.Task;
import org.example.Model.Server;
import org.example.gui.SimulationFrame;

import java.util.*;

public class SimulationManager implements Runnable {
    private int timeLimit;
    private int maxProcessingTime;
    private int minProcessingTime;

    private int minArrivalTime;
    private int maxArrivalTime;
    private int numberOfServers;
    private int numberOfClients;

    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> generatedTasks = Collections.synchronizedList(new ArrayList<>());

    private SelectionPolicy selectionPolicy = SelectionPolicy.SHORTEST_TIME;

    public SimulationManager(int timeLimit, int maxProcessingTime, int minProcessingTime, int numberOfServers, int numberOfClients, SimulationFrame frame, int strategy,int maxArrivalTime,int minArrivalTime) {
        this.timeLimit = timeLimit;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.minArrivalTime=minArrivalTime;
        this.maxArrivalTime=maxArrivalTime;
        this.frame = frame;
        if (strategy == 1)
            this.selectionPolicy = SelectionPolicy.SHORTEST_QUEUE;
        else
            this.selectionPolicy = SelectionPolicy.SHORTEST_TIME; // Adjust according to your actual selection policies

        // Initialize scheduler with provided parameters
        scheduler = new Scheduler(numberOfServers, maxProcessingTime);

        // Initialize strategy
        scheduler.changeStrategy(selectionPolicy);

        // Generate random tasks
        generateNRandomTasks();
    }

    private void generateNRandomTasks() {
        for (int i = 0; i < numberOfClients; i++) {
            int processingTime = (int) (Math.random() * (maxProcessingTime - minProcessingTime + 1) + minProcessingTime);
            int arrivalTime = (int) (Math.random() * (maxArrivalTime-minArrivalTime+1)+minArrivalTime);
            generatedTasks.add(new Task(i, arrivalTime, processingTime));
        }
        generatedTasks.sort(Comparator.comparingInt(Task::getArrivalTime));
    }

    // Method to calculate the average waiting time for each server
    private void calculateAverageWaitingTimes(List<Server> servers) {
        for (Server server : servers) {
            double averageWaitingTime = server.getAvW();
            frame.appendToOutput("Server Average Waiting Time: " + averageWaitingTime);
        }
    }

    public void run() {
        List<Integer> peakHour=new ArrayList<>();
        List<Integer> nrofClients=new ArrayList<>();
        int currentTime = 0;
        while (currentTime <= timeLimit || !generatedTasks.isEmpty()) {
            // Dispatch tasks at the current time
            Iterator<Task> iterator = generatedTasks.iterator();
            while (iterator.hasNext()) {
                Task task = iterator.next();
                if (task.getArrivalTime() == currentTime) {
                    scheduler.dispatchTask(task);
                    iterator.remove(); // Remove the dispatched task from the list
                }
            }
            frame.appendToOutput("time"+currentTime+'\n');
            // Process tasks in each server
            List<Server> servers = scheduler.getServers();
            frame.displayTasksAndServers(generatedTasks, servers);// Display servers' queues in the UI

            for (Server server : servers) {
                Task[] tasks = server.getTasks();
                if (tasks.length > 0) { // Check if there are tasks in the queue
                    tasks[0].decrementServiceTime(); // Decrement service time of the first task
                    if (tasks[0].getServiceTime() == 0) {
                        server.removeTask(); // Remove the task if its service time is zero
                    }
                }
            }

            for(int i=0;i<servers.size();i++)
                if(servers.get(i).getNumTasks()>nrofClients.get(i))
                {
                    nrofClients.set(i, servers.get(i).getNumTasks());
                    peakHour.set(i,currentTime);
                }


            // Wait for 1 second
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            currentTime++;
        }

        List<Double> averageWaitingTimes=new ArrayList<>();
        for (Server server : scheduler.getServers()) {
            averageWaitingTimes.add(server.getAvW());
        }

        // Pass average waiting times to the frame for display
        frame.displayAverageWaitingTimes(averageWaitingTimes);

        List<Double> averageServiceTime=new ArrayList<>();
        for (Server server : scheduler.getServers()) {
            averageServiceTime.add(server.getAsS());
        }

        frame.displayAverageServiceTimes(averageServiceTime);

        frame.updateStatus("Simulation completed.");
    }
}
