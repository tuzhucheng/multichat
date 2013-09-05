package com.tuzhucheng.multichat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionManager extends Thread {
    private static int PORT = 4545;

    private ServerSocket serverSocket;
    private ArrayList<ClientCommunicator> clientCommunicators;
    private MessageQueue messageQueue;

    public ConnectionManager(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
        clientCommunicators = new ArrayList<ClientCommunicator>();
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientCommunicator communicator = new ClientCommunicator(socket, messageQueue);
                clientCommunicators.add(communicator);
                communicator.start();
            }
        } catch (IOException e) {
            return;
        } finally {
            System.out.println("ConnectionManager thread ended.");
        }
    }

    public void stopListening() {
        try {
            for (ClientCommunicator communicator : clientCommunicators) {
                communicator.getSocket().close();
                communicator.stopClientCommunicator();
            }
            if (serverSocket != null)
                serverSocket.close();
        } catch (IOException e) {
            System.err.println("Failed to close socket");
        }
    }

}
