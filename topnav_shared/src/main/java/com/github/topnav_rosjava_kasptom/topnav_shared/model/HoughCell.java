package com.github.topnav_rosjava_kasptom.topnav_shared.model;

public class HoughCell implements Comparable<HoughCell> {
    private double angle;
    private double angleDeg;
    private double range;
    private int votes;

    public HoughCell(double angle, double range, int votes) {
        this.angle = angle;
        this.angleDeg = angle / Math.PI * 180;
        this.range = range;
        this.votes = votes;
    }

    public double getAngle() {
        return angle;
    }

    public double getAngleDegrees() {
        return angleDeg;
    }

    public double getRange() {
        return range;
    }

    public int getVotes() {
        return votes;
    }

    @Override
    public int compareTo(HoughCell other) {
        return Integer.compare(this.votes, other.votes);
    }
}
