package com.company;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Priority_Scheduler implements Scheduler{
    public ArrayList<Process> processes;
    public ArrayList<Process> originalProcesses;
    public PriorityQueue<Process> processesPQ;
    int time;
    int initialTime;
    ArrayList<TimeSlot> timeLine;
    int contextSwitching;
    int agingFactor;
    boolean preemptiveScheduler;
    public Priority_Scheduler(ArrayList<Process> processes, int contextSwitching,int agingFactor,boolean preemptiveScheduler)
    {
        this.processes = processes;
        this.timeLine = new ArrayList<TimeSlot>();
        this.preemptiveScheduler = preemptiveScheduler;
        processesPQ = new PriorityQueue<Process>(processes.size(),new ComparatorPriority());
        this.contextSwitching = contextSwitching;
        this.originalProcesses = new ArrayList<Process>();
        this.agingFactor = agingFactor;
        for (int i = 0; i < processes.size(); i++)
            originalProcesses.add(processes.get(i));

        getNextProcesses(Integer.MAX_VALUE);
        initialTime = time;
    }
    private void doAging()
    {
        if (agingFactor == 0 || (time - initialTime) % agingFactor < agingFactor - 1)
            return;
        PriorityQueue<Process> newProccesPQ = new PriorityQueue<Process>(10,new ComparatorPriority());
        while (!processesPQ.isEmpty())
        {
            Process process = processesPQ.peek();
            processesPQ.poll();
            process.priority--;
            newProccesPQ.add(process);
        }
        processesPQ = newProccesPQ;
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
                processesPQ.add(process);
                processes.remove(i);
                i--;
            }
        }
    }
    public void runScheduler()
    {
        boolean firstProcess = true;
        while (!processesPQ.isEmpty())
        {
            if (!firstProcess)
            {
                int startTime = time;
                for (int i = 1; i<= contextSwitching; i++)
                {
                    time++;
                    doAging();
                    getNextArrivedProcesses(0);
                }
                timeLine.add(new TimeSlot(startTime,"Context Switching", time));
            }
            firstProcess = false;
            Process currentProcess = processesPQ.peek();
            processesPQ.poll();
            int startTime = time;
            int i;
            for(i = 1; i <= currentProcess.executionTime; i++)
            {
                time++;
                doAging();
                boolean preempt = getNextArrivedProcesses(currentProcess.priority);
                if (i != currentProcess.executionTime) {
                    if (preemptiveScheduler && preempt)
                        break;
                }
            }
            timeLine.add(new TimeSlot(startTime,currentProcess.name,time));
            if (preemptiveScheduler && i < currentProcess.executionTime)
            {
                currentProcess.executionTime-=i;
                processesPQ.add(currentProcess);
            }

        }
    }

    public boolean getNextArrivedProcesses(int currentPriority)
    {
        boolean preempt = false;
        for(int i = 0; i < processes.size(); i++)
        {
            if (processes.get(i).arrivalTime == time) {
                processesPQ.add(processes.get(i));
                if (processes.get(i).priority < currentPriority)
                    preempt = true;
                processes.remove(i);
                i--;
            }
        }
        return preempt;
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
}
