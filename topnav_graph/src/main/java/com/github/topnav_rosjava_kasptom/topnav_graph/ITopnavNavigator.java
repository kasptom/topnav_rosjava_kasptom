package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_shared.listeners.OnFeedbackChangeListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.listeners.OnGuidelineChangeListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;

import java.util.List;

public interface ITopnavNavigator extends OnFeedbackChangeListener {
    void showGraph();

    List<Guideline> createGuidelines(String startMarkerId, String startArUcoId);

    List<Guideline> createGuidelines(String startMarkerId, String startArUcoId, boolean isDeadReckoningEnabled, String fullRobotRotationMs);

    void start();

    void pause();

    void stop();

    void setOnGuidelineChangeListner(OnGuidelineChangeListener listener);

    List<Guideline> getGuidelines();

    int getCurrentGuidelineIndex();
}
