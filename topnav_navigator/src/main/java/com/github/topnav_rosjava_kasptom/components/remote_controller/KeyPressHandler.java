package com.github.topnav_rosjava_kasptom.components.remote_controller;

import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyPressHandler implements EventHandler<javafx.scene.input.KeyEvent> {
    private final IRemoteControlCommandSender sender;

    public KeyPressHandler(IRemoteControlCommandSender commandSender) {
        this.sender = commandSender;
    }

    @Override
    public void handle(KeyEvent event) {
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
