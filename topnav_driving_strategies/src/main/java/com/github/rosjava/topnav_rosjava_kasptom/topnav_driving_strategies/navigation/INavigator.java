package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.navigation;

public interface INavigator extends ISceneChangeListener {
    void addNavigationChangeListener(NavigationChangeListener listener);

    void removeAllNavigationChangeListeners();
}
