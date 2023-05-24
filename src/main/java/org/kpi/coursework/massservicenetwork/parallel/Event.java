package org.kpi.coursework.massservicenetwork.parallel;

public class Event implements Comparable<Event> {
    private final EventType eventType;
    private final double time;

    public Event(EventType eventType, double time) {
        this.eventType = eventType;
        this.time = time;
    }

    public EventType getEventType() {
        return eventType;
    }

    public double getTime() {
        return time;
    }

    @Override
    public int compareTo(Event o) {
        if (time > o.getTime()) {
            return 1;
        } else if (time < o.getTime()) {
            return -1;
        }
        return 0;
    }
}
