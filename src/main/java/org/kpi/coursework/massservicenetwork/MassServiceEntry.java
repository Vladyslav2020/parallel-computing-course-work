package org.kpi.coursework.massservicenetwork;

public class MassServiceEntry {
    protected MassServiceSystem massServiceSystem;
    protected int numberRejectedDemands;

    public MassServiceEntry(MassServiceSystem massServiceSystems) {
        this.massServiceSystem = massServiceSystems;
    }

    public void startService(double time) {
        if (massServiceSystem.isBlocked()) {
            numberRejectedDemands++;
            return;
        }
        massServiceSystem.takeDemand(time);
    }

    public int getNumberRejectedDemands() {
        return numberRejectedDemands;
    }
}
