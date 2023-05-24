package org.kpi.coursework.massservicenetwork.parallel;

import org.kpi.coursework.massservicenetwork.Association;
import org.kpi.coursework.massservicenetwork.MassServiceSystem;

public class ParallelAssociation extends Association {
    public ParallelAssociation(MassServiceSystem entrySystem, MassServiceSystem exitSystem, double probability) {
        super(entrySystem, exitSystem, probability);
    }

    @Override
    public void promoteDemand(double time) {
        ((ParallelMassServiceSystem) exitSystem).addEvent(new Event(EventType.TAKE_DEMAND, time));
    }
}
