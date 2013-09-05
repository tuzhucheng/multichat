package com.tuzhucheng.multichat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * The ClientCommunicator is responsible for reading and writing data to the socket that it holds.
 * Specifically, ClientCommunicator itself reads data that comes from the socket and the inner class
 * @see Sender writes data to the socket.
 * */

public class ClientCommunicator extends Thread {

    private Socket socket;
    BufferedReader in;
    PrintWriter out;
    MessageQueue messageQueue;
    Sender sender;
    int toSendMessageIndex = 0;

    public ClientCommunicator(Socket socket, MessageQueue messageQueue) {
        System.out.println("Connected to new client at port " + socket.getPort() + ".");
        this.messageQueue = messageQueue;
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        sender = new Sender();
        sender.start();

        try {
            while (true) {
                String receivedMessage = in.readLine();
                messageQueue.addReceivedMessage(receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }  finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.println("ClientCommunicator BufferedReader failed to close.");
                }
            }
        }

        System.out.println("ClientCommunicator thread ended.");
    }

    public void stopClientCommunicator() {
        if (in != null) {
            try {
                in.close();
                sender.interrupt();
            } catch (IOException e) {
                System.err.println("ClientCommunicator BufferedReader failed to close.");
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    private class Sender extends Thread {

        @Override
        public void run() {
            while (true) {
                List<String> messages = messageQueue.retrieveToSendMessages(toSendMessageIndex);
                if (messages != null) {
                    for (String message : messages) {
                        out.println(message);
                        toSendMessageIndex++;
                    }
                } else {
                    System.err.println("Null messages!");
                    break;
                }

                if (isInterrupted()) {
                    break;
                }
            }
            System.out.println("ClientCommunicator.Sender thread stopped.");
        }
    }
}
