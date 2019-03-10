package com.github.topnav_rosjava_kasptom.topnav_graph.model;

import lombok.Getter;

public class PointDto {
    @Getter
    private double x;

    @Getter
    private double y;

    public PointDto(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static PointDto getAverage(PointDto first, PointDto second) {
        return new PointDto((first.x + second.x) / 2.0, (first.y + second.y) / 2.0);
    }
}
