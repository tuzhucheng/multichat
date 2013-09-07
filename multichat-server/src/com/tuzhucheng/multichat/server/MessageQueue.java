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
    private LinkedList<ClientInfo> clientList;
    private boolean shutdownTriggered;

    public MessageQueue() {
        clientList = new LinkedList<ClientInfo>();
    }

    synchronized public void addToSendMessage(String message) {
        toSendList.add(message);
        notifyAll();
    }

    synchronized public List<String> retrieveToSendMessages(int index) throws InterruptedException {
        while (toSendList.size() == index) {
            if (!shutdownTriggered)
                wait();
            else
                throw new InterruptedException();
        }

        if (index < toSendList.size()) {
            return toSendList.subList(index, toSendList.size());
        } else {
            System.err.println("Index is " + index + ". Send List size is " + toSendList.size());
            return null;
        }
    }

    synchronized public String pollReceivedMessage() throws InterruptedException {
        while(receivedMessagesList.isEmpty()) {
            if (!shutdownTriggered)
                wait();
            else
                throw new InterruptedException();
        }
        return receivedMessagesList.poll();
    }

    synchronized public void addReceivedMessage(String message) {
        receivedMessagesList.push(message);
        notifyAll();
    }

    public void addClient(ClientInfo info) {
        clientList.add(info);
    }

    synchronized public LinkedList<ClientInfo> getClientList() {
        return clientList;
    }

    synchronized public void changeClientInfoStatus(int port, String newStatus) {
        for (ClientInfo info : clientList) {
            if (info.getPort() == port) {
                info.setStatus(newStatus);
                break;
            }
        }
        notifyAll();
    }

    public void closeClientCommunicatorInputStreams() {
        for (ClientInfo info : clientList) {
            info.getCommunicator().closeInputStream();
        }
    }

    private boolean allClientInputStreamClosed() {
        for (ClientInfo info : clientList) {
            if (!info.getStatus().equals(ClientInfo.STATE_CLOSED_INPUTSTREAM))
                return false;
        }
        return true;
    }

    private boolean allClientOutputStreamClosed() {
        for (ClientInfo info : clientList) {
            if (!info.getStatus().equals(ClientInfo.STATE_CLOSED_OUTPUTSTREAM))
                return false;
        }
        return true;
    }

    synchronized public void waitForAllClientInputStreamClose() {
        while (!allClientInputStreamClosed()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();
    }

    synchronized public void waitForAllClientOutputStreamClose() {
        while (!allClientOutputStreamClosed()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        notifyAll();
    }

    synchronized public void shutdown() {
        shutdownTriggered = true;
        notifyAll();
    }

}
