package com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation;

import lombok.Getter;

public class NodeNodeRosonDto extends BaseRosonRelationDto {
    @Getter
    private String nodeFromId;

    @Getter
    private String nodeToId;
}
