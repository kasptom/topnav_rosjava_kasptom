package com.github.topnav_rosjava_kasptom.components.remote_controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.HashSet;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;
import static javafx.scene.input.KeyEvent.KEY_RELEASED;

public class KeyPressHandler implements EventHandler<javafx.scene.input.KeyEvent> {
    private final IRemoteControlCommandSender sender;
    private final HashSet<KeyCode> pressedKeys;

    public KeyPressHandler(IRemoteControlCommandSender commandSender) {
        this.sender = commandSender;
        pressedKeys = new HashSet<>(4);
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getEventType().equals(KEY_PRESSED)) {
            handleKeyPressEvent(event);
        } else if (event.getEventType().equals(KEY_RELEASED)) {
            handleKeyReleasedEvent(event);
        }
    }

    private void handleKeyReleasedEvent(KeyEvent event) {
        pressedKeys.remove(event.getCode());

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

    private void handleKeyPressEvent(KeyEvent event) {
        if (pressedKeys.contains(event.getCode())) {
            return;
        }

        pressedKeys.add(event.getCode());

        if (event.getCode() == KeyCode.W) {
            sender.driveForward();
        } else if (event.getCode() == KeyCode.S) {
            sender.driveBackward();
        } else if (event.getCode() == KeyCode.A) {
            sender.turnLeft();
        } else if (event.getCode() == KeyCode.D) {
            sender.turnRight();
        }
    }
}
