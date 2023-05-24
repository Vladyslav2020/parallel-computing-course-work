package org.kpi.coursework.massservicenetwork;

import java.util.Random;

public class DemandInputFlow {
    private final Random random = new Random();
    protected double arrivalInterval;
    private double nextDemandTime;
    protected int numberIncomingEvents;
    protected MassServiceEntry massServiceEntry;

    public DemandInputFlow(double arrivalInterval, MassServiceEntry massServiceEntry) {
        this.arrivalInterval = arrivalInterval;
        this.massServiceEntry = massServiceEntry;
    }

    public void createDemand(double arrivalTime) {
        nextDemandTime = arrivalTime + getDelay(arrivalInterval);
        numberIncomingEvents++;
        massServiceEntry.startService(arrivalTime);
    }

    public double getNextDemandTime() {
        return nextDemandTime;
    }

    public int getNumberIncomingDemands() {
        return numberIncomingEvents;
    }

    protected double getDelay(double lambda) {
        return -lambda * Math.log(1 - random.nextDouble());
    }
}
