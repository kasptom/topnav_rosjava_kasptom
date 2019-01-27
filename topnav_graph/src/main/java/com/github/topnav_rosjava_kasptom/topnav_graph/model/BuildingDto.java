package com.github.topnav_rosjava_kasptom.topnav_graph.model;

import lombok.Getter;

import java.util.List;

public class BuildingDto {
    private @Getter List<BaseIdentifiableDto> walls;
    private @Getter List<BaseIdentifiableDto> gates;
    private @Getter List<BaseIdentifiableDto> spaces;
    private @Getter List<BaseIdentifiableDto> markers;
}
