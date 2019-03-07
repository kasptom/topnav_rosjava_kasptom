package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;

import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        RosonParser parser = new RosonParser();

        if (args.length == 0) {
            System.err.println("Please specify the path to the *.roson file");
            return;
        }

        RosonBuildingDto buildingDto = parser.parse(args[0]);

        WorkspaceGraph workspaceGraph = new WorkspaceGraph(buildingDto);
        workspaceGraph.showGraph();
    }
}
