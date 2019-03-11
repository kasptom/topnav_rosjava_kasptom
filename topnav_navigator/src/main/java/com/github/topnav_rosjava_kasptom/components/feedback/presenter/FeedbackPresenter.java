package com.github.topnav_rosjava_kasptom.components.feedback.presenter;

import com.github.topnav_rosjava_kasptom.components.feedback.view.IFeedbackView;
import com.github.topnav_rosjava_kasptom.services.IRosTopnavService;
import com.github.topnav_rosjava_kasptom.services.RosTopNavService;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Feedback;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Topology;
import javafx.application.Platform;

import java.util.List;
import java.util.stream.Collectors;

public class FeedbackPresenter implements IFeedbackPresenter {

    private final IFeedbackView view;
    private final IRosTopnavService service;
    private long timestamp;

    public FeedbackPresenter(IFeedbackView view) {
        this.view = view;
        this.service = RosTopNavService.getInstance();
    }

    @Override
    public void onFeedbackReceived(Feedback feedback) {
        if (!isTimeElapsed()) {
            return;
        }

        List<Topology> topologies = feedback.getTopologies();
        List<String> detectionStrings = topologies.stream()
                .map(topology -> String.format("%s, %s, %s, %s",
                        topology.getIdentity(),
                        topology.getRelativeAlignment(),
                        topology.getRelativeDirection(),
                        topology.getRelativeDistance()))
                .collect(Collectors.toList());

        detectionStrings.add(0, String.format("%s, %s, %s, %s", "ID", "alignment", "direction", "distance"));

        Platform.runLater(() -> this.view.onUpdateDetections(detectionStrings));
    }

    private boolean isTimeElapsed() {
        if (System.nanoTime() - timestamp > 1e9) {
            timestamp = System.nanoTime();
            return true;
        }

        return false;
    }

    @Override
    public void onInit() {
        this.service.addOnFeedbackChangeListener(this::onFeedbackReceived);
    }

    @Override
    public void onDestroy() {
        this.service.onDestroy();
    }
}
