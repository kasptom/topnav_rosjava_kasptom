package com.github.topnav_rosjava_kasptom.topnav_graph.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * https://github.com/graphstream/gs-core/issues/65
 */
public class StyleConverter {

    private static Pattern classpathURLPattern = Pattern.compile("'file:([^']*)'");

    public static String convert(String styleSheetPath) throws IOException {
        File file = new File(styleSheetPath);
        byte[] bytes = new byte[(int) file.length()];
        FileInputStream in = new FileInputStream(file);
        in.read(bytes);
        in.close();
        String input = new String(bytes);
        Matcher matcher = classpathURLPattern.matcher(input);
        while (matcher.find()) {
            String resource = matcher.group(1);
            String fullPath = ResourceUtils.getFullPath(resource);
            //System.out.println("converted url: " + url.toString());
            input = input.replace(matcher.group(), "'" + fullPath + "'");
            matcher.reset(input);
        }
        return input;
    }
}