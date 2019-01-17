package com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter;

import com.github.topnav_rosjava_kasptom.components.topnav_navigator.view.IGuidelineView;
import com.github.topnav_rosjava_kasptom.services.IRosTopnavService;
import com.github.topnav_rosjava_kasptom.services.RosTopNavService;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy.DRIVING_STRATEGY_IDLE;

public class GuidelinePresenter implements IGuidelinePresenter {

    private final IRosTopnavService rosTopnavService;
    private final IGuidelineView view;

    private String strategyName = DRIVING_STRATEGY_IDLE;

    public GuidelinePresenter(IGuidelineView view) {
        this.rosTopnavService = RosTopNavService.getInstance();
        this.view = view;
    }

    @Override
    public void onInit() {
        rosTopnavService.onInit();
    }

    @Override
    public void onDestroy() {
        rosTopnavService.onDestroy();
    }

    @Override
    public void onSelectStrategy(String strategy) {
        strategyName = strategy;
    }

    @Override
    public void onStartStrategy() {
        rosTopnavService.startStrategy(this.strategyName);
    }

    @Override
    public void onStopStrategy() {
        rosTopnavService.stopStrategy(strategyName);
    }

    @Override
    public void onChangeCameraDirection(RelativeDirection relativeDirection) {
        rosTopnavService.changeCameraDirection(relativeDirection);
    }
}
