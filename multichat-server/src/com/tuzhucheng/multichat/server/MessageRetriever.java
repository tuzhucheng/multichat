package com.tuzhucheng.multichat.server;

/**
 * This class retrieves messages received from clients, displays them to the screen,
 * and add them to a queue so that other clients can receive the message sent by a certain client.
 */
public class MessageRetriever extends Thread {

    private MessageQueue messageQueue;

    public MessageRetriever(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String receivedMessage = messageQueue.pollReceivedMessage();
                if (receivedMessage == null) {
                    System.err.println("Received null message. Terminating.");
                    break;
                } else {
                    System.out.println(receivedMessage);
                    messageQueue.addToSendMessage(receivedMessage);
                }
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("MessageRetriever ended.");
    }

}
