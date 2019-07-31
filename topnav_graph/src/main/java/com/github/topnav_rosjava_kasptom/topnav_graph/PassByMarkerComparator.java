package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.marker.MarkerDto;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.MarkerRoles;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.GuidelineParam;
import org.graphstream.graph.Edge;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class PassByMarkerComparator implements Comparator<Guideline> {
    private HashMap<String, Integer> markerTypeToIdx = new HashMap<>();
    private final List<MarkerDto> markers;

    /**
     * @param edge edge with markers
     */
    public PassByMarkerComparator(Edge edge) {
        String topnavEdgeDirection = edge.getAttribute(TopNavConstants.TOPNAV_ATTRIBUTE_KEY_EDGE_TYPE);

        markers = edge.getSourceNode().getAttribute(TopNavConstants.TOPNAV_ATTRIBUTE_KEY_MARKERS);

        if (TopNavConstants.TOPNAV_ATTRIBUTE_VALUE_EDGE_TYPE_LEFTWARD.equals(topnavEdgeDirection)) {
            markerTypeToIdx.put(MarkerRoles.MARKER_ROLE_LEFT, 1);
            markerTypeToIdx.put(MarkerRoles.MARKER_ROLE_RIGHT, 2);
        } else {
            markerTypeToIdx.put(MarkerRoles.MARKER_ROLE_RIGHT, 1);
            markerTypeToIdx.put(MarkerRoles.MARKER_ROLE_LEFT, 2);
        }
    }

    @Override
    public int compare(Guideline first, Guideline second) {
        String firstId = first.getParameters()
                .stream().filter(param -> param.getName().equals(DrivingStrategy.ApproachMarker.KEY_APPROACHED_MARKER_ID))
                .map(GuidelineParam::getValue)
                .findFirst().orElse(null);

        String secondId = second.getParameters()
                .stream().filter(param -> param.getName().equals(DrivingStrategy.ApproachMarker.KEY_APPROACHED_MARKER_ID))
                .map(GuidelineParam::getValue)
                .findFirst().orElse(null);


        String firstMarkerRole = markers.stream()
                .filter(marker -> marker.getAruco().getId().equals(firstId))
                .map(MarkerDto::getRole)
                .findFirst().orElse(null);

        String secondMarkerRole = markers.stream()
                .filter(marker -> marker.getAruco().getId().equals(secondId))
                .map(MarkerDto::getRole)
                .findFirst().orElse(null);

        return markerTypeToIdx
                .getOrDefault(firstMarkerRole, 0)
                .compareTo(markerTypeToIdx.getOrDefault(secondMarkerRole, 0));
    }
}
