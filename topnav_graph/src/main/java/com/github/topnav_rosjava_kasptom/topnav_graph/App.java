package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.BuildingDto;

import java.io.FileNotFoundException;

public class App {
    public static void main(String[] args) throws FileNotFoundException {
        RosonParser parser = new RosonParser();
        BuildingDto buildingDto = parser.parse(args[0]);

        WorkspaceGraph workspaceGraph = new WorkspaceGraph(buildingDto);
        workspaceGraph.showGraph();
    }
}
