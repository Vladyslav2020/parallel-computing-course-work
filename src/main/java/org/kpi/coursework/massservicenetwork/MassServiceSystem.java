package org.kpi.coursework.massservicenetwork;

import java.util.Random;

public class MassServiceSystem {
    private final Random random = new Random();
    private final int channelsNumber;
    private final int queueSize;
    private final double[] serviceEndTime;
    private final int[] channelStates;
    protected int currentQueueSize;
    private final double meanServiceInterval;
    private double minTime;
    private int minChannel;
    private boolean isBlocked;

    public MassServiceSystem(int channelsNumber, int queueSize, double meanServiceInterval) {
        this.channelsNumber = channelsNumber;
        this.queueSize = queueSize;
        this.serviceEndTime = new double[channelsNumber];
        this.channelStates = new int[channelsNumber];
        this.currentQueueSize = 0;
        this.meanServiceInterval = meanServiceInterval;
        isBlocked = false;

        for (int i = 0; i < channelsNumber; i++) {
            serviceEndTime[i] = Double.MAX_VALUE;
            channelStates[i] = 0;
        }

        recalculateTime();
    }

    public void takeDemand(double time) {
        boolean hasFreeChannel = false;
        for (int i = 0; i < channelsNumber; i++) {
            if (channelStates[i] == 0) {
                hasFreeChannel = true;
                takeDemandForChannel(i, time);
                recalculateTime();
                break;
            }
        }

        if (!hasFreeChannel && currentQueueSize < queueSize) {
            currentQueueSize++;

            if (currentQueueSize == queueSize) {
                isBlocked = true;
            }
        }
    }

    public void releaseDemand(double time) {
        int channelId = getMinChannel();
        if (currentQueueSize > 0) {
            currentQueueSize--;

            if (isBlocked) {
                isBlocked = false;
            }

            takeDemandForChannel(channelId, time);
        } else {
            releaseChannel(channelId);
        }
        recalculateTime();
    }

    public int getCurrentQueueSize() {
        return currentQueueSize;
    }

    private void takeDemandForChannel(int channelId, double time) {
        channelStates[channelId] = 1;
        serviceEndTime[channelId] = time + getDelay(meanServiceInterval);
    }

    protected void releaseChannel(int channelId) {
        channelStates[channelId] = 0;
        serviceEndTime[channelId] = Double.MAX_VALUE;
    }

    public long getNumberLoadedChannels() {
        int sum = 0;
        for (int i = 0; i < getNumChannel(); i++) {
            sum += channelStates[i];
        }
        return sum;
    }

    public int getNumChannel() {
        return channelsNumber;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public double getMinTime() {
        return minTime;
    }

    public int getMinChannel() {
        return minChannel;
    }

    protected void recalculateTime() {
        minTime = serviceEndTime[0];
        minChannel = 0;
        for (int i = 1; i < channelsNumber; i++) {
            if (serviceEndTime[i] < minTime) {
                minTime = serviceEndTime[i];
                minChannel = i;
            }
        }
    }

    private double getDelay(double mean) {
        return -mean * Math.log(1 - random.nextDouble());
    }
}
