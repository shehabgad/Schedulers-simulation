package com.company;

import java.util.*;

public class AG_Scheduler implements Scheduler{
    public ArrayList<Process> processes;
    public ArrayList<Process> originalProcesses;
    public Map<String,ArrayList<Integer>> quantumHistory = new HashMap<String, ArrayList<Integer>>();
    int time;
    ArrayList<TimeSlot> timeLine;
    int contextSwitching;
    public ArrayDeque<Process> processQueue;

    public AG_Scheduler(ArrayList<Process> processes, int contextSwitching)
    {
        this.processes = processes;
        this.timeLine = new ArrayList<TimeSlot>();
        processQueue = new ArrayDeque<Process>();
        this.contextSwitching = contextSwitching;
        this.originalProcesses = new ArrayList<Process>();
        for (int i = 0; i < processes.size(); i++) {
            originalProcesses.add(processes.get(i));
            quantumHistory.put(processes.get(i).name,new ArrayList<Integer>());
        }

        getNextProcesses(Integer.MAX_VALUE);
    }
    public boolean getNextArrivedProcesses(int remaningExecutionTime)
    {
        boolean preempt = false;
        for(int i = 0; i < processes.size(); i++)
        {
            if (processes.get(i).arrivalTime == time) {
                processQueue.add(processes.get(i));
                if (processes.get(i).executionTime < remaningExecutionTime)
                    preempt = true;
                processes.remove(i);
                i--;
            }
        }
        return preempt;
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
    public void runScheduler()
    {
        boolean firstProcess = true;
        while (!processQueue.isEmpty())
        {
            if (!firstProcess)
            {
                int startTime = time;
                for(int i = 1; i<= contextSwitching; i++)
                {
                    time++;
                    getNextArrivedProcesses(0);
                }
                timeLine.add(new TimeSlot(startTime,"Context Switching", time));
            }
            firstProcess = false;

            Process currentProcess = processQueue.peek();
            processQueue.poll();
            quantumHistory.get(currentProcess.name).add(currentProcess.quantumTime);
            int currentQuantumTime = currentProcess.quantumTime;

            int part1Quantum = (int)Math.ceil(0.25 * (double) currentQuantumTime);
            int part2Quantum = (int)(Math.ceil(0.5*(double) currentQuantumTime) - Math.ceil((double) 0.25*currentQuantumTime));
            int part3Quantum = currentQuantumTime - (part1Quantum + part2Quantum);

            int startTime = time;
            for (int i = 1; i <= part1Quantum; i++)
            {
                time++;
                getNextArrivedProcesses(0);
                currentProcess.executionTime--;
                currentQuantumTime--;
                if (currentProcess.executionTime == 0)
                    break;
            }
            if (currentProcess.executionTime == 0) {
                timeLine.add(new TimeSlot(startTime,currentProcess.name,time));
                continue;
            }


            boolean isHigherPriorityExist = checkHigherPriority(currentProcess,currentQuantumTime);
            if (isHigherPriorityExist) {
                timeLine.add(new TimeSlot(startTime,currentProcess.name,time));
                continue;
            }
            for (int i = 1; i<= part2Quantum; i++)
            {
                time++;
                getNextArrivedProcesses(0);
                currentProcess.executionTime--;
                currentQuantumTime--;
                if (currentProcess.executionTime == 0)
                    break;
            }
            if (currentProcess.executionTime == 0)
            {
                timeLine.add(new TimeSlot(startTime,currentProcess.name,time));
                continue;
            }

            boolean isLessBurstTimeExist = checkLessBurstTime(currentProcess,currentQuantumTime);
            if (isLessBurstTimeExist) {
                timeLine.add(new TimeSlot(startTime,currentProcess.name,time));
                continue;
            }
            for (int i = 1; i<= part3Quantum;i++)
            {
                time++;
                getNextArrivedProcesses(0);
                currentProcess.executionTime--;
                currentQuantumTime--;
                if (currentProcess.executionTime == 0)
                    break;
                isLessBurstTimeExist = checkLessBurstTime(currentProcess,currentQuantumTime);
                if (isLessBurstTimeExist)
                    continue;
            }
            if (currentProcess.executionTime == 0 || isLessBurstTimeExist)
            {
                timeLine.add(new TimeSlot(startTime,currentProcess.name,time));
                continue;
            }
            currentProcess.quantumTime+=2;
            if (processQueue.isEmpty())
                firstProcess = true;
            processQueue.addLast(currentProcess);
            timeLine.add(new TimeSlot(startTime,currentProcess.name,time));
        }
    }

    boolean checkHigherPriority(Process currentProcess,int currentQuantumTime)
    {
        int minP = currentProcess.priority;
        Process p = null;
        Iterator it = processQueue.iterator();
        while (it.hasNext())
        {
            Process process =(Process)it.next();
            if (process.priority < minP) {
                p = process;
                minP = process.priority;
            }
        }
        if (p == null)
            return false;
        processQueue.remove(p);
        processQueue.addFirst(p);
        currentProcess.quantumTime+=((int)Math.ceil(currentQuantumTime*0.5));
        processQueue.addLast(currentProcess);
        return true;
    }
    boolean checkLessBurstTime(Process currentProcess,int currentQuantumTime)
    {
        int minP = currentProcess.executionTime;
        Process p = null;
        Iterator it = processQueue.iterator();
        while (it.hasNext())
        {
            Process process =(Process)it.next();
            if (process.executionTime < minP) {
                p = process;
                minP = process.executionTime;
            }
        }
        if (p == null)
            return false;
        processQueue.remove(p);
        processQueue.addFirst(p);
        currentProcess.quantumTime+= currentQuantumTime;
        processQueue.addLast(currentProcess);
        return true;
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
        printQuantumHistory();

    }
    public void printQuantumHistory()
    {
        System.out.println("Quantum History");
        for (int i = 0; i < originalProcesses.size(); i++)
        {
            ArrayList<Integer> processQuantumHistory = quantumHistory.get(originalProcesses.get(i).name);
            System.out.print(originalProcesses.get(i).name + ": ");
            for (int j = 0; j < processQuantumHistory.size(); j++)
                System.out.print(processQuantumHistory.get(j) + " ");
            System.out.println();
        }
        System.out.println("----------------------------------------");
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

}
