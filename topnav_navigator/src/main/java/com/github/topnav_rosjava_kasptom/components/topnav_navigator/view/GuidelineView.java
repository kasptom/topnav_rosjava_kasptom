package com.github.topnav_rosjava_kasptom.components.topnav_navigator.view;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;
import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.GuidelinePresenter;
import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.IGuidelinePresenter;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class GuidelineView implements IGuidelineView, Initializable {

    private final IGuidelinePresenter presenter;

    @FXML
    public TextField commandInput;

    @FXML
    public Button buttonStartStrategy;

    @FXML
    public Button buttonStopStrategy;

    @FXML
    public ChoiceBox<String> strategiesSelector;

    @FXML
    public Button buttonLookAhead;

    @FXML
    public Button buttonLookLeft;

    @FXML
    public Button buttonLookRight;

    public GuidelineView() {
        this.presenter = new GuidelinePresenter(this);
    }

    @FXML
    public void onStartStrategy() {
        presenter.onStartStrategy();
    }

    @FXML
    public void onStopStrategy() {
        presenter.onStopStrategy();
    }

    @FXML
    public void onLookAhead() {
        presenter.onChangeCameraDirection(RelativeDirection.AHEAD);
    }

    @FXML
    public void onLookLeft() {
        presenter.onChangeCameraDirection(RelativeDirection.AT_LEFT);
    }

    @FXML
    public void onLookRight() {
        presenter.onChangeCameraDirection(RelativeDirection.AT_RIGHT);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        strategiesSelector.getItems()
                .addAll(DrivingStrategy.DRIVING_STRATEGIES);
        strategiesSelector.getSelectionModel().selectFirst();
    }

    @Override
    public IBasePresenter getPresenter() {
        return presenter;
    }

    @Override
    public void onShowOptions() {

    }

    @Override
    public void onSelectStrategy(String strategyName) {
        this.presenter.onSelectStrategy(strategyName);
    }

    @Override
    public void onAddParam(String name) {

    }

    @Override
    public void onRemoveParam(String name) {

    }

    @Override
    public void onSendGuideline() {
        this.presenter.onStartStrategy();
    }

    @Override
    public void onShowError(String format) {

    }

    public void onStrategySelect() {
        String optionName = this.strategiesSelector.getSelectionModel().getSelectedItem();
        System.out.println(optionName);
        onSelectStrategy(optionName);
    }
}
