package org.kpi.coursework.massservicenetwork.parallel;

import org.kpi.coursework.massservicenetwork.MassServiceExit;
import org.kpi.coursework.massservicenetwork.MassServiceSystem;

import java.util.Set;

public class ParallelMassServiceExit extends MassServiceExit {

    public ParallelMassServiceExit(Set<ParallelMassServiceSystem> massServiceSystems) {
        super(massServiceSystems.stream().map(massServiceSystem -> (MassServiceSystem) massServiceSystem).collect(java.util.stream.Collectors.toSet()));
    }

    public void finishService(ParallelMassServiceSystem massServiceSystem) {
        if (massServiceSystems.contains(massServiceSystem)) {
            numberServedDemands++;
        }
    }
}
