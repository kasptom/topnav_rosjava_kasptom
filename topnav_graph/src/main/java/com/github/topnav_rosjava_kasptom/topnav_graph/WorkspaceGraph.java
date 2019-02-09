package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.constants.RosonConstants;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.BaseIdentifiableDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.ResourceUtils;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.StyleConverter;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;

public class WorkspaceGraph {

    private Graph graph;
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

        graph.getNodeSet()
                .stream()
                .filter(node -> "marker".equals(node.getAttribute(RosonConstants.METADATA_TYPE)))
                .forEach(markerNode -> {
                    markerNode.addAttribute("ui.label", markerNode.getId());
                    markerNode.addAttribute("ui.class", "marker");
                });
    }

    private void addGraphNode(BaseIdentifiableDto rosonNode) {
        Node node = graph.addNode(rosonNode.getId());
        node.addAttribute("ui.label", rosonNode.getId());
        node.addAttribute("ui.class", rosonNode.getType());

        node.addAttribute(RosonConstants.METADATA_TYPE, rosonNode.getType());
    }
}
