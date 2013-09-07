package com.tuzhucheng.multichat.client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Main {

    private Socket clientSocket;
    public PrintWriter out;
    public BufferedReader userInput;
    private ClientReceiver clientReceiver;

    private static final int SERVER_PORT = 4545;

    private String line;

    public static void main(String[] args) {
	    Main uiSender = new Main();
        uiSender.go();
    }

    private void go() {
        try {
            clientSocket = new Socket("localhost", SERVER_PORT);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            clientReceiver = new ClientReceiver(this, clientSocket);
            userInput = new BufferedReader(new InputStreamReader(System.in));

            clientReceiver.start();

            while(true) {
                line = userInput.readLine();
                if (!clientSocket.isClosed()) {
                    if (line != null) {
                        out.println(line);
                        if(line.equals("END")) {
                            clientReceiver.interrupt();
                            break;
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }

        } catch (SocketException e) {
            // If ClientReceiver receives END message from server, it will terminate the socket
            // to notify the main thread that it should end too
            System.out.println("Main thread socketexception");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Failed to close.");
            }
        }

        System.out.println("Main thread ended");
    }
}
