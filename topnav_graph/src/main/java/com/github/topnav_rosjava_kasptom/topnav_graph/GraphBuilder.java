package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.BaseIdentifiableDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.NodeDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.PointDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.NodeNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.SpaceWallRosonDto;
import org.graphstream.graph.*;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_CLASS;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_LABEL;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants.ROSON_NODE_KIND;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.TOPNAV_ATTRIBUTE_KEY_COST;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.TOPNAV_ATTRIBUTE_KEY_NODE_TYPE;

class GraphBuilder {
    static synchronized void buildGraph(RosonBuildingDto buildingDto, Graph graph) {
        buildingDto.getNodes().forEach(node -> addRosonNode(node, graph));
        buildingDto.getWalls().forEach(wall -> addWall(wall, graph));

        addNodeNodeEdges(buildingDto, graph);
        addWallWallEdges(buildingDto, graph);
    }

    private static void addWall(BaseIdentifiableDto wall, Graph graph) {
        Node node = addIdentifiable(wall.getId(), wall.getType(), graph);
        PointDto average = PointDto.getAverage(wall.getFrom(), wall.getTo());
        positionNodeAt(node, average.getX(), average.getY());
    }

    private static Node addIdentifiable(String id, String type, Graph graph) {
        Node node = graph.addNode(id);
        node.addAttribute(GS_UI_LABEL, id);
        node.addAttribute(GS_UI_CLASS, type);

        node.addAttribute(TOPNAV_ATTRIBUTE_KEY_NODE_TYPE, type);
        return node;
    }

    private static void addRosonNode(NodeDto rosonNode, Graph graph) {
        if (rosonNode.getKind().equals(RosonConstants.NodeKind.MARKER_NODE)) {
            return;
        }

        Node node = graph.addNode(rosonNode.getId());
        node.addAttribute(GS_UI_LABEL, rosonNode.getId());
        node.addAttribute(GS_UI_CLASS, rosonNode.getType(), rosonNode.getKind());

        node.addAttribute(ROSON_NODE_KIND, rosonNode.getKind());
        node.addAttribute(TOPNAV_ATTRIBUTE_KEY_NODE_TYPE, rosonNode.getKind());

        positionNodeAt(node, rosonNode.getPosition().getX(), rosonNode.getPosition().getY());
    }

    private static void addNodeNodeEdges(RosonBuildingDto buildingDto, Graph graph) {
        HashSet<String> markerNodeIds = buildingDto.getNodes()
                .stream()
                .filter(node -> node.getKind().equals(RosonConstants.NodeKind.MARKER_NODE))
                .map(BaseIdentifiableDto::getId)
                .collect(Collectors.toCollection(HashSet::new));

        List<NodeNodeRosonDto> nodeNodes = buildingDto.getNodeNodes()
                .stream()
                .filter(nodeNode -> isNodeMarkerNodeEdge(markerNodeIds, nodeNode))
                .collect(Collectors.toList());

        nodeNodes
                .forEach(nodeNode -> {
                    String fromId = nodeNode.getNodeFromId();
                    String toId = nodeNode.getNodeToId();
                    Edge edge = graph.addEdge(directedEdgeName(fromId, toId), fromId, toId, true);
                    edge.addAttribute(TOPNAV_ATTRIBUTE_KEY_COST, 1.0);
                });
    }

    private static boolean isNodeMarkerNodeEdge(HashSet<String> markerNodeIds, NodeNodeRosonDto nodeNode) {
        return !markerNodeIds.contains(nodeNode.getNodeFromId())
                && !markerNodeIds.contains(nodeNode.getNodeToId());
    }

    private static String directedEdgeName(String fromNodeId, String toNodeId) {
        return String.format("%s -> %s", fromNodeId, toNodeId);
    }

    private static String undirectedEdgeName(String nodeId, String otherNodeId) {
        return nodeId.compareTo(otherNodeId) < 0
                ? String.format("%s -- %s", nodeId, otherNodeId)
                : String.format("%s -- %s", otherNodeId, nodeId);
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

    private static void positionNodeAt(Node node, double x, double y) {
        node.addAttribute("layout.frozen");
        node.addAttribute("xy", x, y);
    }
}
