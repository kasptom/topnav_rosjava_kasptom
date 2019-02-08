package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.BuildingDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.ResourceUtils;
import com.github.topnav_rosjava_kasptom.topnav_graph.utils.StyleConverter;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.io.IOException;

public class WorkspaceGraph {

    private Graph graph;
    private static final String RENDERER_KEY = "org.graphstream.ui.renderer";
    private static final String RENDERER_NAME = "org.graphstream.ui.j2dviewer.J2DGraphRenderer";
    private static final String CUSTOM_NODE_STYLE = "css/stylesheet.css";


    public WorkspaceGraph(BuildingDto buildingDto) throws IOException {
        System.setProperty(RENDERER_KEY, RENDERER_NAME);
        graph = new SingleGraph("Building graph (roson)");
        graph.addAttribute("ui.stylesheet", StyleConverter.convert(ResourceUtils.getFullPath(CUSTOM_NODE_STYLE)));
        buildGraph(buildingDto);
    }

    public void showGraph() {
        this.graph.display();
    }

    private void buildGraph(BuildingDto buildingDto) {
        buildingDto.getMarkers().forEach(marker -> graph.addNode(marker.getId()));

        graph.forEach(node -> {
            node.addAttribute("ui.label", node.getId());
            node.addAttribute("ui.class", "marker");
        });
    }
}
