package com.github.topnav_rosjava_kasptom.topnav_graph;

import com.github.topnav_rosjava_kasptom.topnav_graph.model.BaseIdentifiableDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.NodeDto;
import com.github.topnav_rosjava_kasptom.topnav_graph.model.PointDto;

import java.util.Comparator;

public class TopologicalComparator implements Comparator<BaseIdentifiableDto> {
    private final PointDto spaceCenter;

    TopologicalComparator(NodeDto rosonNode) {
        spaceCenter = rosonNode.getPosition();
    }

    @Override
    public int compare(BaseIdentifiableDto prev, BaseIdentifiableDto next) {
        double prevAngle = computeAngle(prev, spaceCenter);
        double nextAngle = computeAngle(next, spaceCenter);
        return Double.compare(prevAngle, nextAngle);
    }

    private double computeAngle(BaseIdentifiableDto node, PointDto spaceCenter) {
        PointDto nodeCenter = node.getPosition();
        nodeCenter = nodeCenter == null
                ? PointDto.getAverage(node.getFrom(), node.getTo())
                : nodeCenter;

        double height = nodeCenter.getY() - spaceCenter.getY();
        double width = nodeCenter.getX() - spaceCenter.getX() ;

        if (width == 0) return Math.signum(height) * Math.PI / 2;

        return Math.atan2(height, width);
    }
}

