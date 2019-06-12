package com.github.topnav_rosjava_kasptom.components.remote_controller.presenter;

public class RemoteControlPresenter implements IRemoteControlPresenter{
    private boolean isManualControlEnabled;
    private OnControlCheckboxChangedListener listener;

    @Override
    public void toggleEnabled() {
        isManualControlEnabled = !isManualControlEnabled;
        this.listener.onCheckboxChanged(isManualControlEnabled);
    }

    @Override
    public void setOnControlCheckboxChangedListener(OnControlCheckboxChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onDestroy() {
        this.listener.onCheckboxChanged(false);
    }
}
