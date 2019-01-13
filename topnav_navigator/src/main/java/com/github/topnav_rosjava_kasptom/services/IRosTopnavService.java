package com.github.topnav_rosjava_kasptom.services;

public interface IRosTopnavService {
    void onInit();
    void onDestroy();

    void startStrategy(String strategyName);

    void stopStrategy(String strategyName);
}
