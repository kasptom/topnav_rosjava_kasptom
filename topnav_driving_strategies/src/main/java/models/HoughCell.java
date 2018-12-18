package models;

public class HoughCell {
    private double angle;
    private double range;
    private int votes;

    public HoughCell(double angle, double range, int votes) {
        this.angle = angle;
        this.range = range;
        this.votes = votes;
    }

    public double getAngle() {
        return angle;
    }

    public double getAngleDegrees() {
        return angle / Math.PI * 180;
    }

    public double getRange() {
        return range;
    }

    public int getVotes() {
        return votes;
    }
}
