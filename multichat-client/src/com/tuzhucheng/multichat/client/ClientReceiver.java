package com.tuzhucheng.multichat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * A class that receives messages from the server and prints them to the screen
 */
public class ClientReceiver extends Thread {

    Socket clientSocket;
    BufferedReader socketIn;
    String line;

    public ClientReceiver(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        try {
            socketIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (true) {
                line = socketIn.readLine();
                if (line != null)
                    System.out.println(line);
                else
                    break;
            }

            System.out.println("ClientReceiver thread ended.");
        } catch (IOException e) {
            System.err.println("Caught IOException");
            e.printStackTrace();
            return;
        }

    }
}
