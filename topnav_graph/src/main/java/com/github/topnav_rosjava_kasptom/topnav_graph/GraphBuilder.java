package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.BaseIdentifiableDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.NodeDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.marker.MarkerDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.MarkerNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.NodeNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.SpaceNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.SpaceWallRosonDto;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_CLASS;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_LABEL;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants.ROSON_METADATA_TYPE;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.*;

class GraphBuilder {
    private static int nextEdgeId = 1;

    static synchronized void buildGraph(RosonBuildingDto buildingDto, Graph graph) {
        nextEdgeId = 1;

        buildingDto.getNodes().forEach(node -> addRosonNode(node, graph));
        buildingDto.getMarkers().forEach(markerDto -> addMarkers(markerDto, graph));
        buildingDto.getWalls().forEach(wall -> addWall(wall, graph));
        buildingDto.getSpaces().forEach(space -> addSpace(space, graph));

        addNodeNodeEdges(buildingDto, graph);
        addMarkerNodeEdges(buildingDto, graph);
        addWallWallEdges(buildingDto, graph);
        addNodeSpaceEdges(buildingDto, graph);
        addSpaceWallEdges(buildingDto, graph);
    }

    private static void addSpace(NodeDto nodeDto, Graph graph) {
        addIdentifiable(nodeDto.getId(), nodeDto.getType(), graph);
    }

    private static void addWall(BaseIdentifiableDto baseIdentifiableDto, Graph graph) {
        addIdentifiable(baseIdentifiableDto.getId(), baseIdentifiableDto.getType(), graph);
    }

    private static void addIdentifiable(String id, String type, Graph graph) {
        Node node = graph.addNode(id);
        node.addAttribute(GS_UI_LABEL, id);
        node.addAttribute(GS_UI_CLASS, type);

        node.addAttribute(TOPNAV_ATTRIBUTE_KEY_NODE_TYPE, type);
    }

    private static void addMarkers(MarkerDto markerDto, Graph graph) {
        Node node = graph.addNode(markerDto.getId());
        node.addAttribute(GS_UI_LABEL, String.format("%s - %s", markerDto.getAruco().getId(), markerDto.getRole()));
        node.addAttribute(GS_UI_CLASS, markerDto.getType());

        node.addAttribute(TOPNAV_ATTRIBUTE_KEY_ID, markerDto.getAruco().getId());
        node.addAttribute(TOPNAV_ATTRIBUTE_KEY_ROLE, markerDto.getRole());

        node.addAttribute(TOPNAV_ATTRIBUTE_KEY_NODE_TYPE, markerDto.getType());
    }

    private static void addRosonNode(NodeDto rosonNode, Graph graph) {
        Node node = graph.addNode(rosonNode.getId());
        node.addAttribute(GS_UI_LABEL, rosonNode.getId());
        node.addAttribute(GS_UI_CLASS, rosonNode.getType(), rosonNode.getKind());

        node.addAttribute(ROSON_METADATA_TYPE, rosonNode.getType());
        node.addAttribute(TOPNAV_ATTRIBUTE_KEY_NODE_TYPE, rosonNode.getKind());
    }

    private static void addNodeNodeEdges(RosonBuildingDto buildingDto, Graph graph) {
        List<NodeNodeRosonDto> nodeNodes = buildingDto.getNodeNodes();
        nodeNodes.forEach(nodeNode -> {
            Edge edge = graph.addEdge(getNextEdgeId(), nodeNode.getNodeFromId(), nodeNode.getNodeToId(), true);
            edge.addAttribute(TOPNAV_ATTRIBUTE_KEY_COST, 1.0);
        });
    }

    private static void addMarkerNodeEdges(RosonBuildingDto buildingDto, Graph graph) {
        List<MarkerNodeRosonDto> markerNodes = buildingDto.getMarkerNodes();
        markerNodes.forEach(markerNode -> graph.addEdge(getNextEdgeId(), markerNode.getMarkerId(), markerNode.getNodeId()));
    }

    private static void addWallWallEdges(RosonBuildingDto buildingDto, Graph graph) {
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

    private static void addNodeSpaceEdges(RosonBuildingDto buildingDto, Graph graph) {
        List<SpaceNodeRosonDto> spaceNodes = buildingDto.getSpaceNodes();
        spaceNodes.forEach(spaceNode -> graph.addEdge(getNextEdgeId(), spaceNode.getNodeId(), spaceNode.getSpaceId()));
    }

    private static void addSpaceWallEdges(RosonBuildingDto buildingDto, Graph graph) {
        List<SpaceWallRosonDto> spaceWalls = buildingDto.getSpaceWalls();
        spaceWalls.forEach(spaceWall -> graph.addEdge(getNextEdgeId(), spaceWall.getWallId(), spaceWall.getSpaceId()));
    }

    private static String getNextEdgeId() {
        return String.format("edge%d", ++nextEdgeId);
    }
}
