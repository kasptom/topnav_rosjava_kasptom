package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.RosonBuildingDto;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.stream.Collectors;

public class RosonParser {
    public RosonBuildingDto parse(String rosonFilePath) throws FileNotFoundException {
        String fullPath = Objects.requireNonNull(RosonParser.class.getClassLoader().getResource(rosonFilePath)).getPath();
        return parseFullPathFile(fullPath);
    }

    public RosonBuildingDto parseFullPathFile(String fullPath) throws FileNotFoundException {
        Gson gson = new Gson();
        FileReader reader = new FileReader(fullPath);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String json = bufferedReader.lines().collect(Collectors.joining());
        return gson.fromJson(json, RosonBuildingDto.class);
    }
}
