package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.navigation;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.SceneEvent;

public interface ISceneChangeListener {
    void onSceneChanged(SceneEvent event);
}
