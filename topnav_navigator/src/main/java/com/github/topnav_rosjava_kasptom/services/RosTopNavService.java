package com.github.topnav_rosjava_kasptom.services;

import com.github.topnav_rosjava_kasptom.topnav_shared.constants.DrivingStrategy;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Feedback;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.RelativeDirection;
import com.github.topnav_rosjava_kasptom.topnav_shared.model.Topology;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.ros.exception.RosRuntimeException;
import org.ros.internal.loader.CommandLineLoader;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;
import std_msgs.Float64;
import topnav_msgs.FeedbackMsg;
import topnav_msgs.GuidelineMsg;

import java.util.List;
import java.util.stream.Collectors;

public class RosTopNavService implements IRosTopnavService {

    private static IRosTopnavService instance;

    private boolean isInitialized;
    private boolean isDestroyed;

    private NavigationNode navigationNode;
    private OnFeedbackChangeListener listener;

    public static IRosTopnavService getInstance() {
        if (instance == null) {
            instance = new RosTopNavService();
        }
        return instance;
    }

    @Override
    public void onInit() {
        if (isInitialized) {
            return;
        }

        isInitialized = true;

        CommandLineLoader loader = new CommandLineLoader(Lists.newArrayList(NavigationNode.class.getCanonicalName()));
        String nodeClassName = loader.getNodeClassName();
        System.out.println("Loading node class: " + loader.getNodeClassName());
        NodeConfiguration nodeConfiguration = loader.build();

        try {
            navigationNode = (NavigationNode) loader.loadClass(nodeClassName);
        } catch (ClassNotFoundException classNotFoundExc) {
            throw new RosRuntimeException("Unable to locate node: " + nodeClassName, classNotFoundExc);
        } catch (InstantiationException | IllegalAccessException exception) {
            throw new RosRuntimeException("Unable to instantiate node: " + nodeClassName, exception);
        }

        Preconditions.checkState(navigationNode != null);
        NodeMainExecutor nodeMainExecutor = DefaultNodeMainExecutor.newDefault();
        nodeMainExecutor.execute(navigationNode, nodeConfiguration);

        navigationNode.setFeedbackMessageListener(this::convertAndPassToListener);
    }

    @Override
    public void onDestroy() {
        if (isDestroyed) {
            return;
        }
        isDestroyed = true;
        //
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

    @Override
    public void changeCameraDirection(RelativeDirection relativeDirection) {
        Publisher<Float64> publisher = navigationNode.getCameraDirectionPublisher();

        double rotation;

        switch (relativeDirection) {
            case AHEAD:
                rotation = 0.0;
                break;
            case AT_LEFT:
                rotation = Math.PI / 2;
                break;
            case AT_RIGHT:
                rotation = -Math.PI / 2;
                break;
            default:
                rotation = 0.0;
                break;
        }

        Float64 message = publisher.newMessage();
        message.setData(rotation);
        publisher.publish(message);
    }

    @Override
    public void setOnFeedbackChangeListener(OnFeedbackChangeListener listener) {
        this.listener = listener;
    }

    private RosTopNavService() {}

    private void convertAndPassToListener(FeedbackMsg feedbackMsg) {
        if (listener != null) {
            List<Topology> topologies = feedbackMsg.getTopologies()
                    .stream()
                    .map(msg -> new Topology(
                            msg.getTimestamp().totalNsecs(),
                            msg.getIdentity(),
                            msg.getRelativeAlignment(),
                            msg.getRelativeDirection(),
                            msg.getRelativeDistance()))
                    .collect(Collectors.toList());
            Feedback feedback = new Feedback(feedbackMsg.getTimestamp().totalNsecs(), topologies);
            listener.onFeedbackChange(feedback);
        }
    }
}
