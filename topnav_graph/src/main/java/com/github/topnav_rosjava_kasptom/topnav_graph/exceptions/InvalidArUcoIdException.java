package com.github.topnav_rosjava_kasptom.topnav_graph.exceptions;

public class InvalidArUcoIdException extends Throwable {
    public InvalidArUcoIdException(String message, String arucoId) {
        super(String.format(message, arucoId));
    }
}
