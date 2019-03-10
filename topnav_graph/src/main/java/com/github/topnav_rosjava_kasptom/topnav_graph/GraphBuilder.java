package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeKindException;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.BaseIdentifiableDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.NodeDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.PointDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.NodeNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.SpaceGateRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.SpaceNodeRosonDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.SpaceWallRosonDto;
import org.graphstream.graph.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_CLASS;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_LABEL;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants.ROSON_NODE_KIND;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.TOPNAV_ATTRIBUTE_KEY_COST;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.TOPNAV_ATTRIBUTE_KEY_NODE_TYPE;

class GraphBuilder {
    static synchronized void buildGraph(RosonBuildingDto buildingDto, Graph graph) throws InvalidRosonNodeKindException {
        buildingDto.getNodes().forEach(node -> addRosonNode(node, graph));
        buildingDto.getWalls().forEach(wall -> addWall(wall, graph));
        buildingDto.getGates().forEach(gate -> addWall(gate, graph));

        addNodeNodeEdges(buildingDto, graph);

        for (NodeDto node : buildingDto.getNodes()) {
            if (node.getKind().equals(RosonConstants.NodeKind.SPACE_NODE)) {
                addEdgesBetweenNeighbouringTopologies(getOrderedTopologyIdsForSpace(buildingDto, node), graph);
            }
        }
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

        if (rosonNode.getKind().equals(RosonConstants.NodeKind.GATE_NODE)) {
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

        HashSet<String> gateNodeIds = buildingDto.getNodes()
                .stream()
                .filter(node -> node.getKind().equals(RosonConstants.NodeKind.GATE_NODE))
                .map(BaseIdentifiableDto::getId)
                .collect(Collectors.toCollection(HashSet::new));

        List<NodeNodeRosonDto> nodeNodes = buildingDto.getNodeNodes()
                .stream()
                .filter(nodeNode -> !isEdgeWith(markerNodeIds, nodeNode) && !isEdgeWith(gateNodeIds, nodeNode))
                .collect(Collectors.toList());

        nodeNodes
                .forEach(nodeNode -> {
                    String fromId = nodeNode.getNodeFromId();
                    String toId = nodeNode.getNodeToId();
                    Edge edge = graph.addEdge(directedEdgeName(fromId, toId), fromId, toId, true);
                    edge.addAttribute(TOPNAV_ATTRIBUTE_KEY_COST, 1.0);
                });
    }

    private static boolean isEdgeWith(HashSet<String> nodeIds, NodeNodeRosonDto nodeNode) {
        return nodeIds.contains(nodeNode.getNodeFromId())
                || nodeIds.contains(nodeNode.getNodeToId());
    }

    private static String directedEdgeName(String fromNodeId, String toNodeId) {
        return String.format("%s -> %s", fromNodeId, toNodeId);
    }

    private static String undirectedEdgeName(String nodeId, String otherNodeId) {
        return nodeId.compareTo(otherNodeId) < 0
                ? String.format("%s -- %s", nodeId, otherNodeId)
                : String.format("%s -- %s", otherNodeId, nodeId);
    }

    private static void addEdgesBetweenNeighbouringTopologies(List<String> sortedTopologies, Graph graph) {
        for (int i = 1; i < sortedTopologies.size(); i++) {
            String prevTopologyId = sortedTopologies.get(i - 1);
            String topologyId = sortedTopologies.get(i);
            try {
                graph.addEdge(undirectedEdgeName(prevTopologyId, topologyId), prevTopologyId, topologyId);
            } catch (EdgeRejectedException | IdAlreadyInUseException exc) {
                System.out.printf("Edge %s %s already exists\n", prevTopologyId, topologyId);
            }
        }

        String prevTopologyId = sortedTopologies.get(sortedTopologies.size() - 1);
        String topologyId = sortedTopologies.get(0);
        try {
            graph.addEdge(undirectedEdgeName(prevTopologyId, topologyId), prevTopologyId, topologyId);
        } catch (EdgeRejectedException | IdAlreadyInUseException exc) {
            System.out.printf("Edge %s %s already exists\n", prevTopologyId, topologyId);
        }
    }

    private static void positionNodeAt(Node node, double x, double y) {
        node.addAttribute("layout.frozen");
        node.addAttribute("xy", x, y);
    }

    /**
     * @param buildingDto
     * @param node
     * @return
     * @throws InvalidRosonNodeKindException
     */
    private static List<String> getOrderedTopologyIdsForSpace(RosonBuildingDto buildingDto, NodeDto node) throws InvalidRosonNodeKindException {
        if (!node.getKind().equals(RosonConstants.NodeKind.SPACE_NODE)) {
            throw new InvalidRosonNodeKindException(RosonConstants.NodeKind.SPACE_NODE, node.getKind());
        }

        String spaceId = getSpaceId(buildingDto, node);

        HashSet<String> spaceWallIds = buildingDto.getSpaceWalls()
                .stream()
                .filter(spaceWall -> spaceWall.getSpaceId().equals(spaceId))
                .map(SpaceWallRosonDto::getWallId)
                .collect(Collectors.toCollection(HashSet::new));

        HashSet<String> spaceGateIds = buildingDto.getSpaceGates()
                .stream()
                .filter(spaceGate -> spaceGate.getSpaceId().equals(spaceId))
                .map(SpaceGateRosonDto::getGateId)
                .collect(Collectors.toCollection(HashSet::new));

        List<BaseIdentifiableDto> spaceTopologies = new ArrayList<>();

        List<BaseIdentifiableDto> gates = buildingDto.getGates()
                .stream()
                .filter(gate -> spaceGateIds.contains(gate.getId()))
                .collect(Collectors.toList());

        List<BaseIdentifiableDto> walls = buildingDto.getWalls()
                .stream()
                .filter(wall -> spaceWallIds.contains(wall.getId()))
                .collect(Collectors.toList());

        spaceTopologies.addAll(gates);
        spaceTopologies.addAll(walls);

        spaceTopologies.sort(new TopologicalComparator(node));

        return spaceTopologies.stream().map(BaseIdentifiableDto::getId).collect(Collectors.toList());
    }

    private static String getSpaceId(RosonBuildingDto buildingDto, NodeDto node) {
        return buildingDto.getSpaceNodes()
                .stream()
                .filter(spaceNode -> spaceNode.getNodeId().equals(node.getId()))
                .map(SpaceNodeRosonDto::getSpaceId)
                .findFirst()
                .orElse(null);
    }
}
