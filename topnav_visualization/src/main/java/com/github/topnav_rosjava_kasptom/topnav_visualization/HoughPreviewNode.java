/*
 * Copyright (C) 2014 kasptom.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.topnav_rosjava_kasptom.topnav_visualization;

import org.apache.commons.logging.Log;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import sensor_msgs.LaserScan;
import topnav_msgs.HoughAcc;

import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.GAZEBO_LASER_SCAN_TOPIC;
import static com.github.topnav_rosjava_kasptom.topnav_shared.constants.TopicNames.TOPNAV_HOUGH_TOPIC;

public class HoughPreviewNode extends AbstractNodeMain {

    private Subscriber<HoughAcc> houghAccSubscriber;
    private Subscriber<LaserScan> hokuyoSubscriber;
    private HoughPreview preview;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("laser_scan_java_preview");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        preview = new HoughPreview();

        houghAccSubscriber = connectedNode.newSubscriber(TOPNAV_HOUGH_TOPIC, HoughAcc._TYPE);
        hokuyoSubscriber = connectedNode.newSubscriber(GAZEBO_LASER_SCAN_TOPIC, LaserScan._TYPE);

        final Log log = connectedNode.getLog();

//        houghAccSubscriber.addMessageListener(message -> log.info("I heard: \"" + message.getData() + "\""));
        hokuyoSubscriber.addMessageListener(message -> preview.onLaserPointsUpdated(message));
    }
}
