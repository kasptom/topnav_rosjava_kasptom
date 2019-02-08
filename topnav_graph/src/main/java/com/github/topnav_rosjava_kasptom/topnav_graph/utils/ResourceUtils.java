package com.github.topnav_rosjava_kasptom.topnav_graph.utils;

import java.util.Objects;

public class ResourceUtils {
    public static String getUrl(String customNodeStyle) {
        String fullPath = getFullPath(customNodeStyle);
        return String.format("url('file:%s')", fullPath);
    }

    public static String getFullPath(String customNodeStyle) {
        return Objects.requireNonNull(ResourceUtils.class.getClassLoader().getResource(customNodeStyle)).getPath();
    }
}
