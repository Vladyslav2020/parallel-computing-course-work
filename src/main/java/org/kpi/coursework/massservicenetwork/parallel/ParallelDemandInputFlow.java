package org.kpi.coursework.massservicenetwork.parallel;

import org.kpi.coursework.massservicenetwork.DemandInputFlow;

public class ParallelDemandInputFlow extends DemandInputFlow implements Runnable {
    private final double simulationTime;

    public ParallelDemandInputFlow(double simulationTime, double arrivalInterval, ParallelMassSystemEntry massServiceEntry) {
        super(arrivalInterval, massServiceEntry);
        this.simulationTime = simulationTime;
    }

    @Override
    public void run() {
        double currentTime = 0;
        while (currentTime < simulationTime) {
            currentTime += getDelay(arrivalInterval);
            massServiceEntry.startService(currentTime);
            numberIncomingEvents++;
        }
        massServiceEntry.startService(simulationTime);
        numberIncomingEvents++;
    }
}
