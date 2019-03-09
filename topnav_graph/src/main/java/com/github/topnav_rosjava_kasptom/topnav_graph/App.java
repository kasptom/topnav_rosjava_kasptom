package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class App {

    public static void main(String[] args) throws IOException {
        RosonParser parser = new RosonParser();

        if (args.length == 0) {
            System.err.println("Please specify the path to the *.roson file");
            return;
        }

        RosonBuildingDto buildingDto = parser.parse(args[0]);

        TopologicalNavigator navigator = new TopologicalNavigator(buildingDto);
        navigator.showGraph();

        String firstMarkerId, secondMarkerId;


        try {
            if (args.length != 3) {
                Scanner scanner = new Scanner(System.in);

                System.out.println("Please input the first marker id");
                firstMarkerId = Integer.toString(scanner.nextInt());

                System.out.println("Please input the second marker id");
                secondMarkerId = Integer.toString(scanner.nextInt());
            } else {
                firstMarkerId = args[1];
                secondMarkerId = args[2];
            }

            navigator.createGuidelines(firstMarkerId, secondMarkerId);
        } catch (InvalidArUcoIdException e) {
            System.out.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Invalid ArUco ids");
        }
    }
}
