package com.github.topnav_rosjava_kasptom.components.topnav_navigator.view;

import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.GuidelinePresenter;
import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.IGuidelinePresenter;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class GuidelineView implements IGuidelineView, Initializable {

    private final GuidelinePresenter presenter;

    @FXML
    public TextField commandInput;

    @FXML
    public Button buttonStartStrategy;

    @FXML
    public Button buttonStopStrategy;

    @FXML
    public ChoiceBox strategiesSelector;

    public GuidelineView() {
        this.presenter = new GuidelinePresenter(this);
    }

    @FXML
    public void onStartStrategy() {
        presenter.onStartStrategy();
    }

    @Override
    public IGuidelinePresenter getPresenter() {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        strategiesSelector.getItems()
                .addAll(DrivingStrategy.DRIVING_STRATEGIES);
        strategiesSelector.getSelectionModel().selectFirst();
    }

    public void onStrategySelect() {
        String optionName = (String) this.strategiesSelector.getSelectionModel().getSelectedItem();
        System.out.println(optionName);
        onSelectStrategy(optionName);
    }
}
