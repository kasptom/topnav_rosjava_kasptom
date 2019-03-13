package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeKindException;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.BaseIdentifiableDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.NodeDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.PointDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.marker.MarkerDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.*;
import org.graphstream.graph.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_CLASS;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.GS_UI_LABEL;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants.ROSON_NODE_KIND;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.*;
import static java.util.stream.Collectors.toCollection;

class GraphBuilder {
    static synchronized void buildGraph(RosonBuildingDto buildingDto, Graph graph) throws InvalidRosonNodeKindException, InvalidRosonNodeIdException {
        buildingDto.getNodes().forEach(node -> addRosonNode(node, graph));
        buildingDto.getWalls().forEach(wall -> addWall(wall, graph));
        buildingDto.getGates().forEach(gate -> addWall(gate, graph));

        addMarkers(buildingDto, graph);
        addNodeNodeEdges(buildingDto, graph);
        addGateNodeEdges(buildingDto, graph);
        addWallNodesPerSpaceAndWallNodeEdges(buildingDto, graph);

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
        node.addAttribute(GS_UI_LABEL, shortLabelFromId(id));
        node.addAttribute(GS_UI_CLASS, type);

        node.addAttribute(TOPNAV_ATTRIBUTE_KEY_NODE_TYPE, type);
        return node;
    }

    private static void addRosonNode(NodeDto rosonNode, Graph graph) {
        if (rosonNode.getKind().equals(RosonConstants.NodeKind.MARKER_NODE)) {
            return;
        }

        Node node = graph.addNode(rosonNode.getId());
        node.addAttribute(GS_UI_LABEL, shortLabelFromId(rosonNode.getId()));
        node.addAttribute(GS_UI_CLASS, rosonNode.getType(), rosonNode.getKind());

        node.addAttribute(ROSON_NODE_KIND, rosonNode.getKind());
        node.addAttribute(TOPNAV_ATTRIBUTE_KEY_NODE_TYPE, rosonNode.getKind());

        if (rosonNode.getKind().equals(RosonConstants.NodeKind.GATE_NODE)) {
            return;
        }
        positionNodeAt(node, rosonNode.getPosition().getX(), rosonNode.getPosition().getY());
    }

    /**
     * Leaves only the capital letters and digits from the id
     * @param longIdName long version of the label
     * @return the shortened id
     */
    private static String shortLabelFromId(String longIdName) {
        return longIdName.replaceAll("[a-z]", "");
    }

    private static void addNodeNodeEdges(RosonBuildingDto buildingDto, Graph graph) {
        HashSet<String> markerNodeIds = buildingDto.getNodes()
                .stream()
                .filter(node -> node.getKind().equals(RosonConstants.NodeKind.MARKER_NODE))
                .map(BaseIdentifiableDto::getId)
                .collect(toCollection(HashSet::new));

        List<NodeNodeRosonDto> nodeNodes = buildingDto.getNodeNodes()
                .stream()
                .filter(nodeNode -> !isEdgeWith(markerNodeIds, nodeNode))
                .collect(Collectors.toList());

        HashSet<String> spaceNodeIds = buildingDto.getNodes()
                .stream()
                .filter(node -> RosonConstants.NodeKind.SPACE_NODE.equals(node.getKind()))
                .map(BaseIdentifiableDto::getId)
                .collect(toCollection(HashSet::new));


        nodeNodes
                .forEach(nodeNode -> {
                    String fromId = nodeNode.getNodeFromId();

                    if (spaceNodeIds.contains(fromId)) {
                        return;
                    }

                    String toId = nodeNode.getNodeToId();
                    Edge edge = graph.addEdge(directedEdgeName(fromId, toId), fromId, toId, true);
                    edge.addAttribute(TOPNAV_ATTRIBUTE_KEY_COST, 1.0);
                });
    }

    private static void addGateNodeEdges(RosonBuildingDto buildingDto, Graph graph) {
        List<GateNodeRosonDto> gateNodes = buildingDto.getGateNodes();
        gateNodes.forEach(gateNode -> {
            String gateId = gateNode.getGateId();
            String nodeId = gateNode.getNodeId();
            graph.addEdge(directedEdgeName(gateId, nodeId), gateId, nodeId, true);
        });
    }

