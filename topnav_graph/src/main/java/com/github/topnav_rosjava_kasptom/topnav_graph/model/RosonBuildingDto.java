package com.github.topnav_rosjava_kasptom.topnav_graph.model;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.MarkerNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.NodeNodeRosonDto;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.List;

public class RosonBuildingDto {

    @Getter
    private List<BaseIdentifiableDto> nodes;

    @Getter
    private List<BaseIdentifiableDto> walls;

    @Getter
    private List<BaseIdentifiableDto> gates;

    @Getter
    private List<BaseIdentifiableDto> spaces;

    @Getter
    private List<BaseIdentifiableDto> markers;

    @SerializedName("marker-nodes")
    @Getter
    private List<MarkerNodeRosonDto> markerNodes;

    @Getter
    @SerializedName("node-nodes")
    private List<NodeNodeRosonDto> nodeNodes;
}
