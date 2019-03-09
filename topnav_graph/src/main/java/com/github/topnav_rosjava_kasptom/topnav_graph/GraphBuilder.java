package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.BaseIdentifiableDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.NodeDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.marker.MarkerDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.MarkerNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.NodeNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.SpaceNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.SpaceWallRosonDto;
import org.graphstream.graph.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_CLASS;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_LABEL;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants.ROSON_METADATA_TYPE;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.*;

class GraphBuilder {
    static synchronized void buildGraph(RosonBuildingDto buildingDto, Graph graph) {
        buildingDto.getNodes().forEach(node -> addRosonNode(node, graph));
        buildingDto.getMarkers().forEach(markerDto -> addMarkers(markerDto, graph));
        buildingDto.getWalls().forEach(wall -> addWall(wall, graph));
        buildingDto.getSpaces().forEach(space -> addSpace(space, graph));

        addNodeNodeEdges(buildingDto, graph);

        replaceMarkerNodesWithMarkers(buildingDto, graph);
        addMarkersMetadataToParentNodes(buildingDto, graph);

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
            String fromId = nodeNode.getNodeFromId();
            String toId = nodeNode.getNodeToId();
            Edge edge = graph.addEdge(directedEdgeName(fromId, toId), fromId, toId, true);
            edge.addAttribute(TOPNAV_ATTRIBUTE_KEY_COST, 1.0);
        });
    }

    private static void replaceMarkerNodesWithMarkers(RosonBuildingDto buildingDto, Graph graph) {
        List<MarkerNodeRosonDto> markerToNodes = buildingDto.getMarkerNodes();

        markerToNodes.forEach(markerToNode -> {
            Node markerNode = graph.getNode(markerToNode.getNodeId());
            Node marker = graph.getNode(markerToNode.getMarkerId());

            Node nodeToConnectDirectly = markerNode.getNeighborNodeIterator().next();

            graph.addEdge(directedEdgeName(marker.getId(), nodeToConnectDirectly.getId()), marker.getId(), nodeToConnectDirectly.getId(), true);
            graph.removeNode(markerNode);
        });
    }

    private static void addMarkersMetadataToParentNodes(RosonBuildingDto buildingDto, Graph graph) {
        List<MarkerDto> markersToAdd = buildingDto.getMarkers();
        markersToAdd.forEach(marker -> {
            Node markerNode = graph.getNode(marker.getId());
            Node parentNode = markerNode.getNeighborNodeIterator().next();
            if (!parentNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS)) {
                parentNode.addAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS, new ArrayList<MarkerDto>());
            }

            List<MarkerDto> markers = parentNode.getAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
            markers.add(marker);
        });
    }

    private static String directedEdgeName(String fromNodeId, String toNodeId) {
        return String.format("%s -> %s", fromNodeId, toNodeId);
    }

    private static String undirectedEdgeName(String nodeId, String otherNodeId) {
        return nodeId.compareTo(otherNodeId) < 0
                ? String.format("%s -- %s", nodeId, otherNodeId)
                : String.format("%s -- %s", otherNodeId, nodeId);
    }

    private static Edge getDirectedEdge(Graph graph, String fromNodeId, String toNodeId) {
        return graph.getEdge(directedEdgeName(fromNodeId, toNodeId));
    }

    private static Edge getUndirectedEdge(Graph graph, String nodeId, String otherNodeId) {
        return graph.getEdge(undirectedEdgeName(nodeId, otherNodeId));
    }

    private static void addWallWallEdges(RosonBuildingDto buildingDto, Graph graph) {
        List<SpaceWallRosonDto> spaceWalls = buildingDto.getSpaceWalls();
        for (int i = 1; i < spaceWalls.size(); i++) {
            SpaceWallRosonDto prevWall = spaceWalls.get(i - 1);
            SpaceWallRosonDto currWall = spaceWalls.get(i);

            if (prevWall.getSpaceId().equals(currWall.getSpaceId())) {
                try {
                    String wallId = prevWall.getWallId();
                    String otherWallId = currWall.getWallId();
                    graph.addEdge(undirectedEdgeName(wallId, otherWallId), wallId, otherWallId);
                } catch (EdgeRejectedException | IdAlreadyInUseException exc) {
                    System.out.printf("Edge %s %s already exists\n", prevWall.getWallId(), currWall.getWallId());
                }
            }
        }
    }

    private static void addNodeSpaceEdges(RosonBuildingDto buildingDto, Graph graph) {
        List<SpaceNodeRosonDto> spaceNodes = buildingDto.getSpaceNodes();
        spaceNodes.forEach(spaceNode -> {
            String spaceId = spaceNode.getSpaceId();
            String nodeId = spaceNode.getNodeId();
            graph.addEdge(undirectedEdgeName(spaceId, nodeId), spaceId, nodeId);
        });
    }

    private static void addSpaceWallEdges(RosonBuildingDto buildingDto, Graph graph) {
        List<SpaceWallRosonDto> spaceWalls = buildingDto.getSpaceWalls();
        spaceWalls.forEach(spaceWall -> {
            String spaceId = spaceWall.getSpaceId();
            String wallId = spaceWall.getWallId();
            graph.addEdge(undirectedEdgeName(spaceId, wallId), spaceId, wallId);
        });
    }
}
