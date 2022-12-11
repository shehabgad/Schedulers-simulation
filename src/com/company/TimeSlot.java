package com.company;

public class TimeSlot {
    public String name;
    public int startT;
    public int endT;

    public TimeSlot(int startT,String name,int endT)
    {
        this.startT = startT;
        this.name = name;
        this.endT = endT;
    }
}
