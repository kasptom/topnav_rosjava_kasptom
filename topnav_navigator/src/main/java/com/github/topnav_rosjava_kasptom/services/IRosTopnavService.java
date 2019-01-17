package com.github.topnav_rosjava_kasptom.services;

import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;

public interface IRosTopnavService {
    void onInit();
    void onDestroy();

    void startStrategy(String strategyName);

    void stopStrategy(String strategyName);

    void changeCameraDirection(RelativeDirection relativeDirection);

    void setOnFeedbackChangeListener(OnFeedbackChangeListener listener);
}
