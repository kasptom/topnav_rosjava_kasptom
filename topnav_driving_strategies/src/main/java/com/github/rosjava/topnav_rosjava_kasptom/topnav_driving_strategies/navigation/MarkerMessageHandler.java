package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.navigation;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.MarkerDetection;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.RelativeAlignment;
import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.RelativeDistance;
import org.ros.message.MessageListener;
import topnav_msgs.MarkerMsg;
import topnav_msgs.MarkersMsg;

import java.util.HashMap;

public class MarkerMessageHandler implements MessageListener<MarkersMsg> {

    private HashMap<String, RelativeAlignment> previousAlignments = new HashMap<>();
    private HashMap<String, RelativeDistance> previousDistances = new HashMap<>();

    @Override
    public void onNewMessage(MarkersMsg markersMsg) {
        markersMsg.getMarkers().forEach(this::printMarkerMessage);
    }

    private void printMarkerMessage(MarkerMsg markerMsg) {
        double[] pos = markerMsg.getCameraPosition();
        MarkerDetection detection = MarkerDetection.createDetection(Integer.toString(markerMsg.getId()), markerMsg.getCameraPosition());

        RelativeAlignment currentAlignment = detection.getRelativeAlignment();
        RelativeDistance currentDistance = detection.getRelativeDistance();

        if (currentAlignment != previousAlignments.get(detection.getId())
                || currentDistance != previousDistances.get(detection.getId())) {
            previousAlignments.put(detection.getId(), currentAlignment);
            previousDistances.put(detection.getId(), currentDistance);

            System.out.printf("Marker ID: %s\n Position: (%.2f, %.2f, %.2f)\n %s %s\n",
                    detection.getId(), pos[0], pos[1], pos[2], detection.getRelativeAlignment(), detection.getRelativeDistance());
        }
    }

    public void addOnSceneChangeListener(ISceneChangeListener sceneChangeListener) {
    }
}
