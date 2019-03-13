package com.github.topnav_rosjava_kasptom.components.autopilot.presenter;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;

public interface IAutopilotPresenter extends IBasePresenter {
    void initializePresenter();

    void play();

    void pause();

    void stop();

    void setMarkers(String startMarkerId, String endMarkerId);

    void showGraph();

    void loadRoson();
}
