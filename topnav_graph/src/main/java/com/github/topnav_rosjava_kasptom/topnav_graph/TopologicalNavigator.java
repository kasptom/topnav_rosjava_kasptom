package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidArUcoIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeKindException;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.ResourceUtils;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.StyleConverter;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.TopologicalNavigatorUtils;
import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.listeners.OnGuidelineChangeListener;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Feedback;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Guideline;
import org.graphstream.algorithm.networksimplex.DynamicOneToAllShortestPath;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_STYLESHEET;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.*;

public class TopologicalNavigator implements ITopnavNavigator {
    private final DynamicOneToAllShortestPath algorithm;
    private final IFeedbackResolver feedbackResolver;
    private Graph graph;

    private OnGuidelineChangeListener guidelineChangeListener;
    private List<Guideline> guidelines;
    private int currentGuidelineIdx;
    private boolean isPaused;

    private static final String RENDERER_KEY = "org.graphstream.ui.renderer";
    private static final String RENDERER_NAME = "org.graphstream.ui.j2dviewer.J2DGraphRenderer";
    private static final String CUSTOM_NODE_STYLE = "css/stylesheet.css";

    public TopologicalNavigator(RosonBuildingDto buildingDto) throws IOException, InvalidRosonNodeKindException, InvalidRosonNodeIdException, InvalidArUcoIdException {
        System.setProperty(RENDERER_KEY, RENDERER_NAME);
        graph = new SingleGraph("Building graph (roson)");
        graph.addAttribute(GS_UI_STYLESHEET, StyleConverter.convert(ResourceUtils.getFullPath(CUSTOM_NODE_STYLE)));
        GraphBuilder.buildGraph(buildingDto, graph);

        feedbackResolver = new FeedbackResolver();
        guidelines = new ArrayList<>();
        currentGuidelineIdx = 0;
        this.algorithm = new DynamicOneToAllShortestPath(TOPNAV_ATTRIBUTE_KEY_COST);
        algorithm.init(graph);
    }

    @Override
    public void showGraph() {
        graph.display().setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
    }

    @Override
    public List<Guideline> createGuidelines(String startArUcoId, String endArUcoId) {
        algorithm.setSource(getMarkerNodeByArUcoId(startArUcoId).getId());
        algorithm.compute();

        Path path = algorithm.getPath(getMarkerNodeByArUcoId(endArUcoId));

        LinkedList<Guideline> guidelines = new LinkedList<>();
        List<Edge> pathEdges = new ArrayList<>(path.getEdgeSet());
        System.out.println(pathEdges.stream()
                .map(Element::getId)
                .reduce((pathStr, edgeId) -> String.format("%s, %s", pathStr, edgeId))
                .orElse("no path available"));

        for (int i = 0; i < pathEdges.size(); ) {
            Edge edge = pathEdges.get(i);
            Edge nextEdge = (i + 1 == pathEdges.size()) ? null : pathEdges.get(i + 1);

            if (isGateEdgeWithMarkers(edge, nextEdge)) {
                Guideline guideline = TopologicalNavigatorUtils.convertToPassThroughDoorGuideline(edge, nextEdge);
                guidelines.add(guideline);
                i += 2;
            } else if (isWallEndingEdge(edge)) {
                Guideline guideline = TopologicalNavigatorUtils.createFollowWallGuideline(edge);
                guidelines.add(guideline);
                i++;
            } else if (isMarkerEndingEdge(edge)) {
                Guideline guideline = TopologicalNavigatorUtils.createLookForMarkerGuideline(edge.getTargetNode());
                guidelines.add(guideline);
                i++;
            } else {
                i++;
            }
        }

        mergeSubsequentFollowWallGuidelines(guidelines);

        this.guidelines.clear();
        this.guidelines.addAll(guidelines);
        return guidelines;
    }

    @Override
    public void start() {
        isPaused = false;
        if (guidelines.isEmpty()) throw new RuntimeException("Guidelines list is empty");

        Guideline currentGuideline = guidelines.get(currentGuidelineIdx);
        guidelineChangeListener.onGuidelineChange(currentGuideline);
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void stop() {
        guidelines.clear();
        currentGuidelineIdx = 0;
        guidelineChangeListener.onNoGuidelineAvailable();
    }

    @Override
    public void setOnGuidelineChangeListner(OnGuidelineChangeListener listener) {
        guidelineChangeListener = listener;
    }

    @Override
    public List<Guideline> getGuidelines() {
        return guidelines;
    }

    private void mergeSubsequentFollowWallGuidelines(LinkedList<Guideline> guidelines) {
        for (int i = 1; i < guidelines.size(); i++) {
            if (guidelines.get(i - 1).getGuidelineType().equals(DrivingStrategy.DRIVING_STRATEGY_ALONG_WALL_2)
                    && guidelines.get(i).getGuidelineType().equals(DrivingStrategy.DRIVING_STRATEGY_ALONG_WALL_2)) {
                //noinspection SuspiciousListRemoveInLoop
                guidelines.remove(i);
            }
        }
    }

    private boolean isGateEdgeWithMarkers(Edge edge, Edge nextEdge) {
        if (nextEdge == null) return false;

        Node frontDoorNode = edge.getSourceNode();
        Node gateNode = edge.getTargetNode();
        Node backDoorNode = nextEdge.getTargetNode();

        if (!frontDoorNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE) || !gateNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE)) {
            return false;
        }

        return frontDoorNode.getAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_TOPOLOGY_TYPE_GATE_TOPOLOGY)
                && gateNode.getAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_TOPOLOGY_TYPE_GATE)
                && backDoorNode.getAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_TOPOLOGY_TYPE_GATE_TOPOLOGY)
                && frontDoorNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS)
                && backDoorNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
    }

    private boolean isWallEndingEdge(Edge edge) {
        Node node = edge.getTargetNode();
        return node.getAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE).equals(TOPNAV_ATTRIBUTE_VALUE_TOPOLOGY_TYPE_WALL);
    }

    private boolean isMarkerEndingEdge(Edge edge) {
        Node node = edge.getTargetNode();
        return node.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
    }

    private Node getMarkerNodeByArUcoId(String arUcoId) {
        Node markerNode = graph.getNode(arUcoId);
        return graph.getNode(markerNode.getNeighborNodeIterator().next().getId());
    }

    @Override
    public void onFeedbackChange(Feedback feedback) {
        if (feedbackResolver.shouldSwitchToNextGuideline(feedback, currentGuidelineIdx, guidelines)) {
            currentGuidelineIdx++;
            guidelineChangeListener.onGuidelineChange(guidelines.get(currentGuidelineIdx));
        } else if (feedbackResolver.shouldStop(feedback, currentGuidelineIdx, guidelines)) {
            guidelineChangeListener.onNoGuidelineAvailable();
        }
    }
}
