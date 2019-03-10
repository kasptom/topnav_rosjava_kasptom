package com.github.topnav_rosjava_kasptom.topnav_graph.exceptions;

public class InvalidRosonNodeIdException extends Throwable{
    public InvalidRosonNodeIdException(String nodeId) {
        super(String.format("Invalid roson node id: %s", nodeId));
    }
}
