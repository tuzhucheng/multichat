package com.tuzhucheng.multichat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The Main class for the server
 * */

public class Main {

    private MessageQueue messageQueue;
    private ConnectionManager connectionManager;
    private BufferedReader inputStream;
    private MessageRetriever messageRetriever;

    public static void main(String[] args) {
        Main uiThread = new Main();
        uiThread.go();
        System.out.println("Main thread ended");
    }

    private void go() {
        System.out.println("multichat server");
        messageQueue = new MessageQueue();
        connectionManager = new ConnectionManager(messageQueue);
        messageRetriever = new MessageRetriever(messageQueue);
        connectionManager.start();
        messageRetriever.start();

        inputStream = new BufferedReader(new InputStreamReader(System.in));
        String line;
        do {
            try {
                line = inputStream.readLine();
                messageQueue.addToSendMessage(line);
                if (line.equals("END")) {
                    connectionManager.stopListening();
                    messageRetriever.interrupt();
                    break;
                }
            } catch (IOException e) {
                return;
            }
        } while (line != null);

    }
}
