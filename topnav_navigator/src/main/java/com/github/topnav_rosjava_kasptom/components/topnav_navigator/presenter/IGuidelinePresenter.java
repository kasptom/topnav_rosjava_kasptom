package com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;

public interface IGuidelinePresenter extends IBasePresenter {
    void onSelectStrategy(String strategy);

    void onStartStrategy();

    void onStopStrategy();

    void onChangeCameraDirection(RelativeDirection ahead);
}
