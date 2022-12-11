package com.company;

import java.util.Comparator;

public class ComparatorPriority implements Comparator<Process>{
    public int compare(Process p1,Process p2)
    {
        int c =  p1.priority - p2.priority;
        if (c == 0)
            return p1.arrivalTime - p2.arrivalTime;
        return c;
    }
}
