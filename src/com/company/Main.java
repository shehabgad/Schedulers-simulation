package com.company;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static Scanner sc;
    public static ArrayList<Process> takeProcessesInput()
    {
        System.out.println("enter number of processes");
        int n = sc.nextInt();
        ArrayList<Process> processes = new ArrayList<Process>();
        System.out.println("Enter process name, priority, arrival time, execution time, quantum time for next " + n + " lines");
        sc.nextLine();
        for (int i = 0; i < n; i++)
        {

            String name = sc.next();
            int priority = sc.nextInt();
            int arrivalTime = sc.nextInt();
            int executionTime = sc.nextInt();
            int quantumTime = sc.nextInt();
            Process p = new Process(name,priority,arrivalTime,executionTime);
            p.quantumTime = quantumTime;
            processes.add(p);
        }
        return processes;
    }

    public static void roundRobingScheduler()
    {
        ArrayList<Process> processes = takeProcessesInput();

        System.out.println("Enter quantum");
        sc.nextLine();
        int quantum = sc.nextInt();

        System.out.println("Enter context switching");
        sc.nextLine();
        int contextSwitching = sc.nextInt();

        Scheduler round_robin = new RoundRobin_Scheduler(processes,contextSwitching,quantum);
        round_robin.runScheduler();
        round_robin.printOutput();
    }
    public static void  agScheduler()
    {
        ArrayList<Process> processes = takeProcessesInput();

        System.out.println("Enter context switching");
        sc.nextLine();
        int contextSwitching = sc.nextInt();

        Scheduler ag = new AG_Scheduler(processes,contextSwitching);
        ag.runScheduler();
        ag.printOutput();
    }
    public static void priorityScheduler()
    {
        ArrayList<Process> processes = takeProcessesInput();

        System.out.println("Enter context switching");
        sc.nextLine();
        int contextSwitching = sc.nextInt();

        System.out.println("Enter aging factor");
        sc.nextLine();
        int agingFactor = sc.nextInt();

        Scheduler priority = new Priority_Scheduler(processes,contextSwitching,agingFactor,true);
        priority.runScheduler();
        priority.printOutput();
    }
    public static void sfjScheduler()
    {
        ArrayList<Process> processes = takeProcessesInput();

        System.out.println("Enter context switching");
        sc.nextLine();
        int contextSwitching = sc.nextInt();

        Scheduler sfj = new SFJ_Scheudler(processes,contextSwitching);
        sfj.runScheduler();
        sfj.printOutput();
    }
    public static void main(String[] args) {
        sc = new Scanner(System.in);
        System.out.println("For preemptive sfj enter 1");
        System.out.println("For round robin enter 2");
        System.out.println("For preemptive priority enter 3");
        System.out.println("For AG enter 4");
        int choice = sc.nextInt();
        if (choice == 1)
            sfjScheduler();
        else if (choice == 2)
            roundRobingScheduler();
        else if (choice == 3)
            priorityScheduler();
        else
            agScheduler();

    }
}
// sfj input
//p1 0 0 8 0
//p2 0 1 4 0
//p3 0 2 9 0
//p4 0 3 5 0

// round robin input
//p1 0 0 4 0
//p2 0 1 5 0
//p3 0 2 2 0
//p4 0 3 1 0
//p5 0 4 6 0
//p6 0 6 3 0
//quantum time = 2


// preemptive priority input
// p1 2 0 1 0
// p2 6 1 7 0
// p3 3 2 3 0
// p4 5 3 6 0
// p5 4 4 5 0
// p6 10 5 15 0
// p7 9 15 8 0

//ag input
//p1 4 0 17 7
//p2 7 2 6 9
//p3 3 5 11 4
//p4 6 15 4 6