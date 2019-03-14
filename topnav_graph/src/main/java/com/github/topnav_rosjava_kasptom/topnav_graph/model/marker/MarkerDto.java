package com.github.topnav_rosjava_kasptom.topnav_graph.model.marker;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.BaseIdentifiableDto;
import lombok.Getter;

public class MarkerDto extends BaseIdentifiableDto {
    @Getter
    private ArUcoDto aruco;

    /**
     * LeftMarker, RightMarker
     */
    @Getter
    private String role;

    @Getter
    private String attachedToNodeId;

    @Getter
    private String spaceId;

    public String getLabel() {
        return String.format("%s, %s", aruco.getId(), getRole());
    }
}
