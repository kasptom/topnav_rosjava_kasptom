package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.navigation;

public interface INavigator extends SceneChangeListener {
    void addNavigationChangeListener(NavigationChangeListener listener);

    void removeAllNavigationChangeListeners();
}
