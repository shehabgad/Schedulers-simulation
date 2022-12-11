package com.company;

import java.util.Comparator;

public class ComparatorSFJ implements Comparator<Process> {
    public int compare(Process p1,Process p2)
    {
        int c=  p1.executionTime - p2.executionTime;
        if (c == 0)
            return p1.arrivalTime - p2.arrivalTime;
        return c;
    }
}
