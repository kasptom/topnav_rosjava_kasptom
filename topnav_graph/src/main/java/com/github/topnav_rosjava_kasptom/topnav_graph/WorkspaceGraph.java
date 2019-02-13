package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.BaseIdentifiableDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.MarkerNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.NodeNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.ResourceUtils;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.StyleConverter;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;
import java.util.List;

public class WorkspaceGraph {

    private Graph graph;

    private int nextEdgeId = 1;

    private static final String RENDERER_KEY = "org.graphstream.ui.renderer";
    private static final String RENDERER_NAME = "org.graphstream.ui.j2dviewer.J2DGraphRenderer";
    private static final String CUSTOM_NODE_STYLE = "css/stylesheet.css";


    public WorkspaceGraph(RosonBuildingDto buildingDto) throws IOException {
        System.setProperty(RENDERER_KEY, RENDERER_NAME);
        graph = new SingleGraph("Building graph (roson)");
        graph.addAttribute("ui.stylesheet", StyleConverter.convert(ResourceUtils.getFullPath(CUSTOM_NODE_STYLE)));
        buildGraph(buildingDto);
    }

    public void showGraph() {
        this.graph.display();
    }

    private void buildGraph(RosonBuildingDto buildingDto) {
        buildingDto.getNodes().forEach(this::addGraphNode);
        buildingDto.getMarkers().forEach(this::addGraphNode);

        addNodeNodeEdges(buildingDto);
        addMarkerNodeEdges(buildingDto);
    }

    private void addGraphNode(BaseIdentifiableDto rosonNode) {
        Node node = graph.addNode(rosonNode.getId());
        node.addAttribute("ui.label", rosonNode.getId());
        node.addAttribute("ui.class", rosonNode.getType(), rosonNode.getKind());

        node.addAttribute(RosonConstants.METADATA_TYPE, rosonNode.getType());
    }

    private void addNodeNodeEdges(RosonBuildingDto buildingDto) {
        List<NodeNodeRosonDto> nodeNodes = buildingDto.getNodeNodes();
        nodeNodes.forEach(nodeNode -> graph.addEdge(getNextEdgeId(), nodeNode.getNodeFromId(), nodeNode.getNodeToId(), true));
    }

    private void addMarkerNodeEdges(RosonBuildingDto buildingDto) {
        List<MarkerNodeRosonDto> markerNodes = buildingDto.getMarkerNodes();
        markerNodes.forEach(markerNode -> graph.addEdge(getNextEdgeId(), markerNode.getMarkerId(), markerNode.getNodeId(), true));
    }

    private String getNextEdgeId() {
        return String.format("edge%d", ++nextEdgeId);
    }
}
