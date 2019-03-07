package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import org.ros.node.topic.SubscriberListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Class created in order to not unsubscribe subscribers not registered by the current instance
 * @param <T> type of the message (ROS topic)
 */
public class TopnavSubscriber<T> implements Subscriber<T> {
    private final Subscriber<T> subscriber;
    private List<MessageListener<T>> messageListeners;

    public TopnavSubscriber(ConnectedNode connectedNode, String topicName, String messageType) {
        subscriber = connectedNode.newSubscriber(topicName, messageType);
        messageListeners = new ArrayList<>();
    }

    @Override
    public void addMessageListener(MessageListener<T> messageListener, int i) {
        subscriber.addMessageListener(messageListener, i);
    }

    @Override
    public void addMessageListener(MessageListener<T> messageListener) {
        messageListeners.add(messageListener);
        subscriber.addMessageListener(messageListener);
    }

    @Override
    public boolean removeMessageListener(MessageListener<T> messageListener) {
        messageListeners.remove(messageListener);
        return subscriber.removeMessageListener(messageListener);
    }

    @Override
    public void removeAllMessageListeners() {
        subscriber.removeAllMessageListeners();
    }

    /**
     * Removes only the subscribers registered by the current instance of TopnavSubscriber class
     * Use {@link TopnavSubscriber#removeAllMessageListeners()} in order to remove all of the listeners
     */
    public void removeAllLocalMessageListeners() {
        messageListeners.forEach(subscriber::removeMessageListener);
        messageListeners.clear();
    }

    @Override
    public void shutdown(long time, TimeUnit timeUnit) {
        subscriber.shutdown(time, timeUnit);
    }

    @Override
    public void shutdown() {
        subscriber.shutdown();
    }

    @Override
    public void addSubscriberListener(SubscriberListener<T> subscriberListener) {
        subscriber.addSubscriberListener(subscriberListener);
    }

    @Override
    public boolean getLatchMode() {
        return subscriber.getLatchMode();
    }

    @Override
    public GraphName getTopicName() {
        return subscriber.getTopicName();
    }

    @Override
    public String getTopicMessageType() {
        return subscriber.getTopicMessageType();
    }
}
