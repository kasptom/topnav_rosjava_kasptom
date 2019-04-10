package com.github.topnav_rosjava_kasptom.components.autopilot.presenter;

import com.github.topnav_rosjava_kasptom.components.autopilot.view.IAutopilotView;
import com.github.topnav_rosjava_kasptom.services.*;
import com.github.topnav_rosjava_kasptom.topnav_graph.ITopnavNavigator;
import com.github.topnav_rosjava_kasptom.topnav_graph.RosonParser;
import com.github.topnav_rosjava_kasptom.topnav_graph.TopologicalNavigator;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidArUcoIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeKindException;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.PropertyKeys;
import com.github.topnav_rosjava_kasptom.topnav_shared.listeners.OnGuidelineChangeListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class AutopilotPresenter implements IAutopilotPresenter, OnGuidelineChangeListener {
    private final IAutopilotView autopilotView;
    private final IRosTopnavService rosService;
    private ITopnavNavigator navigator;
    private IPropertiesService propertiesService;

    private boolean isPaused;
    private boolean isStopped;
    private Guideline currentGuideline;

    public AutopilotPresenter(IAutopilotView autopilotView) {
        this.autopilotView = autopilotView;
        rosService = RosTopNavService.getInstance();
        propertiesService = PropertiesService.getInstance();
    }

    @Override
    public void initializePresenter() {
        isStopped = true;
        isPaused = false;

        String rosonFileName = propertiesService.getProperty(PropertyKeys.PROPERTY_KEY_ROSON_FILE_PATH);

        if (propertiesService.getProperty(PropertyKeys.PROPERTY_KEY_USE_EXAMPLE_ROSON).equals("true")) {
            loadDefaultRosonFile(rosonFileName);
        } else {
            loadRosonFile(rosonFileName);
        }

        rosService.addOnFeedbackChangeListener(navigator);
    }

    @Override
    public void play() {
        if (isStopped) {
            String startMarkerId = autopilotView.getStartMarkerId();
            String endMarkerId = autopilotView.getEndMarkerId();

            navigator.createGuidelines(startMarkerId, endMarkerId);

            navigator.start();
            isStopped = false;
        } else if (isPaused) {
            isPaused = false;
        }

        autopilotView.setDisplayedGuideline(currentGuideline.toString());
    }

    @Override
    public void pause() {
        isPaused = true;
        navigator.pause();
    }

    @Override
    public void stop() {
        isStopped = true;
        navigator.stop();
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

    private void loadRosonFile(String fullFilePath) {
        try {
            RosonParser parser = new RosonParser();

            RosonBuildingDto buildingDto = parser.parseFullPathFile(fullFilePath);
            navigator = new TopologicalNavigator(buildingDto);
            navigator.setOnGuidelineChangeListner(this);

            autopilotView.setShowGraphButtonEnabled(true);

            autopilotView.setDisplayedRosonPath(fullFilePath);
        } catch (InvalidRosonNodeIdException | InvalidRosonNodeKindException | IOException e) {
            autopilotView.setShowGraphButtonEnabled(false);
            e.printStackTrace();
        } catch (InvalidArUcoIdException e) {
            e.printStackTrace();
        }
    }

    private void loadDefaultRosonFile(String rosonFileName) {
        try {
            RosonParser parser = new RosonParser();

            RosonBuildingDto buildingDto = parser.parse(rosonFileName);
            navigator = new TopologicalNavigator(buildingDto);
            navigator.setOnGuidelineChangeListner(this);

            autopilotView.setShowGraphButtonEnabled(true);
            autopilotView.setDisplayedRosonPath(rosonFileName);
        } catch (InvalidRosonNodeIdException | InvalidRosonNodeKindException | IOException e) {
            autopilotView.setShowGraphButtonEnabled(false);
            e.printStackTrace();
        } catch (InvalidArUcoIdException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInit() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onGuidelineChange(Guideline guideline) {
        currentGuideline = guideline;
        autopilotView.setDisplayedGuideline(guideline.toString());
        rosService.startStrategy(currentGuideline.getGuidelineType(), currentGuideline.getParameters());
    }

    @Override
    public void onNoGuidelineAvailable() {
        isStopped = true;
        autopilotView.setDisplayedGuideline("N/A");
        rosService.stopStrategy("");
    }
}
