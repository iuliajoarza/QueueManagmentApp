package org.example.gui;

import org.example.BusinessLogic.SimulationManager;
import org.example.Model.Task;
import org.example.Model.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SimulationFrame extends JFrame {
    private JLabel statusLabel;
    private JButton startButton;
    private JTextField timeLimitField;
    private JTextField maxProcessingTimeField;
    private JTextField minProcessingTimeField;

    private JTextField maxArrivalTimeField;
    private JTextField minArrivalTimeField;
    private JTextField numberOfServersField;
    private JTextField numberOfClientsField;
    private JTextField strategyField;
    private JTextArea waitArea;
    private JTextArea outputTextArea; // Text area to display simulation output
    private SimulationManager simulationManager;

    public SimulationFrame() {
        setTitle("Task Scheduling Simulation");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Create components
        statusLabel = new JLabel("Simulation not started");
        startButton = new JButton("Start Simulation");
        timeLimitField = new JTextField("60", 10); // Default value set to 60
        maxProcessingTimeField = new JTextField("4", 10); // Default value set to 4
        minProcessingTimeField = new JTextField("2", 10); // Default value set to 2
        numberOfServersField = new JTextField("2", 10); // Default value set to 2
        numberOfClientsField = new JTextField("4", 10); // Default value set to 4
        maxArrivalTimeField = new JTextField("4", 10); // Default value set to 4
        minArrivalTimeField = new JTextField("2", 10); // Default value set to 2
        strategyField=new JTextField("1",10);
        waitArea=new JTextArea();
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false); // Make it read-only

        // Add action listener to the start button
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });

        // Add components to the frame
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(10, 2)); // Grid layout with 8 rows and 2 columns
        panel.add(new JLabel("Time Limit:"));
        panel.add(timeLimitField);
        panel.add(new JLabel("Max Processing Time:"));
        panel.add(maxProcessingTimeField);
        panel.add(new JLabel("Min Processing Time:"));
        panel.add(minProcessingTimeField);
        panel.add(new JLabel("Max Arrival Time:"));
        panel.add(maxArrivalTimeField);
        panel.add(new JLabel("Min Arrival Time:"));
        panel.add(minArrivalTimeField);
        panel.add(new JLabel("Number of Servers:"));
        panel.add(numberOfServersField);
        panel.add(new JLabel("Number of Clients:"));
        panel.add(numberOfClientsField);
        panel.add(new JLabel("Strategy"));
        panel.add(strategyField);


        panel.add(startButton);
        panel.add(statusLabel); // Status label spans two columns

        add(panel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void startSimulation() {
        // Disable start button to prevent multiple simulations running concurrently
        startButton.setEnabled(false);

        // Get input values
        int timeLimit = Integer.parseInt(timeLimitField.getText());
        int maxProcessingTime = Integer.parseInt(maxProcessingTimeField.getText());
        int minProcessingTime = Integer.parseInt(minProcessingTimeField.getText());
        int numberOfServers = Integer.parseInt(numberOfServersField.getText());
        int numberOfClients = Integer.parseInt(numberOfClientsField.getText());
        int strategy=Integer.parseInt(strategyField.getText());
        int maxArrival=Integer.parseInt(maxArrivalTimeField.getText());
        int minArrival=Integer.parseInt(minArrivalTimeField.getText());

        // Update status label
        statusLabel.setText("Simulation running...");

        // Create and start the simulation manager
        simulationManager = new SimulationManager(timeLimit, maxProcessingTime, minProcessingTime, numberOfServers, numberOfClients, this,strategy,maxArrival,minArrival);
        Thread simulationThread = new Thread(simulationManager);
        simulationThread.start();
    }

    // Method to update status label from SimulationManager
    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    // Method to append text to the output text area
    public void appendToOutput(String text) {
        outputTextArea.append(text + "\n");
    }

    // Method to display tasks waiting to be dispatched and servers' queues
    public void displayTasksAndServers(List<Task> tasks, List<Server> servers) {
        outputTextArea.append("Tasks Waiting to be Dispatched:\n");
        for (Task task : tasks) {
            outputTextArea.append("(" + task.getID() + "," + task.getArrivalTime() + "," + task.getServiceTime() + ")\n");
        }
        outputTextArea.append("\nServer Queues:\n");
        for (Server server : servers) {
            outputTextArea.append("Server "  + ": ");
            Task[] serverTasks = server.getTasks();
            if (serverTasks.length == 0) {
                outputTextArea.append("empty\n");
            } else {
                for (Task task : serverTasks) {
                    outputTextArea.append("(" + task.getID() + "," + task.getArrivalTime() + "," + task.getServiceTime() + ") ");
                }
                outputTextArea.append("\n");
            }
        }
        outputTextArea.append("\n");
    }

    // Add this method to the SimulationFrame class
    public void displayAverageWaitingTimes(List<Double> averageWaitingTimes) {
        // Clear existing content in waitArea
        outputTextArea.append("\n");

        // Display average waiting times for each server
        for (int i = 0; i < averageWaitingTimes.size(); i++) {
            outputTextArea.append("Average Waiting Time for Server " + (i+1) + ": " + averageWaitingTimes.get(i) + "\n");
        }
    }

    public void displayAverageServiceTimes(List<Double> averageServiceTimes) {
        // Clear existing content in waitArea
        outputTextArea.append("\n");

        // Display average waiting times for each server
        for (int i = 0; i < averageServiceTimes.size(); i++) {
            outputTextArea.append("Average Servive Time for Server " + (i+1) + ": " + averageServiceTimes.get(i) + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SimulationFrame();
            }
        });
    }
}