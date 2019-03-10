package com.github.topnav_rosjava_kasptom.topnav_graph.exceptions;

public class InvalidRosonNodeKindException extends Throwable{
    public InvalidRosonNodeKindException(String expectedNodeKind, String nodeKind) {
        super(String.format("Expected: %s, Given: %s", expectedNodeKind, nodeKind));
    }
}
