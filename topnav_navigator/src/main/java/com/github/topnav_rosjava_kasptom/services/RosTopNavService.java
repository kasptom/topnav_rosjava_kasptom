package com.github.topnav_rosjava_kasptom.services;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.ros.exception.RosRuntimeException;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;
import topnav_msgs.GuidelineMsg;

public class RosTopNavService implements IRosTopnavService {

    private NavigationNode navigationNode;

    public RosTopNavService() {
    }

    @Override
    public void onInit() {
        CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(NavigationNode.class.getCanonicalName()));
        String nodeClassName = loader.getNodeClassName();
        System.out.println("Loading node class: " + loader.getNodeClassName());
        NodeConfiguration nodeConfiguration = loader.build();

        try {
            navigationNode = (NavigationNode) loader.loadClass(nodeClassName);
        } catch (ClassNotFoundException var6) {
            throw new RosRuntimeException("Unable to locate node: " + nodeClassName, var6);
        } catch (InstantiationException | IllegalAccessException var7) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, var7);
        }

        Preconditions.checkState(navigationNode != null);
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(navigationNode, nodeConfiguration);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void startStrategy(String strategyName) {
        Publisher<GuidelineMsg> publisher = navigationNode.getGuidelinePublisher();
        GuidelineMsg message = publisher.newMessage();

        message.setGuidelineType(strategyName);

        publisher.publish(message);
    }

    @Override
    public void stopStrategy(String strategyName) {
        Publisher<GuidelineMsg> publisher = navigationNode.getGuidelinePublisher();
        GuidelineMsg message = publisher.newMessage();

        message.setGuidelineType(DrivingStrategy.DRIVING_STRATEGY_IDLE);

        publisher.publish(message);
    }
}