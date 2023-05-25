package org.kpi.coursework.massservicenetwork;

public class Connection {
    protected MassServiceSystem entrySystem;
    protected MassServiceSystem exitSystem;
    private final double probability;
    private int numberRejectedDemands;

    public Connection(MassServiceSystem entrySystem, MassServiceSystem exitSystem, double probability) {
        this.entrySystem = entrySystem;
        this.exitSystem = exitSystem;
        this.probability = probability;
        numberRejectedDemands = 0;
    }

    public void promoteDemand(double time) {
        entrySystem.releaseDemand(time);
        if (exitSystem.isBlocked()) {
            numberRejectedDemands++;
            return;
        }
        exitSystem.takeDemand(time);
    }

    public int getNumberRejectedDemands() {
        return numberRejectedDemands;
    }

    public MassServiceSystem getEntrySystem() {
        return entrySystem;
    }

    public MassServiceSystem getExitSystem() {
        return exitSystem;
    }

    public double getProbability() {
        return probability;
    }
}
