package org.kpi.coursework.massservicenetwork.parallel;

import org.kpi.coursework.massservicenetwork.Association;
import org.kpi.coursework.massservicenetwork.MassServiceSystem;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParallelMassServiceSystem extends MassServiceSystem implements Runnable {
    private final Set<Event> events;
    private final double simulationTime;
    private final Lock lock = new ReentrantLock();
    private final Condition queueIsNotEmpty = lock.newCondition();
    private List<ParallelAssociation> parallelAssociations;
    private ParallelMassServiceExit parallelMassServiceExit;
    private double averageQueueSize;
    private double averageChannelLoad;
    private long numberRejectedDemands;

    public ParallelMassServiceSystem(double simulationTime, int channelsNumber, int queueSize, double meanServiceInterval) {
        super(channelsNumber, queueSize, meanServiceInterval);
        this.simulationTime = simulationTime;
        this.events = new TreeSet<>();
        this.numberRejectedDemands = 0;
    }

    @Override
    public void run() {
        double currentTime = 0;
        while (currentTime < simulationTime) {
            Event event;
            lock.lock();
            try {
                while (!eventCame()) {
                    queueIsNotEmpty.await();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
            event = getMinEvent();
            calculateStatisticMetrics(currentTime, event);
            currentTime = event.getTime();
            if (event.getEventType() == EventType.FINISH_WORK) {
                if (parallelAssociations != null) {
                    parallelAssociations.forEach(parallelAssociation -> ((ParallelMassServiceSystem) parallelAssociation.getExitSystem()).addEvent(event));
                }
                break;
            } else if (event.getEventType() == EventType.TAKE_DEMAND) {
                if (isBlocked()) {
                    numberRejectedDemands++;
                } else {
                    takeDemand(currentTime);
                    addEvent(new Event(EventType.RELEASE_DEMAND, getMinTime()));
                }
            } else {
                releaseDemand(currentTime);
                promoteDemand(currentTime);
            }
        }
    }

    private boolean eventCame() {
        lock.lock();
        try {
            if (events.size() > 0) {
                for (Event event : events) {
                    if (event.getEventType() != EventType.RELEASE_DEMAND) {
                        return true;
                    }
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void releaseDemand(double time) {
        int channelId = getMinChannel();
        if (currentQueueSize > 0) {
            currentQueueSize--;
            addEvent(new Event(EventType.TAKE_DEMAND, time));
            if (isBlocked()) {
                setBlocked(false);
            }

        }
        releaseChannel(channelId);
        recalculateTime();
    }

    private void calculateStatisticMetrics(double currentTime, Event event) {
        averageChannelLoad += (event.getTime() - currentTime) * getNumberLoadedChannels() / simulationTime;
        averageQueueSize += (event.getTime() - currentTime) * getCurrentQueueSize() / simulationTime;
    }

    private Event getMinEvent() {
        lock.lock();
        try {
            Event minEvent = events.iterator().next();
            events.remove(minEvent);
            return minEvent;
        } finally {
            lock.unlock();
        }
    }

    private void promoteDemand(double time) {
        if (parallelAssociations != null) {
            double probability = Math.random();
            double sum = 0;
            for (Association association : parallelAssociations) {
                sum += association.getProbability();
                if (probability <= sum) {
                    association.promoteDemand(time);
                    return;
                }
            }
        }
        if (parallelMassServiceExit != null) {
            parallelMassServiceExit.finishService(this);
        }
    }

    public void addEvent(Event event) {
        lock.lock();
        try {
            events.add(event);
            queueIsNotEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void setParallelMassServiceExit(ParallelMassServiceExit parallelMassServiceExit) {
        this.parallelMassServiceExit = parallelMassServiceExit;
    }

    public void setParallelAssociations(List<ParallelAssociation> parallelAssociations) {
        this.parallelAssociations = parallelAssociations;
    }

    public long getNumberRejectedDemands() {
        return numberRejectedDemands;
    }

    public double getAverageQueueSize() {
        return averageQueueSize;
    }

    public double getAverageChannelLoad() {
        return averageChannelLoad;
    }
}
