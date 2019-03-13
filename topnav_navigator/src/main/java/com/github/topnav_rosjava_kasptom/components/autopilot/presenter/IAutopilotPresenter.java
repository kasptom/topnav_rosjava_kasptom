package com.github.topnav_rosjava_kasptom.components.autopilot.presenter;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidArUcoIdException;

public interface IAutopilotPresenter extends IBasePresenter {
    void initializePresenter();

    void play() throws InvalidArUcoIdException;

    void pause();

    void stop();

    void setMarkers(String startMarkerId, String endMarkerId);

    void showGraph();

    void loadRoson();
}
