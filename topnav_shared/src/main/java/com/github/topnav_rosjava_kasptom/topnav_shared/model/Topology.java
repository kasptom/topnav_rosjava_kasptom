package com.github.topnav_rosjava_kasptom.topnav_shared.model;

public class Topology {
    private long timestamp;
    private String identity;
    private String relativeAlignment;
    private String relativeDirection;
    private String relativeDistance;

    public Topology(long timestamp, String identity, String relativeAlignment, String relativeDirection, String relativeDistance) {
        this.timestamp = timestamp;
        this.identity = identity;
        this.relativeAlignment = relativeAlignment;
        this.relativeDirection = relativeDirection;
        this.relativeDistance = relativeDistance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getIdentity() {
        return identity;
    }

    public String getRelativeAlignment() {
        return relativeAlignment;
    }

    public String getRelativeDirection() {
        return relativeDirection;
    }

    /**
     * @see RelativeDistance#toString()
     * @return the string representation of the relative distance
     */
    public String getRelativeDistance() {
        return relativeDistance;
    }
}
