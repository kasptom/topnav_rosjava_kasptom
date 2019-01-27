package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.BuildingDto;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

public class WorkspaceGraph {

    private Graph graph;

    public WorkspaceGraph(BuildingDto buildingDto) {
        graph = new SingleGraph("Building");
        buildGraph(buildingDto);
    }

    public void showGraph() {
        this.graph.display();
    }

    private void buildGraph(BuildingDto buildingDto) {
        buildingDto.getMarkers().forEach(marker -> graph.addNode(marker.getId()));


        graph.forEach(node -> node.addAttribute("ui.label", node.getId()));
    }
}
