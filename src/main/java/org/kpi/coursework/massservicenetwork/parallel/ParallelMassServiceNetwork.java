package org.kpi.coursework.massservicenetwork.parallel;

import org.kpi.coursework.massservicenetwork.Association;
import org.kpi.coursework.massservicenetwork.MassServiceExit;
import org.kpi.coursework.massservicenetwork.MassServiceNetwork;
import org.kpi.coursework.massservicenetwork.MassServiceSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class ParallelMassServiceNetwork extends MassServiceNetwork {

    public ParallelMassServiceNetwork(ParallelDemandInputFlow demandInputFlow, ParallelMassSystemEntry massServiceEntry, List<ParallelMassServiceSystem> massServiceSystems, MassServiceExit massServiceExit, List<Association> associations) {
        super(demandInputFlow, massServiceEntry, massServiceSystems.stream().map(parallelMassServiceSystem -> (MassServiceSystem) parallelMassServiceSystem).collect(Collectors.toList()), massServiceExit, associations);
    }

    public void run(double simulationTime) {
        ExecutorService pool = Executors.newFixedThreadPool(4);

        long startTime = System.currentTimeMillis();
        List<Future<?>> futures = new ArrayList<>();
        futures.add(pool.submit((ParallelDemandInputFlow) demandInputFlow));
        massServiceSystems.stream().map(massServiceSystem -> (ParallelMassServiceSystem) massServiceSystem).forEach(massServiceSystem -> {
            List<ParallelAssociation> filteredAssociations = associations.stream().map(association -> (ParallelAssociation) association)
                    .filter(association -> association.getEntrySystem() == massServiceSystem).toList();
            if (filteredAssociations.size() > 0) {
                massServiceSystem.setParallelAssociations(filteredAssociations);
            } else {
                massServiceSystem.setParallelMassServiceExit((ParallelMassServiceExit) massServiceExit);
            }
            futures.add(pool.submit(massServiceSystem));
        });
        pool.shutdown();
        futures.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime) + "ms");
        printStatistics(simulationTime);
    }

    private void printStatistics(Double simulationTime) {
        long numberRejectedDemands = massServiceSystems.stream().map(massServiceSystem -> (ParallelMassServiceSystem) massServiceSystem).mapToLong(ParallelMassServiceSystem::getNumberRejectedDemands).sum();
        System.out.println("Simulation time: " + simulationTime);
        System.out.println("Number incoming events: " + demandInputFlow.getNumberIncomingDemands());
        System.out.println("Number of served demands: " + massServiceExit.getNumberServedDemands());
        System.out.printf("Probability of rejection: %.4f\n", (double) numberRejectedDemands / demandInputFlow.getNumberIncomingDemands());
        for (int i = 0; i < massServiceSystems.size(); i++) {
            System.out.println("Mass service system " + (i + 1));
            System.out.println("Number of rejected demands: " + ((ParallelMassServiceSystem) massServiceSystems.get(i)).getNumberRejectedDemands());
            System.out.printf("Average queue size: %.4f\n", ((ParallelMassServiceSystem) massServiceSystems.get(i)).getAverageQueueSize());
            System.out.printf("Average channel load: %.4f\n", ((ParallelMassServiceSystem) massServiceSystems.get(i)).getAverageChannelLoad());
        }
    }
}
