package org.kpi.coursework;

import org.kpi.coursework.massservicenetwork.*;
import org.kpi.coursework.massservicenetwork.parallel.*;

import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        runSequential();
        runParallel();
    }

    private static void runParallel() {
        double simulationTime = 1000;
        ParallelMassServiceSystem parallelMassServiceSystem1 = new ParallelMassServiceSystem(simulationTime, 5, 10, 1.2);
        ParallelMassServiceSystem parallelMassServiceSystem2 = new ParallelMassServiceSystem(simulationTime, 7, 8, 2);
        ParallelMassServiceSystem parallelMassServiceSystem3 = new ParallelMassServiceSystem(simulationTime, 2, 1, 1);
        ParallelMassSystemEntry parallelMassSystemEntry = new ParallelMassSystemEntry(parallelMassServiceSystem1, simulationTime);
        ParallelDemandInputFlow parallelDemandInputFlow = new ParallelDemandInputFlow(simulationTime, 0.2, parallelMassSystemEntry);
        ParallelMassServiceExit parallelMassServiceExit = new ParallelMassServiceExit(Set.of(parallelMassServiceSystem2, parallelMassServiceSystem3));
        ParallelAssociation parallelAssociation1 = new ParallelAssociation(parallelMassServiceSystem1, parallelMassServiceSystem2, 0.7);
        ParallelAssociation parallelAssociation2 = new ParallelAssociation(parallelMassServiceSystem1, parallelMassServiceSystem3, 0.3);
        new ParallelMassServiceNetwork(parallelDemandInputFlow, parallelMassSystemEntry, List.of(parallelMassServiceSystem1, parallelMassServiceSystem2, parallelMassServiceSystem3), parallelMassServiceExit, List.of(parallelAssociation1, parallelAssociation2))
                .run(simulationTime);
    }

    private static void runSequential() {
        MassServiceSystem massServiceSystem1 = new MassServiceSystem(5, 10, 1.2);
        MassServiceSystem massServiceSystem2 = new MassServiceSystem(7, 8, 2);
        MassServiceSystem massServiceSystem3 = new MassServiceSystem(2, 1, 1);
        MassServiceEntry massServiceEntry = new MassServiceEntry(massServiceSystem1);
        DemandInputFlow demandInputFlow = new DemandInputFlow(0.2, massServiceEntry);
        MassServiceExit massServiceExit = new MassServiceExit(Set.of(massServiceSystem2, massServiceSystem3));
        List<Association> associations = List.of(
                new Association(massServiceSystem1, massServiceSystem2, 0.7),
                new Association(massServiceSystem1, massServiceSystem3, 0.3)
        );
        new MassServiceNetwork(demandInputFlow, massServiceEntry, List.of(massServiceSystem1, massServiceSystem2, massServiceSystem3), massServiceExit, associations)
                .run(1000);
    }
}
