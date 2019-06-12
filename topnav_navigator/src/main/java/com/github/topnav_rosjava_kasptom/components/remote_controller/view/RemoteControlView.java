package com.github.topnav_rosjava_kasptom.components.remote_controller.view;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;
import com.github.topnav_rosjava_kasptom.components.remote_controller.presenter.IRemoteControlPresenter;
import com.github.topnav_rosjava_kasptom.components.remote_controller.presenter.RemoteControlPresenter;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class RemoteControlView implements IRemoteControlView, Initializable {

    private IRemoteControlPresenter presenter;

    public RemoteControlView() {
        this.presenter = new RemoteControlPresenter();
    }

    public void onControlToggleClick() {
        presenter.toggleEnabled();
    }

    @Override
    public IBasePresenter getPresenter() {
        return this.presenter;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
