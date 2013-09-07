package com.tuzhucheng.multichat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * The ClientCommunicator is responsible for reading and writing data to the socket that it holds.
 * Specifically, ClientCommunicator itself reads data that comes from the socket and the inner class
 * @see Sender writes data to the socket.
 * */

public class ClientCommunicator extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private MessageQueue messageQueue;
    private Sender sender;

    public ClientCommunicator(Socket socket, MessageQueue messageQueue) {
        System.out.println("Connected to new client at port " + socket.getPort() + ". Local port is " + socket.getLocalPort());
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
                if (receivedMessage != null)
                    messageQueue.addReceivedMessage(receivedMessage);
                else
                    break;
            }
        } catch (SocketException e) {
            // Typically this means that the socket has been closed
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("ClientCommunicator thread ended.");
    }

    public void closeInputStream() {
        if (!socket.isInputShutdown()) {
            try {
                socket.shutdownInput();
                messageQueue.changeClientInfoStatus(socket.getPort(), ClientInfo.STATE_CLOSED_INPUTSTREAM);
            } catch (IOException e) {
                System.err.println("Failed to close socket input stream.");
                e.printStackTrace();
            }
        }
    }

    public void closeOutputStream() {
        if(!socket.isOutputShutdown()) {
            try {
                socket.shutdownOutput();
            } catch (IOException e) {
                System.err.println("Failed to close socket output stream.");
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }

    private class Sender extends Thread {

        private int toSendMessageIndex;

        @Override
        public void run() {
            while (true) {
                try {
                    List<String> messages = messageQueue.retrieveToSendMessages(toSendMessageIndex);
                    if (messages != null) {
                        boolean exit = false;
                        for (String message : messages) {
                            out.println(message);
                            toSendMessageIndex++;
                            if (message.equals("CLOSE_SOCKET_INPUT")) {
                                closeOutputStream();
                                messageQueue.changeClientInfoStatus(socket.getPort(), ClientInfo.STATE_CLOSED_OUTPUTSTREAM);
                                exit = true;
                                break;
                            }
                        }
                        if (exit) break;
                    } else {
                        System.err.println("Null messages!");
                        break;
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }

            System.out.println("ClientCommunicator.Sender thread stopped.");
        }
    }
}
