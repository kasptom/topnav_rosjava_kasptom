package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.marker.MarkerDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.ResourceUtils;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.StyleConverter;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;
import org.graphstream.algorithm.networksimplex.DynamicOneToAllShortestPath;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_STYLESHEET;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.*;

class TopologicalNavigator {
    private final DynamicOneToAllShortestPath algorithm;
    private Graph graph;
    private RosonBuildingDto buildingDto;

    private static final String RENDERER_KEY = "org.graphstream.ui.renderer";
    private static final String RENDERER_NAME = "org.graphstream.ui.j2dviewer.J2DGraphRenderer";
    private static final String CUSTOM_NODE_STYLE = "css/stylesheet.css";

    TopologicalNavigator(RosonBuildingDto buildingDto) throws IOException {
        System.setProperty(RENDERER_KEY, RENDERER_NAME);
        graph = new SingleGraph("Building graph (roson)");
        graph.addAttribute(GS_UI_STYLESHEET, StyleConverter.convert(ResourceUtils.getFullPath(CUSTOM_NODE_STYLE)));
        GraphBuilder.buildGraph(buildingDto, graph);
        this.buildingDto = buildingDto;

        this.algorithm = new DynamicOneToAllShortestPath(TOPNAV_ATTRIBUTE_KEY_COST);
        algorithm.init(graph);
    }

    void showGraph() {
        graph.display();
    }

    List<Guideline> createGuidelines(String fromMarker, String toMarker) throws InvalidArUcoIdException {
        String source = getMarkerNodeByArUcoId(fromMarker);
        String destination = getMarkerNodeByArUcoId(toMarker);

        algorithm.setSource(source);
        algorithm.compute();
        Path path = algorithm.getPath(graph.getNode(destination));

        LinkedList<Guideline> guidelines = new LinkedList<>();
        path.getNodeSet()
                .forEach(node -> updateGuidelines(node.getId(), guidelines));
        return guidelines;
    }

    private void updateGuidelines(String nodeId, LinkedList<Guideline> guidelines) {

        Node node = graph.getNode(nodeId);
        String nodeType = node.getAttribute(TOPNAV_ATTRIBUTE_KEY_NODE_TYPE);

        System.out.printf("[%10s] %s\n", nodeId, nodeType);

        if (hasMarkerNodeNeighbours(node)) {
            //Guideline guideline =
        }
    }

    private boolean hasMarkerNodeNeighbours(Node node) {
        return node.getEdgeSet()
                .stream()
                .map(edge -> isMarkerNode(edge.getNode1()) || isMarkerNode(edge.getNode0()))
                .reduce((areMarkerNeighbours, isMarkerNeighbour) -> areMarkerNeighbours || isMarkerNeighbour)
                .orElse(false);
    }

    private String getMarkerNodeByArUcoId(String arUcoId) throws InvalidArUcoIdException {
        MarkerDto markerDto = buildingDto.getMarkers()
                .stream()
                .filter(marker -> marker.getAruco().getId().equals(arUcoId))
                .findAny().orElse(null);

        if (markerDto == null)
            throw new InvalidArUcoIdException("ArUco with id %s does not exist in the building", arUcoId);

        return markerDto.getId();
    }

    private boolean isMarkerNode(Node node) {
        return node.getAttribute(TOPNAV_ATTRIBUTE_KEY_NODE_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_NODE_TYPE_MARKER);
    }

    private boolean isSpaceNode(Node node) {
        return node.getAttribute(TOPNAV_ATTRIBUTE_KEY_NODE_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_NODE_TYPE_MARKER);
    }
}
