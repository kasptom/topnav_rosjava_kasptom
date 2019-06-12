package com.github.topnav_rosjava_kasptom.components.remote_controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyReleaseHandler implements EventHandler<KeyEvent> {

    private final IRemoteControlCommandSender sender;

    public KeyReleaseHandler(IRemoteControlCommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getCode() == KeyCode.W) {
            sender.stopDrivingForward();
        } else if (event.getCode() == KeyCode.S) {
            sender.stopDrivingBackward();
        } else if (event.getCode() == KeyCode.A) {
            sender.stopTurningLeft();
        } else if (event.getCode() == KeyCode.D) {
            sender.stopTurningRight();
        }
    }
}
