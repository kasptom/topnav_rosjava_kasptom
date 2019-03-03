package com.github.rosjava.topnav_rosjava_kasptom.topnav_driving_strategies.controllers;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import org.ros.node.topic.SubscriberListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    public void removeAllLocalMessageListeners() {
        messageListeners.forEach(subscriber::removeMessageListener);
        messageListeners.clear();
    }

    @Override
    public void shutdown(long l, TimeUnit timeUnit) {
        subscriber.shutdown(l, timeUnit);
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
