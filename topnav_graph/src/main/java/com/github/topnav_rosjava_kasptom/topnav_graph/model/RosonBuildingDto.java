package com.github.topnav_rosjava_kasptom.topnav_graph.model;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.marker.MarkerDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.*;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

public class RosonBuildingDto {

    @Getter
    private List<NodeDto> nodes;

    @Getter
    private List<BaseIdentifiableDto> walls;

    @Getter
    private List<BaseIdentifiableDto> gates;

    @Getter
    private List<NodeDto> spaces;

    @Getter
    private List<MarkerDto> markers;

    @SerializedName("marker-nodes")
    @Getter
    private List<MarkerNodeRosonDto> markerNodes;

    @Getter
    @SerializedName("node-nodes")
    private List<NodeNodeRosonDto> nodeNodes;

    @Getter
    @SerializedName("space-walls")
    private List<SpaceWallRosonDto> spaceWalls;

    @Getter
    @SerializedName("space-gates")
    private List<SpaceGateRosonDto> spaceGates;

    @Getter
    @SerializedName("space-nodes")
    private List<SpaceNodeRosonDto> spaceNodes;

    @Getter
    @SerializedName("gate-nodes")
    private List<GateNodeRosonDto> gateNodes;
}
