package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.BaseIdentifiableDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.NodeDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.marker.MarkerDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.MarkerNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.NodeNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.SpaceNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.SpaceWallRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.ResourceUtils;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.StyleConverter;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.*;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants.ROSON_METADATA_TYPE;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.TOPNAV_ID;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.TOPNAV_ROLE;

public class WorkspaceGraph {
    private Graph graph;

    private int nextEdgeId = 1;

    private static final String RENDERER_KEY = "org.graphstream.ui.renderer";
    private static final String RENDERER_NAME = "org.graphstream.ui.j2dviewer.J2DGraphRenderer";
    private static final String CUSTOM_NODE_STYLE = "css/stylesheet.css";


    public WorkspaceGraph(RosonBuildingDto buildingDto) throws IOException {
        System.setProperty(RENDERER_KEY, RENDERER_NAME);
        graph = new SingleGraph("Building graph (roson)");
        graph.addAttribute(GS_UI_STYLESHEET, StyleConverter.convert(ResourceUtils.getFullPath(CUSTOM_NODE_STYLE)));
        buildGraph(buildingDto);
    }

    public void showGraph() {
        this.graph.display();
    }

    private void buildGraph(RosonBuildingDto buildingDto) {
        buildingDto.getNodes().forEach(this::addGraphNode);
        buildingDto.getMarkers().forEach(this::addMarkers);
        buildingDto.getWalls().forEach(this::addWall);
        buildingDto.getSpaces().forEach(this::addSpace);

        addNodeNodeEdges(buildingDto);
        addMarkerNodeEdges(buildingDto);
        addWallWallEdges(buildingDto);
        addNodeSpaceEdges(buildingDto);
        addSpaceWallEdges(buildingDto);
    }

    private void addSpace(NodeDto nodeDto) {
        Node node = graph.addNode(nodeDto.getId());
        node.addAttribute(GS_UI_LABEL, nodeDto.getId());
        node.addAttribute(GS_UI_CLASS, nodeDto.getType());
    }

    private void addWall(BaseIdentifiableDto baseIdentifiableDto) {
        Node node = graph.addNode(baseIdentifiableDto.getId());
        node.addAttribute(GS_UI_LABEL, baseIdentifiableDto.getId());
        node.addAttribute(GS_UI_CLASS, baseIdentifiableDto.getType());
    }

    private void addMarkers(MarkerDto markerDto) {
        Node node = graph.addNode(markerDto.getId());
        node.addAttribute(GS_UI_LABEL, String.format("%s - %s", markerDto.getAruco().getId(), markerDto.getRole()));
        node.addAttribute(GS_UI_CLASS, markerDto.getType());

        node.addAttribute(TOPNAV_ID, markerDto.getAruco().getId());
        node.addAttribute(TOPNAV_ROLE, markerDto.getRole());
    }

    private void addGraphNode(NodeDto rosonNode) {
        Node node = graph.addNode(rosonNode.getId());
        node.addAttribute(GS_UI_LABEL, rosonNode.getId());
        node.addAttribute(GS_UI_CLASS, rosonNode.getType(), rosonNode.getKind());

        node.addAttribute(ROSON_METADATA_TYPE, rosonNode.getType());
    }

    private void addNodeNodeEdges(RosonBuildingDto buildingDto) {
        List<NodeNodeRosonDto> nodeNodes = buildingDto.getNodeNodes();
        nodeNodes.forEach(nodeNode -> graph.addEdge(getNextEdgeId(), nodeNode.getNodeFromId(), nodeNode.getNodeToId(), true));
    }

    private void addMarkerNodeEdges(RosonBuildingDto buildingDto) {
        List<MarkerNodeRosonDto> markerNodes = buildingDto.getMarkerNodes();
        markerNodes.forEach(markerNode -> graph.addEdge(getNextEdgeId(), markerNode.getMarkerId(), markerNode.getNodeId(), true));
    }

    private void addWallWallEdges(RosonBuildingDto buildingDto) {
        List<SpaceWallRosonDto> spaceWalls = buildingDto.getSpaceWalls();
        for (int i = 1; i < spaceWalls.size(); i++) {
            SpaceWallRosonDto prevWall = spaceWalls.get(i - 1);
            SpaceWallRosonDto currWall = spaceWalls.get(i);

            if (prevWall.getSpaceId().equals(currWall.getSpaceId())) {
                try {
                    graph.addEdge(getNextEdgeId(), prevWall.getWallId(), currWall.getWallId());
                } catch (EdgeRejectedException ere) {
                    System.out.printf("Edge %s %s already exists\n", prevWall.getWallId(), currWall.getWallId());
                }
            }
        }
    }

    private void addNodeSpaceEdges(RosonBuildingDto buildingDto) {
        List<SpaceNodeRosonDto> spaceNodes = buildingDto.getSpaceNodes();
        spaceNodes.forEach(spaceNode -> graph.addEdge(getNextEdgeId(), spaceNode.getNodeId(), spaceNode.getSpaceId()));
    }

    private void addSpaceWallEdges(RosonBuildingDto buildingDto) {
        List<SpaceWallRosonDto> spaceWalls = buildingDto.getSpaceWalls();
        spaceWalls.forEach(spaceWall -> graph.addEdge(getNextEdgeId(), spaceWall.getWallId(), spaceWall.getSpaceId()));
    }

    private String getNextEdgeId() {
        return String.format("edge%d", ++nextEdgeId);
    }
}