    /**
     * nodeWalls does not exist in the roson format had
     *
     * @param buildingDto
     * @param graph
     */
    private static void addWallNodesPerSpaceAndWallNodeEdges(RosonBuildingDto buildingDto, Graph graph) {

        HashSet<String> spaceNodeIds = buildingDto.getNodes()
                .stream()
                .filter(node -> RosonConstants.NodeKind.SPACE_NODE.equals(node.getKind()))
                .map(BaseIdentifiableDto::getId)
                .collect(toCollection(HashSet::new));

        List<SpaceWallRosonDto> spaceWalls = buildingDto.getSpaceWalls();

        HashMap<String, String> spaceNodeIdToSpaceId = new HashMap<>();
        buildingDto.getSpaceNodes()
                .stream()
                .filter(spaceNode -> spaceNodeIds.contains(spaceNode.getNodeId()))
                .forEach(spaceNode -> spaceNodeIdToSpaceId.put(spaceNode.getNodeId(), spaceNode.getSpaceId()));

        spaceNodeIds.forEach(spaceNodeId -> {
            String spaceId = spaceNodeIdToSpaceId.get(spaceNodeId);
            List<SpaceWallRosonDto> wallsFromSpace = spaceWalls
                    .stream()
                    .filter(spaceWall -> spaceWall.getSpaceId().equals(spaceId))
                    .collect(Collectors.toList());

            wallsFromSpace.forEach(wallFromSpace -> {
                String wallNodeId = wallFromSpace.getWallId() + wallFromSpace.getSpaceId();
                Node wallNode = graph.addNode(wallNodeId);
                wallNode.addAttribute(GS_UI_LABEL, shortLabelFromId(wallNodeId));
                graph.addEdge(directedEdgeName(wallNodeId, spaceNodeId), wallNodeId, spaceNodeId, true);
                graph.addEdge(directedEdgeName(wallFromSpace.getWallId(), wallNodeId), wallFromSpace.getWallId(), wallNodeId, true);
            });
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
        node.addAttribute("xy", x, -y);
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
                .collect(toCollection(HashSet::new));

        HashSet<String> spaceGateIds = buildingDto.getSpaceGates()
                .stream()
                .filter(spaceGate -> spaceGate.getSpaceId().equals(spaceId))
                .map(SpaceGateRosonDto::getGateId)
                .collect(toCollection(HashSet::new));

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

    private static void addMarkers(RosonBuildingDto buildingDto, Graph graph) throws InvalidRosonNodeIdException {
        List<MarkerDto> markers = buildingDto.getMarkers();

        if (markers == null) {
            System.out.println("Roson file has no markers(!)");
            return;
        }

        for (MarkerDto marker : markers) {
            addMarkerToGraph(marker, graph);
            attachMarkerToParentNode(marker, buildingDto, graph);
        }
    }

    private static void addMarkerToGraph(MarkerDto marker, Graph graph) {
        Node node = graph.addNode(marker.getAruco().getId());
        node.addAttribute(GS_UI_LABEL, shortLabelFromId(marker.getLabel()));
        node.addAttribute(GS_UI_CLASS, marker.getType());
    }

    private static void attachMarkerToParentNode(MarkerDto marker, RosonBuildingDto buildingDto, Graph graph) throws InvalidRosonNodeIdException {
        MarkerNodeRosonDto markerNodeToAttach = buildingDto
                .getMarkerNodes()
                .stream()
                .filter(markerNode -> markerNode.getMarkerId().equals(marker.getId()))
                .findFirst()
                .orElseThrow(() -> new InvalidRosonNodeIdException(marker.getId()));

        NodeNodeRosonDto markerNodeToParentNode = buildingDto.getNodeNodes()
                .stream()
                .filter(nodeNode -> nodeNode.getNodeFromId().equals(markerNodeToAttach.getNodeId()))
                .findFirst()
                .orElseThrow(() -> new InvalidRosonNodeIdException(markerNodeToAttach.getNodeId()));

        NodeDto parentNode = buildingDto.getNodes()
                .stream()
                .filter(node -> node.getId().equals(markerNodeToParentNode.getNodeToId()))
                .findFirst()
                .orElseThrow(() -> new InvalidRosonNodeIdException(markerNodeToParentNode.getNodeToId()));

        Node parentGraphNode = graph.getNode(parentNode.getId());

        if (!parentGraphNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS)) {
            parentGraphNode.addAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS, new ArrayList<MarkerDto>());
        }

        List<MarkerDto> parentNodeMarkers = parentGraphNode.getAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
        parentNodeMarkers.add(marker);

        graph.addEdge(directedEdgeName(marker.getAruco().getId(), parentNode.getId()), marker.getAruco().getId(), parentNode.getId(), true);
    }
}
