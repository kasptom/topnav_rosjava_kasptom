package com.github.topnav_rosjava_kasptom.topnav_graph.utils;

import com.github.topnav_rosjava_kasptom.topnav_graph.PassByMarkerComparator;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.marker.MarkerDto;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.*;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.MarkerRoles.MARKER_ROLE_LEFT;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.MarkerRoles.MARKER_ROLE_RIGHT;

public class TopologicalNavigatorUtils {
    public static Guideline convertToPassThroughDoorGuideline(Edge edge, Edge nextEdge, boolean isDeadReckoningEnabled, String fullRobotRotationMs) {
        Node frontDoorNode = edge.getSourceNode();
        Node backDoorNode = nextEdge.getTargetNode();

        List<MarkerDto> frontMarkers = frontDoorNode.getAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
        List<MarkerDto> backMarkers = backDoorNode.getAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);

        List<GuidelineParam> params = frontMarkers
                .stream()
                .map(marker -> new GuidelineParam(getDoorParameterName(marker.getRole(), true), marker.getAruco().getId(), "String"))
                .collect(Collectors.toList());

        params.addAll(backMarkers
                .stream()
                .map(marker -> new GuidelineParam(getDoorParameterName(marker.getRole(), false), marker.getAruco().getId(), "String"))
                .collect(Collectors.toList()));

        String guidelineType = DrivingStrategy.DRIVING_STRATEGY_PASS_THROUGH_DOOR_2;

        if (isDeadReckoningEnabled) {
            guidelineType = DrivingStrategy.DRIVING_STRATEGY_PASS_THROUGH_DOOR_3;
            params.add(new GuidelineParam(DrivingStrategy.DeadReckoning.KEY_MANEUVER_ROBOT_FULL_ROTATION_MS, fullRobotRotationMs, "Long"));
        }

        return new Guideline(guidelineType, params);
    }

    private static String getDoorParameterName(String markerRole, boolean isFrontMarker) {
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

    public static Guideline createFollowWallGuideline(Edge edge) {
        List<GuidelineParam> params = new ArrayList<>();
        params.add(createWallAlignmentParameter(edge));
        return new Guideline(DrivingStrategy.DRIVING_STRATEGY_ALONG_WALL_2, params);
    }

    private static GuidelineParam createWallAlignmentParameter(Edge edge) {
        return new GuidelineParam(DrivingStrategy.FollowWall.KEY_TRACKED_WALL_ALIGNMENT,
                edge.getAttribute(TOPNAV_ATTRIBUTE_KEY_EDGE_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_EDGE_TYPE_LEFTWARD)
                        ? DrivingStrategy.FollowWall.VALUE_TRACKED_WALL_RIGHT
                        : DrivingStrategy.FollowWall.VALUE_TRACKED_WALL_LEFT, "String");
    }

    public static Guideline createLookApproachMarkerGuideline(Node node, boolean isDeadReckoningEnabled, String fullRobotRotationMs) {
        List<MarkerDto> markers = node.getAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);

        int[] markerCounter = {0};
        List<GuidelineParam> params = markers
                .stream()
                .map(marker -> new GuidelineParam(
                        ++markerCounter[0] == 1 ? DrivingStrategy.ApproachMarker.KEY_APPROACHED_MARKER_ID : DrivingStrategy.ApproachMarker.KEY_APPROACHED_MARKER_ID_2,
                        marker.getAruco().getId(),
                        "String"))
                .collect(Collectors.toList());

        String guidelineType = DrivingStrategy.DRIVING_STRATEGY_APPROACH_MARKER;

        if (isDeadReckoningEnabled) {
            guidelineType = DrivingStrategy.DRIVING_STRATEGY_APPROACH_MARKER_2;
            params.add(new GuidelineParam(DrivingStrategy.DeadReckoning.KEY_MANEUVER_ROBOT_FULL_ROTATION_MS, fullRobotRotationMs, "Long"));
        }

        return new Guideline(guidelineType, params);
    }

    public static List<Guideline> createPassByMarkerGuidelines(Edge edge, String fullRobotRotationMs) {
        Node node = edge.getSourceNode();
        List<MarkerDto> markers = node.getAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);

        return markers.stream().map(marker -> {
            List<GuidelineParam> params = new ArrayList<>();
            params.add(new GuidelineParam(DrivingStrategy.ApproachMarker.KEY_APPROACHED_MARKER_ID, marker.getAruco().getId(), "String"));
            params.add(createRelativeDirectionParameter(edge));
            params.add(createRelativeAlignmentParameter(edge));
            params.add(new GuidelineParam(DrivingStrategy.DeadReckoning.KEY_MANEUVER_ROBOT_FULL_ROTATION_MS, fullRobotRotationMs, "Long"));

            return new Guideline(DrivingStrategy.DRIVING_STRATEGY_APPROACH_MARKER_2, params);
        }).sorted(new PassByMarkerComparator(edge)).collect(Collectors.toList());
    }

    private static GuidelineParam createRelativeDirectionParameter(Edge edge) {
        return new GuidelineParam(DrivingStrategy.ApproachMarker.KEY_APPROACHED_DIRECTION,
                edge.getAttribute(TOPNAV_ATTRIBUTE_KEY_EDGE_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_EDGE_TYPE_LEFTWARD)
                        ? DrivingStrategy.ApproachMarker.VALUE_APPROACHED_DIRECTION_AT_RIGHT
                        : DrivingStrategy.ApproachMarker.VALUE_APPROACHED_DIRECTION_AT_LEFT, "String");
    }

    private static GuidelineParam createRelativeAlignmentParameter(Edge edge) {
        return new GuidelineParam(DrivingStrategy.ApproachMarker.KEY_APPROACHED_ALIGNMENT,
                edge.getAttribute(TOPNAV_ATTRIBUTE_KEY_EDGE_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_EDGE_TYPE_LEFTWARD)
                        ? DrivingStrategy.ApproachMarker.VALUE_APPROACHED_ALIGNMENT_RIGHT
                        : DrivingStrategy.ApproachMarker.VALUE_APPROACHED_ALIGNMENT_LEFT, "String");
    }
}
