package com.tuzhucheng.multichat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;

public class ConnectionManager extends Thread {
    private static final int PORT = 4545;

    private ServerSocket serverSocket;
    private MessageQueue messageQueue;

    public ConnectionManager(MessageQueue messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server listening on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientCommunicator communicator = new ClientCommunicator(socket, messageQueue);
                ClientInfo clientInfo = new ClientInfo(communicator);
                communicator.start();
                messageQueue.addClient(clientInfo);
            }
        } catch (SocketException e) {
            // SocketException is thrown when serverSocket is closed. This is normal when we shut down the server.
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            System.out.println("ConnectionManager thread ended.");
        }
    }

    public void stopListening() {
        try {
            messageQueue.closeClientCommunicatorInputStreams();
            messageQueue.waitForAllClientInputStreamClose();
            messageQueue.addToSendMessage("CLOSE_SOCKET_INPUT");
            messageQueue.waitForAllClientOutputStreamClose();

            System.out.println("Closing client sockets.");
            for (ClientInfo info : messageQueue.getClientList()) {
                // Close the socket
                Socket socket = info.getCommunicator().getSocket();
                if (!socket.isClosed()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Failed to close client socket.");
                    }
                } else {
                    System.err.println("Client socket is already closed.");
                }
            }

            if (serverSocket != null) {
                serverSocket.close();
                System.out.println("Closed server socket.");
            }

            messageQueue.shutdown();
        } catch (IOException e) {
            System.err.println("Failed to close socket");
        }
    }

}
