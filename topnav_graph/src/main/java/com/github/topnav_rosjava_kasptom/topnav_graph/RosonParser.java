package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.BuildingDto;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.stream.Collectors;

public class RosonParser {
    public BuildingDto parse(String rosonFilePath) throws FileNotFoundException {
        Gson gson = new Gson();
        String fullPath = Objects.requireNonNull(RosonParser.class.getClassLoader().getResource(rosonFilePath)).getPath();
        FileReader reader = new FileReader(fullPath);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String json = bufferedReader.lines().collect(Collectors.joining());
        return gson.fromJson(json, BuildingDto.class);
    }
}
