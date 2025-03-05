package org.example.Model;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.LinkedBlockingQueue;

public class Server implements Runnable {

    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private double avW;
    private double asS;
    private int  sums;
    private  int i;
    private int sum;

    // Constructor
    public Server() {
        this.tasks = new LinkedBlockingQueue<>(); // Initialize an empty LinkedBlockingQueue
        this.waitingPeriod = new AtomicInteger(0); // Initialize waitingPeriod to 0
        this.i=0;
        this.sum=0;
        this.sums=0;
    }

    // Method to add a task to the queue
    public void addTask(Task newTask) {
        this.i+=1;
        if(this.tasks!=null)
        for(Task t:this.tasks)
            this.sum+=t.getServiceTime();
        this.sums=newTask.getServiceTime();
        tasks.add(newTask);
        waitingPeriod.addAndGet(newTask.getServiceTime()) ;// Increment waiting period when a task is added
    }

    public double getAvW() {
        if (this.i != 0) {
            this.avW = (double) this.sum / this.i;
        } else {
            this.avW = 0;
        }
        return this.avW;
    }

    public double getAsS() {
        if (this.i != 0) {
            this.asS = (double) this.sums / this.i;
        } else {
            this.asS = 0;
        }
        return this.asS;
    }

    // Method to run the server
    @Override
    public void run() {
        while (true) {
            try {
                Task task = tasks.take(); // Get the next task from the queue
                System.out.print("(" + task.getID() + "," + task.getArrivalTime() + "," + task.getServiceTime() + "); ");
                Thread.sleep(task.getServiceTime() ); // Sleep for the service time in milliseconds
                task.decrementServiceTime(); // Decrease the service time by 1
                if (task.getServiceTime() > 0) {
                    tasks.put(task); // Put the task back into the queue if its service time is not yet over
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break; // Exit the loop if interrupted
            }
        }
    }
  public void removeTask(){
        tasks.remove();
  }

    public int getNumTasks(){
        return tasks.size();
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public Task[] getTasks() {

        return tasks.toArray(new Task[0]);
    }
}

