package com.github.topnav_rosjava_kasptom.topnav_shared.model;

import java.util.List;

public class Feedback {
    private final long timestamp;
    private final List<Topology> topologies;

    public Feedback(long timestamp, List<Topology> topologies) {
        this.timestamp = timestamp;
        this.topologies = topologies;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Topology> getTopologies() {
        return topologies;
    }
}
