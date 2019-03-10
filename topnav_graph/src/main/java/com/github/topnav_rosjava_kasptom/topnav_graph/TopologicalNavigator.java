package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidArUcoIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeKindException;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.NodeDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.marker.MarkerDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.ResourceUtils;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.StyleConverter;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.TopologicalNavigatorUtils;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;
import org.graphstream.algorithm.networksimplex.DynamicOneToAllShortestPath;
import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;
import java.util.ArrayList;
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

    TopologicalNavigator(RosonBuildingDto buildingDto) throws IOException, InvalidRosonNodeKindException, InvalidRosonNodeIdException {
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

    List<Guideline> createGuidelines(String startArUcoId, String endArUcoId) throws InvalidArUcoIdException {
        algorithm.setSource(getMarkerNodeByArUcoId(startArUcoId).getId());
        algorithm.compute();

        Path path = algorithm.getPath(getMarkerNodeByArUcoId(endArUcoId));

        LinkedList<Guideline> guidelines = new LinkedList<>();
        List<Node> pathNodes = new ArrayList<>(path.getNodeSet());
        System.out.println(pathNodes.stream()
                .map(Element::getId)
                .reduce((pathStr, nodeId) -> String.format("%s -> %s", pathStr, nodeId))
                .orElse("no path available"));

        for (int i = 1; i < pathNodes.size(); i++) {
            Node prevNode = pathNodes.get(i - 1);
            Node node = pathNodes.get(i);

            if (isGateEdgeWithMarkers(prevNode, node)) {
                Guideline guideline = TopologicalNavigatorUtils.convertToPassThroughDoorGuideline(prevNode, node);
                guidelines.add(guideline);
            } else if (isWallEndingEdge(node)) {
                Guideline guideline = TopologicalNavigatorUtils.createFollowWallGuideline();
                guidelines.add(guideline);
            } else if (isMarkerEndingEdge(node)) {
                Guideline guideline = TopologicalNavigatorUtils.createLookForMarkerGuideline(node);
                guidelines.add(guideline);
            }
        }
        return guidelines;
    }

    private boolean isGateEdgeWithMarkers(Node prevNode, Node node) {
        return prevNode.getAttribute(RosonConstants.ROSON_NODE_KIND).equals(RosonConstants.NodeKind.GATE_NODE)
                && node.getAttribute(RosonConstants.ROSON_NODE_KIND).equals(RosonConstants.NodeKind.GATE_NODE)
                && prevNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS)
                && node.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
    }

    private boolean isWallEndingEdge(Node node) {
        return node.getAttribute(TOPNAV_ATTRIBUTE_KEY_NODE_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_NODE_TYPE_WALL);
    }

    private boolean isMarkerEndingEdge(Node node) {
        return node.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
    }

    private Node getMarkerNodeByArUcoId(String arUcoId) throws InvalidArUcoIdException {
        MarkerDto markerDto = buildingDto.getMarkers()
                .stream()
                .filter(marker -> marker.getAruco().getId().equals(arUcoId))
                .findAny().orElse(null);

        if (markerDto == null)
            throw new InvalidArUcoIdException("ArUco with id %s does not exist in the building", arUcoId);

        NodeDto markerNode = buildingDto.getNodes()
                .stream()
                .filter(node -> node.getId().equals(markerDto.getAttachedToNodeId()))
                .findFirst()
                .orElseThrow(() -> new InvalidArUcoIdException("ArUco with id %s does not exist in the building", arUcoId));

        return graph.getNode(markerNode.getId());
    }
}
