package com.company;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class RoundRobin_Scheduler implements Scheduler{
    public ArrayList<Process> processes;
    public ArrayList<Process> originalProcesses;
    public ArrayDeque<Process> processQueue;
    int time;
    ArrayList<TimeSlot> timeLine;
    int contextSwitching;
    int quantumTime;
    public RoundRobin_Scheduler(ArrayList<Process> processes, int contextSwitching,int quantumTime)
    {
        this.processes = processes;
        this.quantumTime = quantumTime;
        this.timeLine = new ArrayList<TimeSlot>();
        processQueue = new ArrayDeque<Process>();
        this.contextSwitching = contextSwitching;
        this.originalProcesses = new ArrayList<Process>();
        for (int i = 0; i < processes.size(); i++)
            originalProcesses.add(processes.get(i));

        getNextProcesses(Integer.MAX_VALUE);
    }
    public void getNextProcesses(int currentTime)
    {
        this.time = currentTime;
        for (Process process : processes) {
            time = Math.min(process.arrivalTime, time);
        }

        for(int i = 0; i < processes.size(); i++)
        {
            Process process = processes.get(i);
            if (process.arrivalTime == time) {
                processQueue.addLast(process);
                processes.remove(i);
                i--;
            }
        }
    }
    public void getNextArrivedProcesses()
    {
//        ArrayList<Process> newArrivedProcess = new ArrayList<Process>();
        for(int i = 0; i < processes.size(); i++)
        {
            if (processes.get(i).arrivalTime == time) {
//                newArrivedProcess.add(processes.get(i));
                processQueue.add(processes.get(i));
                processes.remove(i);
                i--;
            }
        }

    }
    public int calculateWeight(Process process)
    {
        int  completionTime= 0;
        for (int i = 0; i < timeLine.size(); i++)
        {
            if (timeLine.get(i).name.equals(process.name))
            {
                completionTime = timeLine.get(i).endT;
            }
        }
        return completionTime - process.originalExecTime - process.arrivalTime;
    }
    public double calculateAvgWeight()
    {
        return 0;
    }

    public int calculateTurnAroundTime(Process process)
    {
        return 0;
    }

    public double calculateAvgTurnAroundTime()
    {
        return 0;
    }

    public void printTimeLine()
    {
        for(TimeSlot timeSlot: timeLine)
        {
            System.out.println(timeSlot.name + ": " + timeSlot.startT + " --> " + timeSlot.endT);
        }
        System.out.println("---------------------------------------");
    }
    public void printWaitingTime()
    {
        System.out.println("Processes waiting time");
        int totalWaitTime = 0;
        for(Process p: originalProcesses)
        {
            int waitingTime = calculateWeight(p);
            totalWaitTime+=waitingTime;
            System.out.println(p.name + " waiting time is " + waitingTime);
        }
        double avgWaitTime = ((totalWaitTime*1.00)/(originalProcesses.size()*1.00));
        System.out.println("Average waiting time is " + avgWaitTime);
        System.out.println("---------------------------------------");
    }
    public void  printTurnArountTime()
    {
        System.out.println("Processes turnaround time");
        int totalTurnAroundTime = 0;
        for(Process p: originalProcesses)
        {
            int turnaroundTime = calculateWeight(p) + p.originalExecTime;
            totalTurnAroundTime+=turnaroundTime;
            System.out.println(p.name + " turnaround time is " + turnaroundTime);
        }
        double avgTurnAroundTime = ((totalTurnAroundTime*1.00)/(originalProcesses.size()*1.00));
        System.out.println("Average turnaround time is " + avgTurnAroundTime);
        System.out.println("---------------------------------------");
    }
    public void printOutput()
    {
        printTimeLine();
        printWaitingTime();
        printTurnArountTime();

    }
    public void runScheduler()
    {
        boolean firstProcess = true;
        while (!processQueue.isEmpty())
        {
            if (!firstProcess)
            {
                int startTime = time;
                for (int i = 1; i<= contextSwitching; i++)
                {
                    time++;
                    getNextArrivedProcesses();
                }
                timeLine.add(new TimeSlot(startTime,"Context Switching", time));
            }
            firstProcess = false;
            Process currentProcess = processQueue.peek();
            processQueue.poll();
            int startTime = time;
            int i;
            for(i = 1; i <= quantumTime; i++)
            {
                time++;
                getNextArrivedProcesses();
                currentProcess.executionTime--;
                if (currentProcess.executionTime == 0)
                    break;
            }
            timeLine.add(new TimeSlot(startTime,currentProcess.name,time));
            if (currentProcess.executionTime != 0)
            {
                processQueue.addLast(currentProcess);
                if (processQueue.size() == 1)
                    firstProcess = true;
            }
        }
    }
}
