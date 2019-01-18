package com.github.topnav_rosjava_kasptom.components.topnav_navigator.view;

import com.github.topnav_rosjava_kasptom.components.IBasePresenter;
import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.GuidelinePresenter;
import com.github.topnav_rosjava_kasptom.components.topnav_navigator.presenter.IGuidelinePresenter;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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

    @FXML
    public VBox strategyParamsContainer;

    @FXML
    public ChoiceBox<String> parametersSelector;

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

        parametersSelector.getItems()
                .addAll(DrivingStrategy.PARAM_NAMES);
        parametersSelector.getSelectionModel().selectFirst();
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
    public void onAddParam() {
        HBox paramEntryBox = new HBox();
        TextField nameTextField = new TextField("Name");
        TextField valueTextField = new TextField("Value");

        String paramName = parametersSelector.getValue();
        nameTextField.setText(paramName);
        nameTextField.setEditable(false);

        nameTextField.setPrefWidth(100);
        valueTextField.setPrefWidth(100);

        paramEntryBox.getChildren()
                .add(nameTextField);
        paramEntryBox.getChildren()
                .add(valueTextField);

        strategyParamsContainer
                .getChildren()
                .add(paramEntryBox);
    }

    @Override
    public void onRemoveParam() {
        if (strategyParamsContainer.getChildren().isEmpty()) {
            return;
        }

        strategyParamsContainer.getChildren()
                .remove(strategyParamsContainer.getChildren().size() - 1);
    }

    @Override
    public List<GuidelineParam> getGuidelineParams() {
        return strategyParamsContainer
                .getChildren()
                .stream()
                .map(hBox -> {
                    String paramName = ((TextField) ((HBox) hBox).getChildren()
                            .get(0)).getText();
                    String paramValue = ((TextField) ((HBox) hBox).getChildren()
                            .get(1)).getText();
                    return new GuidelineParam(paramName, paramValue, "String");
                })
                .collect(Collectors.toList());
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

    @FXML
    public void onAddClicked() {
        onAddParam();
    }

    @FXML
    public void onRemoveClicked() {
        onRemoveParam();
    }
}
