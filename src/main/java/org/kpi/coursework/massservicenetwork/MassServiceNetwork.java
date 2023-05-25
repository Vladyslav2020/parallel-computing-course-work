package org.kpi.coursework.massservicenetwork;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MassServiceNetwork {
    protected DemandInputFlow demandInputFlow;
    protected MassServiceEntry massServiceEntry;
    protected List<MassServiceSystem> massServiceSystems;
    protected MassServiceExit massServiceExit;
    protected List<Connection> connections;
    private final List<Double> averageQueueSizes;
    private final List<Double> averageChannelLoads;
    private double currentTime;

    public MassServiceNetwork(DemandInputFlow demandInputFlow, MassServiceEntry massServiceEntry, List<MassServiceSystem> massServiceSystems, MassServiceExit massServiceExit, List<Connection> connections) {
        this.demandInputFlow = demandInputFlow;
        this.massServiceEntry = massServiceEntry;
        this.massServiceSystems = massServiceSystems;
        this.massServiceExit = massServiceExit;
        this.connections = connections;
        this.averageQueueSizes = new ArrayList<>();
        this.averageChannelLoads = new ArrayList<>();
        for (int i = 0; i < massServiceSystems.size(); i++) {
            averageQueueSizes.add(0.0);
            averageChannelLoads.add(0.0);
        }
    }

    public void run(double simulationTime) {
        long startTime = System.currentTimeMillis();
        currentTime = 0;
        while (currentTime < simulationTime) {
            double nextDemandTime = demandInputFlow.getNextDemandTime();
            double minServiceTime = Double.MAX_VALUE;
            int minServiceIndex = -1;
            for (int i = 0; i < massServiceSystems.size(); i++) {
                if (massServiceSystems.get(i).getMinTime() < minServiceTime) {
                    minServiceTime = massServiceSystems.get(i).getMinTime();
                    minServiceIndex = i;
                }
            }
            double minTime = Math.min(nextDemandTime, minServiceTime);
            calculateAverageMetrics(simulationTime, minTime);
            if (minTime == nextDemandTime) {
                demandInputFlow.createDemand(minTime);
            } else {
                boolean foundInConnection = false;
                MassServiceSystem currentMassServiceSystem = massServiceSystems.get(minServiceIndex);
                double probability = Math.random();
                double sum = 0;
                for (Connection connection : connections.stream()
                        .filter(connection -> connection.getEntrySystem() == currentMassServiceSystem).toList()) {
                    sum += connection.getProbability();
                    if (probability <= sum) {
                        connection.promoteDemand(minTime);
                        foundInConnection = true;
                        break;
                    }
                }
                if (!foundInConnection) {
                    if (massServiceExit.getMassServiceSystems().contains(currentMassServiceSystem)) {
                        massServiceExit.finishService(currentMassServiceSystem, minTime);
                    }
                }
            }
            currentTime = minTime;
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Total time: " + (endTime - startTime));
        printStatistics(simulationTime);
    }

    private void calculateAverageMetrics(double simulationTime, double minTime) {
        for (int i = 0; i < massServiceSystems.size(); i++) {
            averageQueueSizes.set(i, averageQueueSizes.get(i) + (minTime - currentTime) * massServiceSystems.get(i).getCurrentQueueSize() / simulationTime);
            averageChannelLoads.set(i, averageChannelLoads.get(i) + (minTime - currentTime) * massServiceSystems.get(i).getNumberLoadedChannels() / simulationTime);
        }
    }

    private void printStatistics(double simulationTime) {
        int numberRejectedDemands = massServiceEntry.getNumberRejectedDemands();
        for (Connection connection : connections) {
            numberRejectedDemands += connection.getNumberRejectedDemands();
        }
        System.out.println("Simulation time: " + simulationTime);
        System.out.println("Time: " + Math.round(currentTime));
        System.out.println("Number incoming events: " + demandInputFlow.getNumberIncomingDemands());
        System.out.println("Number of served demands: " + massServiceExit.getNumberServedDemands());
        System.out.printf("Probability of rejection: %.4f\n", (double) numberRejectedDemands / demandInputFlow.getNumberIncomingDemands());
        for (int i = 0; i < massServiceSystems.size(); i++) {
            System.out.println("Mass service system " + (i + 1));
            System.out.println("Number of rejected demands: " + getNumberOfRejectedDemands(massServiceSystems.get(i)));
            System.out.printf("Average queue size: %.4f\n", averageQueueSizes.get(i));
            System.out.printf("Average channel load: %.4f\n", averageChannelLoads.get(i));
        }
    }

    private long getNumberOfRejectedDemands(MassServiceSystem massServiceSystem) {
        Optional<Connection> optionalConnection = connections.stream().filter(association -> association.getExitSystem() == massServiceSystem).findFirst();
        return optionalConnection.map(Connection::getNumberRejectedDemands).orElseGet(() -> massServiceEntry.getNumberRejectedDemands());
    }
}
