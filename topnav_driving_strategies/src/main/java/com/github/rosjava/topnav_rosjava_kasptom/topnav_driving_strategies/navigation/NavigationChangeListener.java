package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.navigation;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.Driving;

public interface NavigationChangeListener {
    void onNavigationChange(Driving driving);
}
