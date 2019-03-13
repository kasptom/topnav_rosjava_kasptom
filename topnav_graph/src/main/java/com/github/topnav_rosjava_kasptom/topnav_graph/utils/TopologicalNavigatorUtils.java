package com.github.topnav_rosjava_kasptom.topnav_graph.utils;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.marker.MarkerDto;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import org.graphstream.graph.Node;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.TOPNAV_ATTRIBUTE_KEY_MARKERS;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.MarkerRoles.MARKER_ROLE_LEFT;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.MarkerRoles.MARKER_ROLE_RIGHT;

public class TopologicalNavigatorUtils {
    public static Guideline convertToPassThroughDoorGuideline(Node prevNode, Node node) {
        List<MarkerDto> frontMarkers = prevNode.getAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
        List<MarkerDto> backMarkers = node.getAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);

        List<GuidelineParam> params = frontMarkers
                .stream()
                .map(marker -> new GuidelineParam(getParameterName(marker.getRole(), true), marker.getAruco().getId(), "String"))
                .collect(Collectors.toList());

        params.addAll(backMarkers
                .stream()
                .map(marker -> new GuidelineParam(getParameterName(marker.getRole(), false), marker.getAruco().getId(), "String"))
                .collect(Collectors.toList()));

        return new Guideline(DrivingStrategy.DRIVING_STRATEGY_PASS_THROUGH_DOOR_2, params);
    }

    private static String getParameterName(String markerRole, boolean isFrontMarker) {
        if (markerRole.equals(MARKER_ROLE_LEFT) && isFrontMarker) {
            return DrivingStrategy.ThroughDoor.KEY_FRONT_LEFT_MARKER_ID;
        } else if (markerRole.equals(MARKER_ROLE_RIGHT) && isFrontMarker) {
            return DrivingStrategy.ThroughDoor.KEY_FRONT_RIGHT_MARKER_ID;
        } else if (markerRole.equals(MARKER_ROLE_LEFT)) {
            return DrivingStrategy.ThroughDoor.KEY_BACK_LEFT_MARKER_ID;
        } else {
            return DrivingStrategy.ThroughDoor.KEY_BACK_RIGHT_MARKER_ID;
        }
    }

    public static Guideline createFollowWallGuideline() {
        return new Guideline(DrivingStrategy.DRIVING_STRATEGY_ALONG_WALL_2, null);
    }

    public static Guideline createLookForMarkerGuideline(Node node) {
        List<MarkerDto> markers = node.getAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
        List<GuidelineParam> params = markers
                .stream()
                .map(marker -> new GuidelineParam(getParameterName(marker.getRole(), false), marker.getAruco().getId(), "String"))
                .collect(Collectors.toList());
        return new Guideline(DrivingStrategy.DRIVING_STRATEGY_TOWARDS_MARKER, params);
    }
}
