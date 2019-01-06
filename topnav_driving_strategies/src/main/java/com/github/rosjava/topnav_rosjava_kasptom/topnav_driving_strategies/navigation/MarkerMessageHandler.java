package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.navigation;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.MarkerDetection;
import org.ros.message.MessageListener;
import topnav_msgs.MarkerMsg;
import topnav_msgs.MarkersMsg;

import java.util.HashMap;

public class MarkerMessageHandler implements MessageListener<MarkersMsg> {
    private HashMap<String, Long> previousTimeStamp = new HashMap<>();

    @Override
    public void onNewMessage(MarkersMsg markersMsg) {
        markersMsg.getMarkers().forEach(this::printMarkerMessage);
    }

    private void printMarkerMessage(MarkerMsg markerMsg) {
        double[] pos = markerMsg.getCameraPosition();
        MarkerDetection detection = MarkerDetection.createDetection(Integer.toString(markerMsg.getId()), markerMsg.getCameraPosition());
        Long currentTimeStamp = System.nanoTime();

        if (!previousTimeStamp.containsKey(detection.getId())
                || currentTimeStamp - previousTimeStamp.get(detection.getId()) > 1e9) {
            previousTimeStamp.put(detection.getId(), currentTimeStamp);
            System.out.printf("Marker ID: %s\n Position: (%.2f, %.2f, %.2f)\n %s %s\n",
                    detection.getId(), pos[0], pos[1], pos[2], detection.getRelativeAlignment(), detection.getRelativeDistance());
        }
    }

    public void addOnSceneChangeListener(ISceneChangeListener sceneChangeListener) {
    }
}
