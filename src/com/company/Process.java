package com.company;

public class Process {
    public String name;
    public int priority;
    public int arrivalTime;
    public int executionTime;
    public int originalExecTime;
    public int quantumTime;
    public Process(String name,int priority,int arrivalTime,int executionTime)
    {
        this.name = name;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.executionTime = executionTime;
        this.originalExecTime = executionTime;
        this.quantumTime = 0;
    }
    public boolean equals(Object obj)
    {
        Process process = (Process) obj;
        return this.name.equals(process.name);
    }
}
