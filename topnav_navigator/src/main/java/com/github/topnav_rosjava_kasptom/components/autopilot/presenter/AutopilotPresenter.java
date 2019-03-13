package com.github.topnav_rosjava_kasptom.components.autopilot.presenter;

import com.github.topnav_rosjava_kasptom.components.autopilot.view.IAutopilotView;
import com.github.topnav_rosjava_kasptom.services.*;
import com.github.topnav_rosjava_kasptom.topnav_graph.RosonParser;
import com.github.topnav_rosjava_kasptom.topnav_graph.TopologicalNavigator;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeKindException;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.PropertyKeys;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Feedback;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class AutopilotPresenter implements IAutopilotPresenter, OnFeedbackChangeListener {
    private final IRosTopnavService rosService;
    private final IAutopilotView autopilotView;
    private TopologicalNavigator navigator;
    private IPropertiesService propertiesService;

    public AutopilotPresenter(IAutopilotView autopilotView) {
        this.autopilotView = autopilotView;
        rosService = RosTopNavService.getInstance();
        propertiesService = PropertiesService.getInstance();
    }

    @Override
    public void initializePresenter() {
        rosService.addOnFeedbackChangeListener(this);
        String rosonFileName = propertiesService.getProperty(PropertyKeys.PROPERTY_KEY_ROSON_FILE_PATH);

        if (propertiesService.getProperty(PropertyKeys.PROPERTY_KEY_USE_EXAMPLE_ROSON).equals("true")) {
            loadDefaultRosonFile(rosonFileName);
        } else {
            loadRosonFile(rosonFileName);
        }
    }

    @Override
    public void play() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void setMarkers(String startMarkerId, String endMarkerId) {

    }

    @Override
    public void showGraph() {
        navigator.showGraph();
    }

    @Override
    public void loadRoson() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open *.roson file");
        File file = fileChooser.showOpenDialog(null);
        String fullFilePath = file.getPath();

        loadRosonFile(fullFilePath);
    }

    // FeedbackChangeListener
    @Override
    public void onFeedbackChange(Feedback feedback) {

    }

    private void loadRosonFile(String fullFilePath) {
        try {
            RosonParser parser = new RosonParser();

            RosonBuildingDto buildingDto = parser.parseFullPathFile(fullFilePath);
            navigator = new TopologicalNavigator(buildingDto);
            autopilotView.setShowGraphButtonEnabled(true);

            autopilotView.setDisplayedRosonPath(fullFilePath);
        } catch (InvalidRosonNodeIdException | InvalidRosonNodeKindException | IOException e) {
            autopilotView.setShowGraphButtonEnabled(false);
            e.printStackTrace();
        }
    }

    private void loadDefaultRosonFile(String rosonFileName) {
        try {
            RosonParser parser = new RosonParser();

            RosonBuildingDto buildingDto = parser.parse(rosonFileName);
            navigator = new TopologicalNavigator(buildingDto);
            autopilotView.setShowGraphButtonEnabled(true);
            autopilotView.setDisplayedRosonPath(rosonFileName);
        } catch (InvalidRosonNodeIdException | InvalidRosonNodeKindException | IOException e) {
            autopilotView.setShowGraphButtonEnabled(false);
            e.printStackTrace();
        }
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onDestroy() {

    }
}
