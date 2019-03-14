package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidArUcoIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeIdException;
import com.github.topnav_rosjava_kasptom.topnav_graph.exceptions.InvalidRosonNodeKindException;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.BaseIdentifiableDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.NodeDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.PointDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.marker.MarkerDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.rosonRelation.*;
import org.graphstream.graph.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.GraphStreamConstants.*;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants.ROSON_NODE_KIND;
import static com.github.topnav_rosjava_kasptom.topnav_graph.constants.TopNavConstants.*;
import static java.util.stream.Collectors.toCollection;

class GraphBuilder {

    private static HashMap<String, String> spaceIdToSpaceNodeId;


    static synchronized void buildGraph(RosonBuildingDto buildingDto, Graph graph) throws InvalidRosonNodeKindException, InvalidRosonNodeIdException, InvalidArUcoIdException {
        spaceIdToSpaceNodeId = createSpaceIdToSpaceNodeIdMap(buildingDto);

        buildingDto.getNodes().forEach(node -> addRosonNode(node, graph));
        buildingDto.getWalls().forEach(wall -> addWallAndTopologyNode(wall, buildingDto, graph));
        buildingDto.getGates().forEach(gate -> addGateAngTopologyNode(gate, buildingDto, graph));

        addMarkers(buildingDto, graph);
        //addNodeNodeEdges(buildingDto, graph);

//        addGateNodeEdges(buildingDto, graph);
//        addWallNodesPerSpaceAndWallNodeEdges(buildingDto, graph);

        for (NodeDto node : buildingDto.getNodes()) {
            if (node.getKind().equals(RosonConstants.NodeKind.SPACE_NODE)) {
                addEdgesBetweenNeighbouringTopologies(getOrderedTopologyIdsForSpace(buildingDto, node, graph), graph);
            }
        }
    }

    /**
     * Space123 -> Node12 (kind == spaceNode)
     *
     * @param buildingDto roson
     * @return map from SpaceId to NodeId
     */
    private static HashMap<String, String> createSpaceIdToSpaceNodeIdMap(RosonBuildingDto buildingDto) {
        HashSet<String> spaceRosonNodeIds = buildingDto.getNodes()
                .stream()
                .filter(rosonNode -> RosonConstants.NodeKind.SPACE_NODE.equals(rosonNode.getKind()))
                .map(BaseIdentifiableDto::getId)
                .collect(Collectors.toCollection(HashSet::new));

        List<SpaceNodeRosonDto> spaceNodes = buildingDto.getSpaceNodes()
                .stream()
                .filter(spaceNode -> spaceRosonNodeIds.contains(spaceNode.getNodeId()))
                .collect(Collectors.toList());

        HashMap<String, String> spaceIdToSpaceNodeId = new HashMap<>();
        spaceNodes.forEach(spaceNode -> {
            spaceIdToSpaceNodeId.put(spaceNode.getSpaceId(), spaceNode.getNodeId());
        });
        return spaceIdToSpaceNodeId;
    }

    private static void addWallAndTopologyNode(BaseIdentifiableDto wall, RosonBuildingDto buildingDto, Graph graph) {
        Node node = addIdentifiable(wall.getId(), wall.getType(), graph);

        List<SpaceWallRosonDto> spacesContainingWall = buildingDto.getSpaceWalls()
                .stream()
                .filter(spaceNode -> spaceNode.getWallId().equals(wall.getId()))
                .collect(Collectors.toList());

        spacesContainingWall.forEach(spaceNode -> {
            String topologyId = spaceNode.getWallId() + spaceNode.getSpaceId();
            Node topologyNode = graph.addNode(topologyId);
            topologyNode.addAttribute(GS_UI_LABEL, shortLabelFromId(topologyId));
            topologyNode.addAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE, TOPNAV_ATTRIBUTE_VALUE_TOPOLOGY_TYPE_WALL);

            graph.addEdge(directedEdgeName(wall.getId(), topologyId), wall.getId(), topologyId, true);

            String spaceNodeId = spaceIdToSpaceNodeId.get(spaceNode.getSpaceId());
            graph.addEdge(directedEdgeName(topologyId, spaceNodeId), topologyId, spaceNodeId, true);
        });

