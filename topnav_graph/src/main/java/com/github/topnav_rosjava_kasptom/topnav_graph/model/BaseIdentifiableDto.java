package com.github.topnav_rosjava_kasptom.topnav_graph.model;

import lombok.Getter;

public class BaseIdentifiableDto {
    @Getter
    private String id;

    @Getter
    private String type;

    @Getter
    private String kind;
}
