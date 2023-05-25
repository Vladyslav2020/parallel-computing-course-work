package org.kpi.coursework.massservicenetwork.parallel;

import org.kpi.coursework.massservicenetwork.Connection;
import org.kpi.coursework.massservicenetwork.MassServiceSystem;

public class ParallelConnection extends Connection {
    public ParallelConnection(MassServiceSystem entrySystem, MassServiceSystem exitSystem, double probability) {
        super(entrySystem, exitSystem, probability);
    }

    @Override
    public void promoteDemand(double time) {
        ((ParallelMassServiceSystem) exitSystem).addEvent(new Event(EventType.TAKE_DEMAND, time));
    }
}
