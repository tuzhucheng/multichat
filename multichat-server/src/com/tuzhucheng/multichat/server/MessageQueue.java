package com.tuzhucheng.multichat.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents shared data that different threads will access.
 * */

public class MessageQueue {

    private ArrayList<String> toSendList = new ArrayList<String>();
    private LinkedList<String> receivedMessagesList = new LinkedList<String>();

    synchronized public void addToSendMessage(String message) {
        toSendList.add(message);
        notifyAll();
    }

    synchronized public List<String> retrieveToSendMessages(int index) {
        while (index == toSendList.size()) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }

        if (index < toSendList.size()) {
            return toSendList.subList(index, toSendList.size());
        } else {
            System.err.println("Index is " + index + ". Send List size is " + toSendList.size());
            return null;
        }
    }

    synchronized public String pollReceivedMessage() {
        while(receivedMessagesList.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {

            }
        }
        return receivedMessagesList.poll();
    }

    synchronized public void addReceivedMessage(String message) {
        receivedMessagesList.push(message);
        notifyAll();
    }

}
