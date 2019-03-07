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
}
