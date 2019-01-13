package com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;

public interface IGuidelinePresenter extends IBasePresenter {
    void onSelectStrategy(String strategy);

    void onStartStrategy();

    void onStopStrategy();
}
