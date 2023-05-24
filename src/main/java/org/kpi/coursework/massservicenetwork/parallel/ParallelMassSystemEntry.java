package org.kpi.coursework.massservicenetwork.parallel;

import org.kpi.coursework.massservicenetwork.MassServiceEntry;

public class ParallelMassSystemEntry extends MassServiceEntry {
    private final double simulationTime;

    public ParallelMassSystemEntry(ParallelMassServiceSystem massServiceSystem, double simulationTime) {
        super(massServiceSystem);
        this.simulationTime = simulationTime;
    }

    @Override
    public void startService(double time) {
        if (time == simulationTime) {
            ((ParallelMassServiceSystem) massServiceSystem).addEvent(new Event(EventType.FINISH_WORK, time));
        } else {
            ((ParallelMassServiceSystem) massServiceSystem).addEvent(new Event(EventType.TAKE_DEMAND, time));
        }
    }
}
