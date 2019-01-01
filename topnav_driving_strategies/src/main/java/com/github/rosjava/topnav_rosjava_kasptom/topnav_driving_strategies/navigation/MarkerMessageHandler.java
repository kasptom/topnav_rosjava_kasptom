package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.navigation;

import org.ros.message.MessageListener;
import topnav_msgs.MarkerMsg;
import topnav_msgs.MarkersMsg;

public class MarkerMessageHandler implements MessageListener<MarkersMsg> {

    @Override
    public void onNewMessage(MarkersMsg markersMsg) {
        markersMsg.getMarkers().forEach(this::printMarkerMessage);
    }

    private void printMarkerMessage(MarkerMsg markerMsg) {
        System.out.println("--------------------");
        double[] rVec = markerMsg.getRotation();
        double[] tVec = markerMsg.getTranslation();
        System.out.printf("Marker ID: %d\n Rotation: (%.2f, %.2f, %.2f)\n Translation: (%.2f, %.2f, %.2f)\n",
                markerMsg.getId(), rVec[0], rVec[1], rVec[2], tVec[0], tVec[1], tVec[2]);
    }

    public void addOnSceneChangeListener(ISceneChangeListener sceneChangeListener) {
    }
}
