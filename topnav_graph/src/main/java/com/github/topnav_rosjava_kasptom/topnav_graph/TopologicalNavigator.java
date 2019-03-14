package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidArUcoIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeKindException;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
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
import org.graphstream.ui.view.Viewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_STYLESHEET;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.*;

public class TopologicalNavigator {
    private final DynamicOneToAllShortestPath algorithm;
    private Graph graph;

    private static final String RENDERER_KEY = "org.graphstream.ui.renderer";
    private static final String RENDERER_NAME = "org.graphstream.ui.j2dviewer.J2DGraphRenderer";
    private static final String CUSTOM_NODE_STYLE = "css/stylesheet.css";

    public TopologicalNavigator(RosonBuildingDto buildingDto) throws IOException, InvalidRosonNodeKindException, InvalidRosonNodeIdException, InvalidArUcoIdException {
        System.setProperty(RENDERER_KEY, RENDERER_NAME);
        graph = new SingleGraph("Building graph (roson)");
        graph.addAttribute(GS_UI_STYLESHEET, StyleConverter.convert(ResourceUtils.getFullPath(CUSTOM_NODE_STYLE)));
        GraphBuilder.buildGraph(buildingDto, graph);

        this.algorithm = new DynamicOneToAllShortestPath(TOPNAV_ATTRIBUTE_KEY_COST);
        algorithm.init(graph);
    }

    public void showGraph() {
        graph.display().setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
    }

    public List<Guideline> createGuidelines(String startArUcoId, String endArUcoId) throws InvalidArUcoIdException {
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
            Node nextNode = pathNodes.size() == i + 1 ? null : pathNodes.get(i + 1);

            if (isGateEdgeWithMarkers(prevNode, nextNode)) {
                Guideline guideline = TopologicalNavigatorUtils.convertToPassThroughDoorGuideline(prevNode, nextNode);
                guidelines.add(guideline);
            } else if (isWallEndingEdge(node, nextNode)) {
                Guideline guideline = TopologicalNavigatorUtils.createFollowWallGuideline();
                guidelines.add(guideline);
            } else if (isMarkerEndingEdge(node)) {
                Guideline guideline = TopologicalNavigatorUtils.createLookForMarkerGuideline(node);
                guidelines.add(guideline);
            }
        }
        return guidelines;
    }

    private boolean isGateEdgeWithMarkers(Node prevNode, Node nextNode) {
        if (nextNode == null) return false;

        if (!prevNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE) || !nextNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE)) {
            return false;
        }

        return prevNode.getAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_TOPOLOGY_TYPE_GATE)
                && nextNode.getAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_TOPOLOGY_TYPE_GATE)
                && prevNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS)
                && nextNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
    }

    private boolean isWallEndingEdge(Node node, Node nextNode) {
        return nextNode == null && node.getAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_TOPOLOGY_TYPE_WALL);
    }

    private boolean isMarkerEndingEdge(Node node) {
        return node.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
    }

    private Node getMarkerNodeByArUcoId(String arUcoId) {
        Node markerNode = graph.getNode(arUcoId);
        return graph.getNode(markerNode.getNeighborNodeIterator().next().getId());
    }
}
