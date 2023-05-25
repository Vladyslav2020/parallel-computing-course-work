package org.kpi.coursework;

import org.kpi.coursework.massservicenetwork.*;
import org.kpi.coursework.massservicenetwork.parallel.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    private static final int NUMBER_OF_PARALLEL_SYSTEMS = 500;
    private static final double SIMULATION_TIME = 10000;

    public static void main(String[] args) {
        runSequentialBinaryNetwork();
        System.out.println("\n\n\n");
        runParallelBinaryNetwork();
    }

    private static void runSequentialAlgoVerification() {
        MassServiceSystem massServiceSystem1 = new MassServiceSystem(6, 10, 1);
        MassServiceSystem massServiceSystem2 = new MassServiceSystem(5, 20, 1.8);
        MassServiceSystem massServiceSystem3 = new MassServiceSystem(3, 1, 1.5);
        MassServiceEntry massServiceEntry = new MassServiceEntry(massServiceSystem1);
        DemandInputFlow demandInputFlow = new DemandInputFlow(0.2, massServiceEntry);
        MassServiceExit massServiceExit = new MassServiceExit(Set.of(massServiceSystem2, massServiceSystem3));
        List<Connection> associations = List.of(
                new Connection(massServiceSystem1, massServiceSystem2, 0.7),
                new Connection(massServiceSystem1, massServiceSystem3, 0.3)
        );
        new MassServiceNetwork(demandInputFlow, massServiceEntry, List.of(massServiceSystem1, massServiceSystem2, massServiceSystem3), massServiceExit, associations)
                .run(1000);
    }

    private static void runParallelDistributedNetwork() {
        List<ParallelMassServiceSystem> parallelMassServiceSystems = new ArrayList<>();
        ParallelMassServiceSystem parallelMassServiceSystem1 = new ParallelMassServiceSystem(SIMULATION_TIME, 10, 10, 1.0);
        for (int i = 0; i < NUMBER_OF_PARALLEL_SYSTEMS; i++) {
            parallelMassServiceSystems.add(new ParallelMassServiceSystem(SIMULATION_TIME, 3, 5, 2));
        }
        List<Connection> parallelConnections = new ArrayList<>();
        for (ParallelMassServiceSystem parallelMassServiceSystem : parallelMassServiceSystems) {
            parallelConnections.add(new ParallelConnection(parallelMassServiceSystem1, parallelMassServiceSystem, (double) 1 / NUMBER_OF_PARALLEL_SYSTEMS));
        }
        ParallelMassSystemEntry parallelMassSystemEntry = new ParallelMassSystemEntry(parallelMassServiceSystem1, SIMULATION_TIME);
        ParallelDemandInputFlow parallelDemandInputFlow = new ParallelDemandInputFlow(SIMULATION_TIME, 0.1, parallelMassSystemEntry);
        ParallelMassServiceExit parallelMassServiceExit = new ParallelMassServiceExit(new HashSet<>(parallelMassServiceSystems));
        parallelMassServiceSystems.add(0, parallelMassServiceSystem1);
        new ParallelMassServiceNetwork(parallelDemandInputFlow, parallelMassSystemEntry, parallelMassServiceSystems, parallelMassServiceExit, parallelConnections)
                .run(SIMULATION_TIME);
    }

    private static void runSequentialDistributedNetwork() {
        MassServiceSystem massServiceSystem1 = new MassServiceSystem(10, 10, 1.0);
        List<MassServiceSystem> massServiceSystems = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PARALLEL_SYSTEMS; i++) {
            massServiceSystems.add(new MassServiceSystem(3, 5, 2));
        }
        MassServiceEntry massServiceEntry = new MassServiceEntry(massServiceSystem1);
        DemandInputFlow demandInputFlow = new DemandInputFlow(0.1, massServiceEntry);
        MassServiceExit massServiceExit = new MassServiceExit(new HashSet<>(massServiceSystems));
        List<Connection> connections = new ArrayList<>();
        for (MassServiceSystem massServiceSystem : massServiceSystems) {
            connections.add(new Connection(massServiceSystem1, massServiceSystem, (double) 1 / NUMBER_OF_PARALLEL_SYSTEMS));
        }
        massServiceSystems.add(0, massServiceSystem1);
        new MassServiceNetwork(demandInputFlow, massServiceEntry, massServiceSystems, massServiceExit, connections)
                .run(SIMULATION_TIME);
        System.out.println("\n");
    }

    private static void runParallelBinaryNetwork() {
        List<ParallelMassServiceSystem> parallelMassServiceSystems = new ArrayList<>();
        List<Connection> parallelConnections = new ArrayList<>();
        ParallelMassServiceSystem parallelMassServiceSystem1 = new ParallelMassServiceSystem(SIMULATION_TIME, 10, 10, 1.0);
        parallelMassServiceSystems.add(parallelMassServiceSystem1);
        int indexOfCurrentParent = 0;
        int numberElementsInLayer = 1;
        while (parallelMassServiceSystems.size() < NUMBER_OF_PARALLEL_SYSTEMS) {
            numberElementsInLayer *= 2;
            for (int i = 0; i < numberElementsInLayer && parallelMassServiceSystems.size() < NUMBER_OF_PARALLEL_SYSTEMS; i++) {
                for (int j = 0; j < 2; j++) {
                    if (parallelMassServiceSystems.size() < NUMBER_OF_PARALLEL_SYSTEMS) {
                        ParallelMassServiceSystem childServiceSystem = new ParallelMassServiceSystem(SIMULATION_TIME, 5, 5, 1.2);
                        parallelConnections.add(new ParallelConnection(parallelMassServiceSystems.get(indexOfCurrentParent), childServiceSystem, getProbability(numberElementsInLayer)));
                        parallelMassServiceSystems.add(childServiceSystem);
                    }
                }
                indexOfCurrentParent++;
            }
        }
        List<ParallelMassServiceSystem> parallelMassServiceSystemExit = new ArrayList<>();
        for (int i = indexOfCurrentParent; i < parallelMassServiceSystems.size(); i++) {
            parallelMassServiceSystemExit.add(parallelMassServiceSystems.get(i));
        }
        ParallelMassSystemEntry parallelMassSystemEntry = new ParallelMassSystemEntry(parallelMassServiceSystem1, SIMULATION_TIME);
        ParallelDemandInputFlow parallelDemandInputFlow = new ParallelDemandInputFlow(SIMULATION_TIME, 0.1, parallelMassSystemEntry);
        ParallelMassServiceExit parallelMassServiceExit = new ParallelMassServiceExit(new HashSet<>(parallelMassServiceSystemExit));
        new ParallelMassServiceNetwork(parallelDemandInputFlow, parallelMassSystemEntry, parallelMassServiceSystems, parallelMassServiceExit, parallelConnections)
                .run(SIMULATION_TIME);
    }

    private static void runSequentialBinaryNetwork() {
        List<MassServiceSystem> massServiceSystems = new ArrayList<>();
        List<Connection> connections = new ArrayList<>();
        MassServiceSystem massServiceSystem1 = new MassServiceSystem(10, 10, 1.0);
        massServiceSystems.add(massServiceSystem1);
        int indexOfCurrentParent = 0;
        int numberElementsInLayer = 1;
        while (massServiceSystems.size() < NUMBER_OF_PARALLEL_SYSTEMS) {
            numberElementsInLayer *= 2;
            for (int i = 0; i < numberElementsInLayer && massServiceSystems.size() < NUMBER_OF_PARALLEL_SYSTEMS; i++) {
                for (int j = 0; j < 2; j++) {
                    if (massServiceSystems.size() < NUMBER_OF_PARALLEL_SYSTEMS) {
                        MassServiceSystem childServiceSystem = new MassServiceSystem(5, 5, 1.2);
                        connections.add(new Connection(massServiceSystems.get(indexOfCurrentParent), childServiceSystem, getProbability(numberElementsInLayer)));
                        massServiceSystems.add(childServiceSystem);
                    }
                }
                indexOfCurrentParent++;
            }
        }
        List<MassServiceSystem> massServiceSystemExit = new ArrayList<>();
        for (int i = indexOfCurrentParent; i < massServiceSystems.size(); i++) {
            massServiceSystemExit.add(massServiceSystems.get(i));
        }
        MassServiceEntry massServiceEntry = new MassServiceEntry(massServiceSystem1);
        DemandInputFlow demandInputFlow = new DemandInputFlow(0.1, massServiceEntry);
        MassServiceExit massServiceExit = new MassServiceExit(new HashSet<>(massServiceSystemExit));
        new MassServiceNetwork(demandInputFlow, massServiceEntry, massServiceSystems, massServiceExit, connections)
                .run(SIMULATION_TIME);
    }

    private static double getProbability(int numberElementsInLayer) {
        if (numberElementsInLayer * 2 - 1 < NUMBER_OF_PARALLEL_SYSTEMS) {
            return 0.5;
        }
        return 1.0;
    }
}
