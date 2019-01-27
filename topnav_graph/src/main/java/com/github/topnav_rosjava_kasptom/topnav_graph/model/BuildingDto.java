package com.github.topnav_rosjava_kasptom.topnav_graph.model;

import java.util.List;

public class BuildingDto {
    private List<BaseIdentifiableDto> walls;
    private List<BaseIdentifiableDto> gates;
    private List<BaseIdentifiableDto> spaces;
    private List<BaseIdentifiableDto> markers;

    public List<BaseIdentifiableDto> getWalls() {
        return walls;
    }

    public void setWalls(List<BaseIdentifiableDto> walls) {
        this.walls = walls;
    }

    public List<BaseIdentifiableDto> getGates() {
        return gates;
    }

    public void setGates(List<BaseIdentifiableDto> gates) {
        this.gates = gates;
    }

    public List<BaseIdentifiableDto> getSpaces() {
        return spaces;
    }

    public void setSpaces(List<BaseIdentifiableDto> spaces) {
        this.spaces = spaces;
    }

    public List<BaseIdentifiableDto> getMarkers() {
        return markers;
    }

    public void setMarkers(List<BaseIdentifiableDto> markers) {
        this.markers = markers;
    }
}