        PointDto average = PointDto.getAverage(wall.getFrom(), wall.getTo());
        positionNodeAt(node, average.getX(), average.getY());
    }

    private static void addGateAngTopologyNode(BaseIdentifiableDto gate, RosonBuildingDto buildingDto, Graph graph) {
        Node node = addIdentifiable(gate.getId(), gate.getType(), graph);

        List<SpaceGateRosonDto> spacesContainingGate = buildingDto.getSpaceGates()
                .stream()
                .filter(spaceNode -> spaceNode.getGateId().equals(gate.getId()))
                .collect(Collectors.toList());

        spacesContainingGate.forEach(spaceNode -> {
            String topologyId = spaceNode.getGateId() + spaceNode.getSpaceId();
            Node topologyNode = graph.addNode(topologyId);
            topologyNode.addAttribute(GS_UI_LABEL, shortLabelFromId(topologyId));
            topologyNode.addAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE, TOPNAV_ATTRIBUTE_VALUE_TOPOLOGY_TYPE_GATE);

            graph.addEdge(undirectedEdgeName(topologyId, gate.getId()), topologyId, gate.getId());

            String spaceNodeId = spaceIdToSpaceNodeId.get(spaceNode.getSpaceId());
            graph.addEdge(directedEdgeName(topologyId, spaceNodeId), topologyId, spaceNodeId, true);
        });

        PointDto average = PointDto.getAverage(gate.getFrom(), gate.getTo());
        positionNodeAt(node, average.getX(), average.getY());
    }

    private static Node addIdentifiable(String id, String rosonType, Graph graph) {
        Node node = graph.addNode(id);
        node.addAttribute(GS_UI_LABEL, shortLabelFromId(id));
        node.addAttribute(GS_UI_CLASS, rosonType);

        node.addAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE, rosonType);
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
        node.addAttribute(GS_UI_LABEL, shortLabelFromId(rosonNode.getId()));
        node.addAttribute(GS_UI_CLASS, rosonNode.getType(), rosonNode.getKind());

        if (rosonNode.getKind().equals(RosonConstants.NodeKind.SPACE_NODE)) {
            System.out.printf("adding space node %s\n", rosonNode.getId());
        }

        node.addAttribute(ROSON_NODE_KIND, rosonNode.getKind());
        node.addAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE, rosonNode.getKind());

        positionNodeAt(node, rosonNode.getPosition().getX(), rosonNode.getPosition().getY());
    }

    /**
     * Leaves only the capital letters and digits from the id
     *
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

//    private static void addGateNodeEdges(RosonBuildingDto buildingDto, Graph graph) {
//        List<GateNodeRosonDto> gateNodes = buildingDto.getGateNodes();
//        gateNodes.forEach(gateNode -> {
//            String gateId = gateNode.getGateId();
//            String nodeId = gateNode.getNodeId();
//            graph.addEdge(directedEdgeName(gateId, nodeId), gateId, nodeId, true);
//        });
//    }

//    /**
//     * nodeWalls does not exist in the roson format had
//     *
//     * @param buildingDto
//     * @param graph
//     */
//    private static void addWallNodesPerSpaceAndWallNodeEdges(RosonBuildingDto buildingDto, Graph graph) {
//
//        HashSet<String> spaceNodeIds = buildingDto.getNodes()
//                .stream()
//                .filter(node -> RosonConstants.NodeKind.SPACE_NODE.equals(node.getKind()))
//                .map(BaseIdentifiableDto::getId)
//                .collect(toCollection(HashSet::new));
//
//        List<SpaceWallRosonDto> spaceWalls = buildingDto.getSpaceWalls();
//
//        HashMap<String, String> spaceNodeIdToSpaceId = new HashMap<>();
//        buildingDto.getSpaceNodes()
//                .stream()
//                .filter(spaceNode -> spaceNodeIds.contains(spaceNode.getNodeId()))
//                .forEach(spaceNode -> spaceNodeIdToSpaceId.put(spaceNode.getNodeId(), spaceNode.getSpaceId()));
//
//        spaceNodeIds.forEach(spaceNodeId -> {
//            String spaceId = spaceNodeIdToSpaceId.get(spaceNodeId);
//            List<SpaceWallRosonDto> wallsFromSpace = spaceWalls
//                    .stream()
//                    .filter(spaceWall -> spaceWall.getSpaceId().equals(spaceId))
//                    .collect(Collectors.toList());
//
//            wallsFromSpace.forEach(wallFromSpace -> {
//                String wallNodeId = getWallNodeId(wallFromSpace);
//
//                Node wallNode = graph.addNode(wallNodeId);
//                wallNode.addAttribute(GS_UI_LABEL, shortLabelFromId(wallNodeId));
//                wallNode.addAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE, TOPNAV_ATTRIBUTE_VALUE_TOPOLOGY_TYPE_WALL);
//
//                graph.addEdge(directedEdgeName(wallNodeId, spaceNodeId), wallNodeId, spaceNodeId, true);
//                graph.addEdge(directedEdgeName(wallFromSpace.getWallId(), wallNodeId), wallFromSpace.getWallId(), wallNodeId, true);
//            });
//        });
//
//    }

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
        node.addAttribute(GS_UI_LAYOUT_FROZEN);
        node.addAttribute(GS_UI_NODE_POSITION, GS_UI_SCALE * x, -GS_UI_SCALE * y);
    }

    /**
     * @param buildingDto
     * @param spaceRosonNode
     * @param graph
     * @return
     * @throws InvalidRosonNodeKindException
     */
    private static List<String> getOrderedTopologyIdsForSpace(RosonBuildingDto buildingDto, NodeDto spaceRosonNode, Graph graph) throws InvalidRosonNodeKindException {
        if (!spaceRosonNode.getKind().equals(RosonConstants.NodeKind.SPACE_NODE)) {
            throw new InvalidRosonNodeKindException(RosonConstants.NodeKind.SPACE_NODE, spaceRosonNode.getKind());
        }

        String spaceId = getSpaceId(buildingDto, spaceRosonNode);

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

        List<BaseIdentifiableDto> rosonIdentifiables = new ArrayList<>();

        List<BaseIdentifiableDto> gates = buildingDto.getGates()
                .stream()
                .filter(gate -> spaceGateIds.contains(gate.getId()))
                .collect(Collectors.toList());

        List<BaseIdentifiableDto> walls = buildingDto.getWalls()
                .stream()
                .filter(wall -> spaceWallIds.contains(wall.getId()))
                .collect(Collectors.toList());

        rosonIdentifiables.addAll(gates);
        rosonIdentifiables.addAll(walls);

        rosonIdentifiables.sort(new TopologicalComparator(spaceRosonNode));

        return rosonIdentifiables.stream()
                .map(node -> {
                    String rosonId = node.getId();
                    Node graphNode = graph.getNode(rosonId);

                    List<Node> neighbours = new ArrayList<>();

                    Iterator<Node> neigbourIterator = graphNode.getNeighborNodeIterator();
                    while (neigbourIterator.hasNext()) {
                        neighbours.add(neigbourIterator.next());
                    }

                    Node topologyNode = neighbours.stream()
                            .filter(neighbour -> neighbour.hasAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE)
                                    && (neighbour.getId().contains(spaceId) || RosonConstants.NodeKind.GATE_NODE.equals(node.getType())))
                            .findFirst()
                            .orElse(null);

                    if (topologyNode == null) {
                        throw new RuntimeException("Node has no expected topology child note");
                    }

                    return topologyNode.getId();
                })
                .collect(Collectors.toList());
    }

    private static String getSpaceId(RosonBuildingDto buildingDto, NodeDto node) {
        return buildingDto.getSpaceNodes()
                .stream()
                .filter(spaceNode -> spaceNode.getNodeId().equals(node.getId()))
                .map(SpaceNodeRosonDto::getSpaceId)
                .findFirst()
                .orElse(null);
    }

    private static void addMarkers(RosonBuildingDto buildingDto, Graph graph) throws InvalidRosonNodeIdException, InvalidArUcoIdException {
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

    private static void attachMarkerToParentNode(MarkerDto marker, RosonBuildingDto buildingDto, Graph graph) throws InvalidRosonNodeIdException, InvalidArUcoIdException {
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

        if (parentNode.getKind().equals(RosonConstants.NodeKind.GATE_NODE)) {
            GateNodeRosonDto markerGateNode = buildingDto.getGateNodes()
                    .stream()
                    .filter(gateNode -> marker.getAttachedToNodeId().equals(gateNode.getNodeId()))
                    .findFirst()
                    .orElseThrow(() -> new InvalidArUcoIdException("Could nod find gate node for ArUco", marker.getAruco().getId()));

            Node gateGraphNode = graph.getNode(markerGateNode.getGateId());

            List<Node> neighbours = new ArrayList<>();
            Iterator<Node> neighbourIterator = gateGraphNode.getNeighborNodeIterator();
            while (neighbourIterator.hasNext()) {
                neighbours.add(neighbourIterator.next());
            }

            for (Node neighbour : neighbours) {
                if (!neighbour.hasAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE)) {
                    continue;
                }

                Edge spaceEdge = neighbour.getEdgeSet()
                        .stream()
                        .filter(edge -> {
                            Node node0 = edge.getNode0();
                            Node node1 = edge.getNode1();
                            return isMarkerSpaceNode(node0, marker.getSpaceId()) || isMarkerSpaceNode(node1, marker.getSpaceId());
                        })
                        .findFirst()
                        .orElse(null);

                if (spaceEdge == null) {
//                    throw new InvalidArUcoIdException("Could not find the space edge from topology node aruco id: %s", marker.getAruco().getId());
                    System.out.printf("No edges found for ArUco %s abd neighbour %s of %s\n", marker.getAruco().getId(), neighbour.getId(), markerGateNode.getNodeId());
                    continue;
                }

                Node spaceNode = spaceEdge.getNode0().equals(neighbour)
                        ? spaceEdge.getNode1()
                        : spaceEdge.getNode0();

                if (!spaceNode.getId().equals(spaceIdToSpaceNodeId.get(marker.getSpaceId()))) {
                    continue;
                }

                if (!neighbour.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS)) {
                    neighbour.addAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS, new ArrayList<MarkerDto>());
                }

                List<MarkerDto> topologyMarkers = neighbour.getAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
                topologyMarkers.add(marker);
                graph.addEdge(directedEdgeName(marker.getAruco().getId(), neighbour.getId()), marker.getAruco().getId(), neighbour.getId(), true);
            }
//             TODO
//            neighbours.stream()
//                    .filter(neighbour -> neighbour.hasAttribute(TOPNAV_ATTRIBUTE_KEY_TOPOLOGY_TYPE)
//                    && neighbour.)
        }

//        Node parentGraphNode = graph.getNode(parentNode.getId());
//
//        if (!parentGraphNode.hasAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS)) {
//            parentGraphNode.addAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS, new ArrayList<MarkerDto>());
//        }
//
//        List<MarkerDto> parentNodeMarkers = parentGraphNode.getAttribute(TOPNAV_ATTRIBUTE_KEY_MARKERS);
//        parentNodeMarkers.add(marker);
//
    }

    private static boolean isMarkerSpaceNode(Node edgeNode, String spaceId) {
        return edgeNode.hasAttribute(ROSON_NODE_KIND)
                && edgeNode.getAttribute(ROSON_NODE_KIND).equals(RosonConstants.NodeKind.SPACE_NODE)
                && edgeNode.getId().equals(spaceIdToSpaceNodeId.get(spaceId));
    }
}
