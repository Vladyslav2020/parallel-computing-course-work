package org.kpi.coursework.massservicenetwork;

import java.util.Set;

public class MassServiceExit {
    protected Set<MassServiceSystem> massServiceSystems;
    protected int numberServedDemands;

    public MassServiceExit(Set<MassServiceSystem> massServiceSystems) {
        this.massServiceSystems = massServiceSystems;
    }

    public void finishService(MassServiceSystem massServiceSystem, double time) {
        if (massServiceSystems.contains(massServiceSystem)) {
            massServiceSystem.releaseDemand(time);
            numberServedDemands++;
        }
    }

    public int getNumberServedDemands() {
        return numberServedDemands;
    }

    public Set<MassServiceSystem> getMassServiceSystems() {
        return massServiceSystems;
    }
}
