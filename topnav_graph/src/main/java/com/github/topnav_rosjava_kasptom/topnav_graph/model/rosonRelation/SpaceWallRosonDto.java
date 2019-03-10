package com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation;

import lombok.Getter;

public class SpaceWallRosonDto extends BaseRosonRelationDto {

    public SpaceWallRosonDto(String spaceId, String wallId, String type) {
        this.type = type;
        this.spaceId = spaceId;
        this.wallId = wallId;
    }

    @Getter
    private String spaceId;

    @Getter
    private String wallId;
}
