package com.github.topnav_rosjava_kasptom.topnav_shared.model;

import java.util.List;

public class Feedback {
    private final long timestamp;
    private final List<Topology> topologies;
    private final String strategyName;

    public Feedback(long timestamp, List<Topology> topologies, String strategyName) {
        this.timestamp = timestamp;
        this.topologies = topologies;
        this.strategyName = strategyName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Topology> getTopologies() {
        return topologies;
    }

    public String getStrategyName() {
        return strategyName;
    }
}
