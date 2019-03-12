package com.github.topnav_rosjava_kasptom.components.autopilot.presenter;

import java.io.FileNotFoundException;

public interface IAutopilotPresenter {
    void initializePresenter();

    void play();

    void pause();

    void stop();

    void setMarkers(String startMarkerId, String endMarkerId);

    void showGraph();

    void loadRoson();
}
