package com.github.topnav_rosjava_kasptom.components.remote_controller;

import com.github.topnav_rosjava_kasptom.components.remote_controller.presenter.IRemoteControlPresenter;
import com.github.topnav_rosjava_kasptom.services.IRosTopnavService;
import com.github.topnav_rosjava_kasptom.services.RosTopNavService;
import com.github.topnav_rosjava_kasptom.topnav_graph.constants.SteeringKeyDecoder;

public class RemoteControlCommandSender implements IRemoteControlCommandSender, IRemoteControlPresenter.OnControlCheckboxChangedListener {
    private final IRosTopnavService topnavService;
    private boolean isForwardPressed;
    private boolean isBackPressed;
    private boolean isLeftPressed;
    private boolean isRightPressed;

    private boolean isRemoteControlEnabled;

    public RemoteControlCommandSender() {
        topnavService = RosTopNavService.getInstance();
    }

    @Override
    public void driveForward() {
        if (!isRemoteControlEnabled) return;
        isForwardPressed = true;
        sendCommand();
    }

    @Override
    public void driveBackward() {
        if (!isRemoteControlEnabled) return;
        isBackPressed = true;
        sendCommand();
    }

    @Override
    public void turnLeft() {
        if (!isRemoteControlEnabled) return;
        isLeftPressed = true;
        sendCommand();
    }

    @Override
    public void turnRight() {
        if (!isRemoteControlEnabled) return;
        isRightPressed = true;
        sendCommand();
    }

    @Override
    public void stopDrivingForward() {
        if (!isRemoteControlEnabled) return;
        isForwardPressed = false;
        sendCommand();
    }

    @Override
    public void stopDrivingBackward() {
        if (!isRemoteControlEnabled) return;
        isBackPressed = false;
        sendCommand();
    }

    @Override
    public void stopTurningLeft() {
        if (!isRemoteControlEnabled) return;
        isLeftPressed = false;
        sendCommand();
    }

    @Override
    public void stopTurningRight() {
        if (!isRemoteControlEnabled) return;
        isRightPressed = false;
        sendCommand();
    }

    private void sendCommand() {
        this.topnavService.sendManualVelocityChange(SteeringKeyDecoder.encode(isForwardPressed, isBackPressed, isLeftPressed, isRightPressed));
    }

    @Override
    public void onCheckboxChanged(boolean isChecked) {
        isRemoteControlEnabled = isChecked;

        if (!isRemoteControlEnabled && (isForwardPressed || isBackPressed || isLeftPressed || isRightPressed)) {
            isForwardPressed = isBackPressed = isLeftPressed = isRightPressed = false;
            sendCommand();
        }
    }
}
