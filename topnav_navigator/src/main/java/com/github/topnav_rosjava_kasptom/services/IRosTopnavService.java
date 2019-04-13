package com.github.topnav_rosjava_kasptom.services;

import com.github.topnav_rosjava_kasptom.topnav_shared.listeners.OnFeedbackChangeListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;

import java.util.List;

public interface IRosTopnavService {
    void onInit();
    void onDestroy();

    void startStrategy(String strategyName, List<GuidelineParam> guidelineParams);

    void stopStrategy(String strategyName);

    void changeCameraDirection(RelativeDirection relativeDirection);

    void addOnFeedbackChangeListener(OnFeedbackChangeListener listener);
}
