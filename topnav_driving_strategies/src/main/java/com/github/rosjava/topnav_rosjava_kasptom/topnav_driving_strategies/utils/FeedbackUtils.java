package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.utils;

import com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.models.MarkerDetection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Topology;
import org.ros.message.Time;
import org.ros.node.NodeConfiguration;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.MarkersMsg;
import topnav_msgs.TopologyMsg;

import java.util.List;
import java.util.stream.Collectors;

public class FeedbackUtils {

    public static void fillInFeedbackMsg(FeedbackMsg feedbackMsg, MarkersMsg markersMsg, long timestamp) {
        List<MarkerDetection> detections = markersMsg.getMarkers()
                .stream()
                .map(markerMsg -> MarkerDetection.createDetection(Integer.toString(markerMsg.getId()), markerMsg.getCameraPosition()))
                .collect(Collectors.toList());

        List<Topology> topologies = detections.stream()
                .map(detection -> convertToTopology(detection, timestamp))
                .collect(Collectors.toList());

        List<TopologyMsg> topologyMsgs = convertToTopologyMessages(topologies);
        feedbackMsg.setTimestamp(Time.fromNano(timestamp));
        feedbackMsg.setTopologies(topologyMsgs);
    }

    private static List<TopologyMsg> convertToTopologyMessages(List<Topology> topologies) {
        NodeConfiguration configuration = NodeConfiguration.newPrivate();
        return topologies.stream()
                .map(topology -> {
                    TopologyMsg topologyMsg = configuration.getTopicMessageFactory().newFromType(TopologyMsg._TYPE);
                    topologyMsg.setIdentity(topology.getIdentity());
                    topologyMsg.setRelativeAlignment(topology.getRelativeAlignment());
                    topologyMsg.setRelativeDirection(topology.getRelativeDirection());
                    topologyMsg.setRelativeDistance(topology.getRelativeDistance());
                    return topologyMsg;
                }).collect(Collectors.toList());
    }

    private static Topology convertToTopology(MarkerDetection detection, long timestamp) {
        return new Topology(timestamp,
                detection.getId(),
                detection.getRelativeAlignment().name(),
                RelativeDirection.UNDEFINED.name(), // FIXME retrieve it from the MarkerMsg and current head's rotation
                detection.getRelativeDistance().name());
    }
}
